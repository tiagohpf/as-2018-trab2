package Server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 * This class implements the thread responsible for
 * dealing with the new incoming clients
 * @author Óscar Pereira
 */
class ThreadEcho extends Thread {

    private final Socket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private JTextArea j;

    // constructo receives the socket
    public ThreadEcho(Socket socket,JTextArea j) {
        this.socket = socket;
        this.j = j;
    }
    @Override
    public void run() {
        try {
            // socket´s output stream
            out = new PrintWriter(socket.getOutputStream(), true);
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
                j.append("Server received a new message: " + text+"\n" );
                // random sleep to induce a processing delay
                sleep( (int) (Math.random() * 10) * 1000);
                // send echo to client
                out.println("my echo (" + text + ")");
            }
            // close everything
            socket.close();
            out.close();
            in.close();
        } catch (IOException | InterruptedException ex) {}
    }
}
