package ServerManagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JTextArea;

/**
 * This class implements the thread responsible for dealing with the new
 * incoming clients
 *
 * @author Óscar Pereira
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

    // constructo receives the socket
    public WorkDistributionThread(Socket socket, JTextArea j, int id, ArrayList<ServerInfo> servers, ReentrantLock rl) {
        this.socket = socket;
        this.j = j;
        this.id = id;
        this.servers = servers;
        this.rl = rl;
    }
    
    public void allocateToServer(String text) throws IOException {
        try {
            int i = 0;
            rl.lock();
            try {
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
                    out.println("Servers are busy!");
                    return;
                }
            } catch (Exception e) {
                
            } finally {
                rl.unlock();
            }
            
            j.append("Client " + id + " allocated on server " + (i + 1) + "\n");
            servers.get(i).incrementThreadId();
            mySocket = new Socket(servers.get(i).getHost(), servers.get(i).getPort());
            servers.get(i).incrementRequests();
            // socket's output stream
            server_out = new PrintWriter(mySocket.getOutputStream(), true);
            // socket's input stream
            server_in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            String[] values = text.split(" ");
            server_out.println(values[0]);
            server_out.println(values[1]);
            result = server_in.readLine();
            server_out.close();
            server_in.close();
            mySocket.close();
            servers.get(i).decrementRequests();
            if (result == null) {
                //out.println("Erro");
                j.append("Reallocating request because server is down!\n");
                allocateToServer(text);
            } else {
                out.println("Result " + result);
            }
        } catch (UnknownHostException e) {
            j.append("Don't know about server host\n");
        } catch (IOException e) {
            j.append("Couldn't get I/O for the connection to server host\n");
            out.println("Error\n");
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
            while (true) {
                // wait for a message from the client
                j.append("Thread is waiting for a new message\n");
                String text = in.readLine();
                // null message?
                if (text == null) {
                    // end of communication with this client
                    System.out.println("End of communication");
                    break;
                }
                j.append("Server received a new message: " + text + "\n");
                
                j.append(servers.toString() + "\n");

                //connect to socket of the server
                allocateToServer(text);
            }
            // close everything
            socket.close();
            out.close();
            in.close();
        } catch (Exception e) {
        }
    }
}
