package ServerManagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JTextArea;

/**
 * This class implements the thread responsible for dealing with the new
 * incoming clients
 *
 * @author José Santos
 * @author Tiago Faria
 */
public class WorkDistributionThread extends Thread {

    private final Socket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private PrintWriter server_out = null;
    private BufferedReader server_in = null;
    private JTextArea j;
    private int id;
    private ArrayList<ServerInfo> servers;
    private ReentrantLock rl;
    private String result;
    private Socket mySocket;
    private String requestId;
    private String[] values;
    private Condition down_notify;
    private ArrayList<ServerInfo> down;
    private int i;

    public WorkDistributionThread(Socket socket, JTextArea j, int id, ArrayList<ServerInfo> servers,
            ReentrantLock rl, ArrayList<ServerInfo> down, Condition downnotify) {
        this.socket = socket;
        this.j = j;
        this.id = id;
        this.servers = servers;
        this.rl = rl;
        this.down = down;
        this.down_notify = downnotify;
    }

    public void allocateToServer() throws IOException, InterruptedException {
        try {
            j.append("Inside Allocate!");
            rl.lock();
            try {
                for (ServerInfo server : servers) {
                    j.append(server.toString() + "\n");
                }
                if (servers.isEmpty()) {
                    j.append("Result: " + id + " | " + requestId + " | 02 | "
                            + values[0] + " | " + values[1] + "\n");
                    j.append("Rejecting request " + requestId + " from client " + id + "\n");
                    out.println("Result " + id + " | " + requestId + "| 02 | " + values[0] + " | " + values[1]);
                    return;
                }
                i = 0;
                boolean flag = false;
                for (int j = 0; j < servers.size(); j++) {
                    if (servers.get(j).getActive_requests() < servers.get(j).getSize() || servers.get(j).getSize() == 0) {
                        if (servers.get(j).getActive_requests() <= servers.get(i).getActive_requests()) {
                            i = j;
                            flag = true;
                        }
                    }
                }
                if (!flag) {
                    j.append("Result: " + id + " | " + requestId + " | 02 | "
                            + values[0] + " | " + values[1] + "\n");
                    j.append("Rejecting request " + requestId + " from client " + id + "\n");
                    out.println("Result " + id + " | " + requestId + "| 02 | " + values[0] + " | " + values[1]);
                    return;
                }
            } catch (Exception e) {

            } finally {
                rl.unlock();
            }

            rl.lock();
            try {
                servers.get(i).incrementThreadId();
                j.append("Client " + id + " allocated on server " + servers.get(i).getId()
                        + " on thread " + servers.get(i).getThreadId() + "\n");
                mySocket = new Socket(servers.get(i).getHost(), servers.get(i).getPort());
                servers.get(i).incrementRequests();
            } finally {
                rl.unlock();
            }
            // socket's output stream
            server_out = new PrintWriter(mySocket.getOutputStream(), true);
            // socket's input stream
            server_in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            server_out.println(values[0]);
            server_out.println(values[1]);
            server_out.println(id);
            server_out.println(requestId);

            Thread t = new Thread() {
                public void run() {
                    rl.lock();
                    try {
                        while (down.isEmpty()) {
                            down_notify.await();
                        }
                        for (ServerInfo s : down) {
                            //System.out.println(s.getId() + " " + servers.get(i).getId() + " " + (i + 1));
                            if (s.getId() == servers.get(i).getId() - 1) {
                                s.decrementRequests();
                                Thread reallocate = new Thread() {

                                    public void run() {
                                        try {
                                            allocateToServer();
                                        } catch (Exception e) {

                                        }
                                    }
                                };
                                reallocate.start();
                            }
                        }

                        boolean flag = true;
                        for (ServerInfo s : down) {
                            if (s.getActive_requests() != 0) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            j.append("All requests reallocated!\n");
                            down.clear();
                        }
                    } catch (Exception e) {
                    } finally {
                        rl.unlock();
                    }
                }
            };
            t.start();

            result = server_in.readLine();

            server_out.close();
            server_in.close();
            mySocket.close();

            if (result != null) {
                rl.lock();
                try {
                    servers.get(i).decrementRequests();
                } finally {
                    rl.unlock();
                }
                out.println("Result " + result);
            }
        } catch (UnknownHostException e) {
            j.append("Don't know about server host\n");
        } catch (IOException e) {
            j.append("Couldn't get I/O for the connection to server host\n");
        }
    }

    @Override
    public void run() {
        try {
            // socket´s output stream
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(id);
            // socket's input stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            j.append("Client " + id + " is connected\n");
            while (true) {
                // wait for a message from the client
                //j.append("Thread is waiting for a new message\n");
                String text = in.readLine();
                requestId = in.readLine();
                // null message?
                if (text == null) {
                    // End of communication with this client
                    System.out.println("End of communication");
                    break;
                }
                values = text.split(" ");
                j.append("Server received a new request: " + id + " | " + requestId + " | 01 |"
                        + values[0] + " | " + values[1] + "\n");
                //connect to socket of the server
                allocateToServer();
            }
            // close everything
            socket.close();
            out.close();
            in.close();
        } catch (Exception e) {
        }
    }
}
