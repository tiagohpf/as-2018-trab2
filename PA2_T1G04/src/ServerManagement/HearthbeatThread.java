/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerManagement;

import Server.Server;
import java.io.BufferedReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author kanto
 */
public class HearthbeatThread extends Thread {

    private final Socket socket;
    private final JTextArea j;
    private final int id;
    private PrintWriter out;
    private BufferedReader in;
    private ArrayList<ServerInfo> servers;
    private ArrayList<ServerInfo> down;
    private ReentrantLock rl;
    private Condition downnotify;
    private int port;
    private InetAddress host;
    private int index;

    public HearthbeatThread(Socket socket, JTextArea j, int id, ArrayList<ServerInfo> servers, ReentrantLock rl, ArrayList<ServerInfo> down, Condition downnotify) {
        this.socket = socket;
        this.j = j;
        this.id = id;
        this.servers = servers;
        this.rl = rl;
        this.down = down;
        this.downnotify = downnotify;

    }

    @Override
    public void run() {
        while (true) {
            rl.lock();
            try {
                index = -1;
                for (int i = 0; i < servers.size(); i++) {
                    if (servers.get(i).getId() == id) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    if (servers.get(index).getMessagesSize() != 0) {
                        servers.get(index).clearMessages();
                    } else {
                        j.append("Server " + id + " is down!\n");
                        System.out.println(servers.toString());

                        down.add(servers.get(index));
                        downnotify.signalAll();
                        servers.remove(index);
                    }
                }
            } catch (Exception e) {

            } finally {
                rl.unlock();
            }
            try {
                sleep(6000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
