package ServerManagement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JTextArea;

/**
 *
 * @author José Santos
 * @author Tiago Faria
 */
public class MonitorThread extends Thread {

    private final Socket socket;
    private final JTextArea j;
    private final int id;
    private PrintWriter out;
    private BufferedReader in;
    private ArrayList<ServerInfo> servers;
    private ReentrantLock rl;
    private int port;
    private InetAddress host;

    public MonitorThread(Socket socket, JTextArea j, int id, ArrayList<ServerInfo> servers, ReentrantLock rl) {
        this.socket = socket;
        this.j = j;
        this.id = id;
        this.servers = servers;
        this.rl = rl;
    }

    @Override
    public void run() {
        try {
            // socket´s output stream
            out = new PrintWriter(socket.getOutputStream(), true);
            // socket's input stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(id);
            port = Integer.parseInt(in.readLine());
            int size = Integer.parseInt(in.readLine());
            host = socket.getLocalAddress();
            rl.lock();
            try {
                servers.add(new ServerInfo(id, port, host, size));
                j.append("Server " + id + " is connected\n");
            } catch (Exception e) {

            } finally {
                rl.unlock();
            }
            while (true) {
                // wait for a message from the client
                String text = in.readLine();
                if (text != null) {
                    j.append("Server " + id + " is " + text + "\n");
                    rl.lock();
                    try {
                        int index = -1;
                        for (int i = 0; i < servers.size(); i++) {
                            if (servers.get(i).getId() == id) {
                                index = i;
                                break;
                            }
                        }
                        if (index != -1) {
                            servers.get(index).addMessage(text);
                        }
                    } catch (Exception e) {

                    } finally {
                        rl.unlock();
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
