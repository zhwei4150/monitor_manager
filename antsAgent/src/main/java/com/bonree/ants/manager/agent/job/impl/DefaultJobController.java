package com.bonree.ants.manager.agent.job.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.AbstractJobController;
import com.bonree.ants.manager.agent.job.AgentJobDetail;
import com.bonree.ants.manager.agent.job.factory.AbstractJobChannelFactory;
import com.bonree.ants.manager.agent.job.launcher.JobChannel;
import com.bonree.ants.manager.job.AgentJobStatus;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月15日 上午11:30:07
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description:  jobChnanel控制中心
 ******************************************************************************/
public class DefaultJobController extends AbstractJobController {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJobController.class);

    private Map<String, JobChannel<?>> jobChannels;

    public DefaultJobController(List<AbstractJobChannelFactory> factorys) {
        Preconditions.checkNotNull(factorys);
        jobChannels = Maps.newConcurrentMap();
        factorys.stream().forEach(factory -> addFactory(factory));
    }

    @Override
    public void startJob(AgentJobDetail job) throws Exception {
        if (job == null) {
            return;
        }
        this.addJobDetail(job);

        if (jobChannels.get(job.getJobProperty().getName()) != null) {
            jobChannels.get(job.getJobProperty().getName()).startChannel();
            return;
        }

        // 使用Task来构造任务
        AbstractJobChannelFactory factory = factorys.get(job.getJobProperty().getLauncherFactory());
        if (factory == null) {
            throw new Exception("invalid launcher factory:" + job.getJobProperty().getLauncherFactory());
        }
        
        JobChannel<?> channel = factory.createJobChannel(job);

        if (channel != null) {
            LOG.info("start job:{},detail:{}", job.getJobProperty().getName(), job);
            channel.startChannel();
            jobChannels.put(job.getJobProperty().getName(), channel);
        }
        this.setJobDetailStatus(job.getJobProperty().getName(), AgentJobStatus.RUNNING);
    }

    @Override
    public void stopJob(String jobName) throws Exception {
        if (jobName == null) {
            return;
        }
        if (jobChannels.get(jobName) != null) {
            LOG.info("stop job:{}", jobName);
            jobChannels.get(jobName).stopChannel();
        }
        this.setJobDetailStatus(jobName, AgentJobStatus.STOP);
    }

}
