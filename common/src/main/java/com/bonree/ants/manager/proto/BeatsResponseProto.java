package com.bonree.ants.manager.proto;

import com.bonree.ants.manager.job.JobProperty;

public class BeatsResponseProto {

    public BeatsResponseProto(ReturnCode returnCode) {
        this.code = returnCode;
    }

    private ReturnCode code;

    private int type;

    private long ServerMillis;

    private JobProperty job;

    public long getServerMillis() {
        return ServerMillis;
    }

    public void setServerMillis(long serverMillis) {
        ServerMillis = serverMillis;
    }

    public JobProperty getJob() {
        return job;
    }

    public void setJob(JobProperty job) {
        this.job = job;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ReturnCode getCode() {
        return code;
    }

    public void setCode(ReturnCode code) {
        this.code = code;
    }

}
