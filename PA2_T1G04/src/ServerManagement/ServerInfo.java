package ServerManagement;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author José Santos
 * @author Tiago Faria
 */
public class ServerInfo {

    private int id;
    private int port;
    private InetAddress host;
    private int active_requests;
    private final int size;
    private int threadId;
    private ArrayList<String> messages;

    public ServerInfo(int id, int port, InetAddress host, int size) {
        this.id = id;
        this.port = port;
        this.active_requests = 0;
        this.host = host;
        this.size = size;
        this.threadId = 0;
        this.messages = new ArrayList<>();
        this.messages.add("Alive");
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActive_requests() {
        return active_requests;
    }
    
    public int getThreadId() {
        return threadId;
    }
    
    public void incrementThreadId() {
        this.threadId++;
    }

    public void incrementRequests() {
        this.active_requests++;
    }

    public void decrementRequests() {
        this.active_requests--;
    }
    
    public void addMessage(String text){
        this.messages.add(text);
    }
    
    public int getMessagesSize(){
        return this.messages.size();
    }
    
    public void clearMessages(){
        this.messages.clear();
    }

    @Override
    public String toString() {
        return "Server: {" + "id=" + id + ", port=" + port + ", host=" + host + ", active_requests=" + active_requests + ", size=" + size + ", threadId=" + threadId + ", messages=" + messages + '}';
    }

}
