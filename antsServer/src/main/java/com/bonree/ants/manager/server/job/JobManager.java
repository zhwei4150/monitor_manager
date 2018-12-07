package com.bonree.ants.manager.server.job;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bonree.ants.manager.job.JobDetail;
import com.bonree.ants.manager.job.JobProperty;
import com.bonree.ants.manager.job.ServerJobStatus;
import com.bonree.ants.manager.node.HostNode;
import com.google.common.base.Preconditions;

public class JobManager {

    private final List<JobDetail> jobDetails;

    public JobManager(List<JobProperty> jobs) {
        Preconditions.checkNotNull(jobs);
        jobDetails = jobs.stream().map(job -> new JobDetail(job)).collect(Collectors.toList());
    }

    /** 概述：尝试寻找一个空闲的任务，若没有空闲的任务，则返回null
     * @return
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public Optional<JobProperty> findIdleJob() {
        Optional<JobDetail> detail = jobDetails.stream().filter(jd -> jd.getState() == ServerJobStatus.NONE).findAny();
        return detail.isPresent() ? Optional.of(detail.get().getJob()) : Optional.empty();
    }

    /** 概述：查找指定的agentNode的任务
     * @param hostId
     * @return
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public Optional<JobProperty> findIdleJobSpec(HostNode node) {
        Optional<JobDetail> detail = jobDetails.stream().filter(jd -> jd.getState() == ServerJobStatus.NONE && jd.getJob()
                .getAgentNodes().equalsIgnoreCase(node.getHost())).findAny();
        return detail.isPresent() ? Optional.of(detail.get().getJob()) : Optional.empty();
    }

    /** 概述：指定优先
     * @param node
     * @return
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public Optional<JobProperty> findIdleJobAny(HostNode node) {
        Optional<JobProperty> job = findIdleJobSpec(node);
        return job.isPresent() ? job : findIdleJob();
    }

    /** 概述：为任务设置状态,如果设置的任务详情不存在，则返回false
     * @param jobName
     * @param state
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public boolean setState(String jobName, ServerJobStatus state) {
        Optional<JobDetail> jobDetail = jobDetails.stream().filter(jd -> jd.getJob().getName()
                .equalsIgnoreCase(jobName)).findAny();
        if (jobDetail.isPresent()) {
            jobDetail.get().setState(state);
            return true;
        }
        return false;
    }

    public boolean setAgent(String jobName, HostNode node) {
        Optional<JobDetail> jobDetail = jobDetails.stream().filter(jd -> jd.getJob().getName()
                .equalsIgnoreCase(jobName)).findAny();
        if (jobDetail.isPresent()) {
            jobDetail.get().setOwner(node.getHost());
            return true;
        }
        return false;
    }

    public boolean setAgentAndState(String jobName, HostNode node, ServerJobStatus state) {
        Optional<JobDetail> jobDetail = jobDetails.stream().filter(jd -> jd.getJob().getName()
                .equalsIgnoreCase(jobName)).findAny();
        if (jobDetail.isPresent()) {
            jobDetail.get().setOwner(node.getHost());
            jobDetail.get().setState(state);
            return true;
        }
        return false;
    }

    /** 概述：查看任务详情的状态
     * @param jobName
     * @return
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public Optional<ServerJobStatus> getState(String jobName) {
        Optional<JobDetail> jobDetail = jobDetails.stream().filter(jd -> jd.getJob().getName()
                .equalsIgnoreCase(jobName)).findAny();
        return jobDetail.isPresent() ? Optional.of(jobDetail.get().getState()) : Optional.empty();
    }

    public synchronized Optional<JobProperty> dispatchJob(HostNode node) {
        Optional<JobProperty> job = findIdleJob();
        if (job.isPresent()) {
            setAgentAndState(job.get().getName(), node, ServerJobStatus.PREDISTRIBUTION);
        }
        return job;
    }

    public synchronized boolean checkAndUpdateJob(HostNode node, List<String> jobNames) {
        if (jobNames == null || jobNames.isEmpty()) {
            return true;
        }
        List<JobDetail> jds = jobDetails.stream().filter(jd -> jd.getOwner().equals(node.getHost()))
                .collect(Collectors.toList());

        if (jobNames.size() != jds.size()) {
            return false;
        }

        for (JobDetail jd : jds) {
            if (!jobNames.contains(jd.getJob().getName())) {
                return false;
            }
        }

        for (JobDetail jd : jds) {
            if (jd.getState() == ServerJobStatus.PREDISTRIBUTION) {
                jd.setState(ServerJobStatus.DISTRIBUTION);
            } else if (jd.getState() == ServerJobStatus.PRECANCEL) {
                jd.setState(ServerJobStatus.NONE);
            }
        }
        return true;
    }

}
