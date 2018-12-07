package com.bonree.ants.manager.agent.job;

import com.bonree.ants.manager.job.AgentJobStatus;
import com.bonree.ants.manager.job.JobProperty;

public class AgentJobDetail {

    private final JobProperty jobProperty;

    private AgentJobStatus status;

    public AgentJobDetail(JobProperty jobProperty) {
        this.jobProperty = jobProperty;
        this.status = AgentJobStatus.INIT;
    }

    public JobProperty getJobProperty() {
        return jobProperty;
    }

    public AgentJobStatus getStatus() {
        return status;
    }

    public void setStatus(AgentJobStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AgentJobDetail [jobProperty=" + jobProperty + ", status=" + status + "]";
    }

}
