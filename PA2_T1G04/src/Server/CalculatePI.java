package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 * This class implements the thread responsible for dealing with the new
 * incoming clients
 *
 * @author José Santos
 * @author Tiago Faria
 */
public class CalculatePI extends Thread {

    private final Socket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private JTextArea j = null;

    // Constructor receives the socket
    public CalculatePI(Socket socket, JTextArea j) {
        this.socket = socket;
        this.j = j;
    }

    public Double compute(Long iterations, Long delay) {

        double pi = 0;
        double denominator = 1;

        for (int n = 0; n < iterations; n++) {
            pi = (n % 2 == 0) ? pi + (1. / denominator) : pi - (1. / denominator);
            denominator += 2;
        }
        try {
            Thread.sleep(delay * 1000);
        } catch (Exception e) {
        }
        return pi * 4;
    }

    @Override
    public void run() {
        try {
            // socket´s output stream
            out = new PrintWriter(socket.getOutputStream(), true);
            // socket's input stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String value0 = in.readLine();
            String value1 = in.readLine();
            String id = in.readLine();
            String requestId = in.readLine();
            j.append("Request " + id + " | " + requestId + " | 01 |" + value0 + " | " + value1+"\n");
            j.append("Computing PI\n");
            double value = compute(Long.parseLong(value0), Long.parseLong(value1));
            out.println(id + " | " + requestId + " | 03 | " + 
                    value0 + " | " + value1 + " | " + String.valueOf(value));
            j.append("Result: " + id + " | " + requestId + " | 03 |" 
                    + value0 + " | " + value1 + " | " + String.valueOf(value)+"\n");
            socket.close();
            out.close();
            in.close();
        } catch (Exception e) {
        }
    }
}
