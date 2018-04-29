/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerManagement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author kanto
 */
class MonitorThread extends Thread {

    public MonitorThread(Socket clientSocket, JTextArea jTextArea1) {
        Scanner read = null;
        try {
            read = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(MonitorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (read.hasNext()) {
            jTextArea1.append(read.nextLine() + "\n");
        }
    }

}
