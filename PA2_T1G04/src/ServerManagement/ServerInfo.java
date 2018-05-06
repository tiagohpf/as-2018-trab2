/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerManagement;

import java.net.InetAddress;

/**
 *
 * @author kanto
 */
public class ServerInfo {

    private int id;
    private int port;
    private InetAddress host;
    private int active_requests;

    public ServerInfo(int id, int port, InetAddress host) {
        this.id = id;
        this.port = port;
        this.active_requests = 0;
        this.host = host;
    }

    public int getId() {
        return id;
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

    public void setActive_requests(int active_requests) {
        this.active_requests = active_requests;
    }

    @Override
    public String toString() {
        return "ServerInfo{" + "id=" + id + ", port=" + port + ", host=" + host + ", active_requests=" + active_requests + '}';
    }

}
