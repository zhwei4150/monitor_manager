package com.bonree.ants.manager.node;

public class ServerHostNode extends HostNode {

    private long timeMillis;

    public ServerHostNode(String host, int port) {
        super(host, port);
        timeMillis = 0;
    }

    public ServerHostNode(String host, int port, long timeMillis) {
        super(host, port);
        this.timeMillis = timeMillis;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public String toString() {
        return "ServerHostNode [timeMillis=" + timeMillis + ", getHost()=" + getHost() + ", getPort()=" + getPort() + "]";
    }

}
