package com.bonree.ants.manager.config;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月12日 上午10:38:35
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: netty配置类
 ******************************************************************************/
public class NettyConfig {

    private String host;
    private int port;

    private static final int DEFAULT_BACKLOG = 128;
    private int backlog;

    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 30000;
    private int connectTimeoutMillis;

    private boolean isKeepAlive;
    private boolean tcpNoDelay;

    private static final int DEFAULT_ACCEPT_WORKER_NUM = 2;
    private int acceptWorkerNum;

    private static final int DEFAULT_REQUEST_HANDLE_WORKER_NUM = 6;
    private int requestHandleWorkerNum;

    private static final int DEFAULT_MAX_HTTP_CONTENT_LENGTH = 65 * 1024 * 1024;
    private int maxHttpContentLength;

    public NettyConfig() {
        this.backlog = DEFAULT_BACKLOG;
        this.connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
        this.isKeepAlive = false;
        this.tcpNoDelay = true;
        this.acceptWorkerNum = DEFAULT_ACCEPT_WORKER_NUM;
        this.requestHandleWorkerNum = DEFAULT_REQUEST_HANDLE_WORKER_NUM;
        this.maxHttpContentLength = DEFAULT_MAX_HTTP_CONTENT_LENGTH;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillies(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    public void setKeepAlive(boolean isKeepAlive) {
        this.isKeepAlive = isKeepAlive;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getAcceptWorkerNum() {
        return acceptWorkerNum;
    }

    public void setAcceptWorkerNum(int acceptWorkerNum) {
        this.acceptWorkerNum = acceptWorkerNum;
    }

    public int getRequestHandleWorkerNum() {
        return requestHandleWorkerNum;
    }

    public void setRequestHandleWorkerNum(int requestHandleWorkerNum) {
        this.requestHandleWorkerNum = requestHandleWorkerNum;
    }

    public int getMaxHttpContentLength() {
        return maxHttpContentLength;
    }

    public void setMaxHttpContentLength(int maxHttpContentLength) {
        this.maxHttpContentLength = maxHttpContentLength;
    }
    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private NettyConfig nettyServerConfig;

        private Builder() {
            this.nettyServerConfig = new NettyConfig();
        }

        public Builder setHost(String host) {
            nettyServerConfig.host = host;
            return this;
        }

        public Builder setPort(int port) {
            nettyServerConfig.port = port;
            return this;
        }

        public Builder setBacklog(int backlog) {
            nettyServerConfig.backlog = backlog;
            return this;
        }

        public Builder setConnectTimeoutMillis(int connectTimeoutMillis) {
            nettyServerConfig.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        public Builder setKeepAlive(boolean isKeepAlive) {
            nettyServerConfig.isKeepAlive = isKeepAlive;
            return this;
        }

        public Builder setTcpNoDelay(boolean tcpNoDelay) {
            nettyServerConfig.tcpNoDelay = tcpNoDelay;
            return this;
        }

        public Builder setAcceptWorkerNum(int acceptWorkerNum) {
            nettyServerConfig.acceptWorkerNum = acceptWorkerNum;
            return this;
        }

        public Builder setRequestHandleWorkerNum(int requestHandleWorkerNum) {
            nettyServerConfig.requestHandleWorkerNum = requestHandleWorkerNum;
            return this;
        }

        public Builder setMaxHttpContentLength(int maxHttpContentLength) {
            nettyServerConfig.maxHttpContentLength = maxHttpContentLength;
            return this;
        }

        public NettyConfig build() {
            return nettyServerConfig;
        }
    }
}
