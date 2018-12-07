package com.bonree.ants.manager.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonree.ants.manager.job.JobProperty;

public class AgentHostNode extends HostNode {

    private Map<String, JobProperty> jobs;
    
    public AgentHostNode(String host, int port) {
        super(host, port);
        jobs = new HashMap<>();
    }

    public boolean addJob(JobProperty job) {
        jobs.put(job.getName(), job);
        return true;
    }

    public boolean removeJob(String jobName) {
        jobs.remove(jobName);
        return true;
    }

    public JobProperty getJob(String jobName) {
        return jobs.get(jobName);
    }
    
    public List<String> listJobName(){
        return new ArrayList<>(jobs.keySet());
    }

    @Override
    public String toString() {
        return "AgentHostNode [tasks=" + jobs + ", getHost()=" + getHost() + ", getPort()=" + getPort() + "]";
    }

}
