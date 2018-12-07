package com.bonree.ants.manager.config;

import java.util.ArrayList;
import java.util.List;

import com.bonree.ants.manager.command.CmdProperty;
import com.bonree.ants.manager.job.JobProperty;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月12日 上午10:38:17
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: ants server配置类
 ******************************************************************************/
public class ServerConfig {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 9998;
    public static final int DEFAULT_INTERVAL = 5;
    public static final int DEFAULT_LEAST_AGENT = 3;
    private String host;
    private int port;
    private int interval;
    private int leastAgent;
    private String jobModulePath;
    private String cmdModulePath;

    private final List<JobProperty> jobs;

    private final List<CmdProperty> cmds;

    public ServerConfig() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
        this.interval = DEFAULT_INTERVAL;
        this.leastAgent = DEFAULT_LEAST_AGENT;
        jobs = new ArrayList<>();
        cmds = new ArrayList<>();
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

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getJobModulePath() {
        return jobModulePath;
    }

    public void setJobModulePath(String jobModulePath) {
        this.jobModulePath = jobModulePath;
    }

    public String getCmdModulePath() {
        return cmdModulePath;
    }

    public void setCmdModulePath(String cmdModulePath) {
        this.cmdModulePath = cmdModulePath;
    }

    public List<JobProperty> getJobs() {
        return jobs;
    }

    public void addJob(JobProperty job) {
        if (job != null) {
            jobs.add(job);
        }
    }

    public void addCmd(CmdProperty cmd) {
        if (cmd != null) {
            cmds.add(cmd);
        }
    }

    public List<CmdProperty> getCmds() {
        return cmds;
    }

    public int getLeastAgent() {
        return leastAgent;
    }

    public void setLeastAgent(int leastAgent) {
        this.leastAgent = leastAgent;
    }

    @Override
    public String toString() {
        return "ServerConfig [host=" + host + ", port=" + port + ", interval=" + interval + ", leastAgent=" + leastAgent + ", jobModulePath=" + jobModulePath + ", cmdModulePath=" + cmdModulePath + ", jobs=" + jobs + ", cmds=" + cmds + "]";
    }

}
