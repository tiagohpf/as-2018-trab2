/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ServerManagement.ServerInfo;
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
 * @author kanto
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
            host = socket.getLocalAddress();
            rl.lock();
            try {
                boolean flag = false;
                for (int i = 0; i < servers.size(); i++) {
                    if (servers.get(i).getId() == id) {
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    servers.add(new ServerInfo(id, port, host));
                }

            } catch (Exception e) {

            } finally {
                rl.unlock();
            }
            while (true) {
                // wait for a message from the client
                String text = in.readLine();
                // null message?
                if (text == null) {
                    // end of communication with this client
                    j.append("Server " + id + " is down!\n");
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
                            servers.remove(index);
                        }
                    } catch (Exception e) {

                    } finally {
                        rl.unlock();
                    }
                    return;
                }
                j.append("Server " + id + " is " + text + "\n");
                System.out.println(servers.toString());
            }
        } catch (Exception e) {
        }
    }
}
