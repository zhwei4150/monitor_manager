package com.bonree.ants.manager.proto;

import java.util.List;

import com.bonree.ants.manager.node.HostNode;

public class BeatsRequestProto {

    private BeatsType type;

    private HostNode minNode;

    private List<String> jobNames;

    public BeatsRequestProto(BeatsType type) {
        this.type = type;
    }

    public List<String> getJobNames() {
        return jobNames;
    }

    public void setJobNames(List<String> jobNames) {
        this.jobNames = jobNames;
    }

    public HostNode getMinNode() {
        return minNode;
    }

    public void setMinNode(HostNode minNode) {
        this.minNode = minNode;
    }

    public BeatsType getType() {
        return type;
    }

    public void setType(BeatsType type) {
        this.type = type;
    }

}
