package com.bonree.ants.manager.agent.job;

import java.util.List;
import java.util.Map;

import com.bonree.ants.manager.agent.job.factory.AbstractJobChannelFactory;
import com.bonree.ants.manager.job.AgentJobStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:35:03
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 抽象的Job控制器类
 ******************************************************************************/
public abstract class AbstractJobController implements JobController {

    protected Map<String, AbstractJobChannelFactory> factorys;
    protected Map<String, AgentJobDetail> agentJobDetails;

    public AbstractJobController() {
        factorys = Maps.newConcurrentMap();
        agentJobDetails = Maps.newConcurrentMap();
    }

    public AbstractJobController addFactory(AbstractJobChannelFactory factory) {
        factorys.put(factory.getFactoryName(), factory);
        return this;
    }

    public AbstractJobController addFactorys(List<AbstractJobChannelFactory> factorys) {
        factorys.stream().forEach(factory -> this.factorys.put(factory.getFactoryName(), factory));
        return this;
    }

    public List<AgentJobDetail> listAllJobDetail() {
        return Lists.newArrayList(agentJobDetails.values());
    }

    public AgentJobDetail getJobDetail(String jobName) {
        return agentJobDetails.get(jobName);
    }

    public void addJobDetail(AgentJobDetail job) {
        agentJobDetails.put(job.getJobProperty().getName(), job);
    }

    public void setJobDetailStatus(String jobName, AgentJobStatus status) {
        agentJobDetails.get(jobName).setStatus(status);
    }

}
