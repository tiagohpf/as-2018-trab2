/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 *
 * @author kanto
 */
public class MonitorThread extends Thread {

    private final Socket socket;
    private final JTextArea j;
    private final int id;
    private PrintWriter out;
    private BufferedReader in;

    public MonitorThread(Socket socket, JTextArea j, int id) {
        this.socket = socket;
        this.j = j;
        this.id = id;
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
                String text = in.readLine();
                // null message?
                if (text == null) {
                    // end of communication with this client
                    j.append("Server " + id + " is down!\n");
                    return;
                }
                j.append("Server " + id + " is " + text + "\n");
            }
        } catch (Exception e) {
        }
    }
}
