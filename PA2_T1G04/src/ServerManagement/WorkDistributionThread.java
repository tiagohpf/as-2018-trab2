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
                try {
                    // create a socket
                    mySocket = new Socket("localhost", servers.get(0).getPort());
                    // socket's output stream
                    server_out = new PrintWriter(mySocket.getOutputStream(), true);
                    // socket's input stream
                    server_in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                    String[] values = text.split(" ");
                    server_out.println(values[0]);
                    server_out.println(values[1]);
                } catch (UnknownHostException e) {
                    j.append("Don't know about server host\n");
                } catch (IOException e) {
                    j.append("Couldn't get I/O for the connection to server host\n");
                    out.println("Error\n");
                }
                result = server_in.readLine();
                if (result == null) {
                    out.println("Erro");
                } else {
                    out.println(result);
                }
            }
            // close everything
            socket.close();
            out.close();
            in.close();
        } catch (Exception e) {
        }
    }
}
