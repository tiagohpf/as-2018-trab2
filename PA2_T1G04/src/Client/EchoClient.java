package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is a client aimed to contact a server
 * on localhost and port 5000. User may insert a line to
 * be sent to the server. The server will answer with the same
 * message. Connection is closed if a null message is sent
 * to the server.
 * @author Óscar Pereira
 */
public class EchoClient {

    private static Socket echoSocket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static final String host = "localhost";
    private static final int port = 5000;

    public static void main( String[] args ) throws IOException {

        // open a connection with the server
        try {
            // create a socket
            echoSocket = new Socket( host, port );
            // socket's output stream
            out = new PrintWriter( echoSocket.getOutputStream(), true );
            // socket's input stream
            in = new BufferedReader( new InputStreamReader( echoSocket.getInputStream() ) );

        } catch( UnknownHostException e ) {
            System.err.println( "Don't know about " + host );
            System.exit( 1 );
        }
        catch (IOException e ) {
            System.err.println( "Couldn't get I/O for the connection to: " + host );
            System.exit( 1 );
        }
        System.out.println( "Connection is established with the Server" );
        // input stream from the console (messages to be sento to the server)
        BufferedReader stdIn = new BufferedReader( new InputStreamReader( System.in ) );
        String userInput;
        System.out.println( "Please insert a messages to be sent to teh server" );
        // not empty message?
        System.out.println("id = " + in.readLine());
        while( ( userInput = stdIn.readLine() ).length() != 0 ) {
            // send the message to the server
            out.println( userInput );
            // wait for echo
            String txt = in.readLine();
            // print echo
            System.out.println( "Client received echo: " + txt );
        }
        // empty message -> close connection
        out.close();
        in.close();
        echoSocket.close();
        System.out.println( "Client closed the connection" );
        System.exit( 0 );
    }
}
