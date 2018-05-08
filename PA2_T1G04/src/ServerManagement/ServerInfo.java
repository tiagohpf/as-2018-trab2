package ServerManagement;

import java.net.InetAddress;

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
    private int size;

    public ServerInfo(int id, int port, InetAddress host, int size) {
        this.id = id;
        this.port = port;
        this.active_requests = 0;
        this.host = host;
        this.size = size;
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

    public synchronized void incrementRequests() {
        this.active_requests++;
    }

    public synchronized void decrementRequests() {
        this.active_requests--;
    }

    @Override
    public String toString() {
        return "ServerInfo{" + "id=" + id + ", port=" + port + ", host=" + host + ", active_requests=" + active_requests + ", size=" + size + '}';
    }

}
