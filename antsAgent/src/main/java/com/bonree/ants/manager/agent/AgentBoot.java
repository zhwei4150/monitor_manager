package com.bonree.ants.manager.agent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.action.AgentActionGroup;
import com.bonree.ants.manager.agent.job.JobController;
import com.bonree.ants.manager.agent.job.factory.AbstractJobChannelFactory;
import com.bonree.ants.manager.agent.job.factory.impl.StandardJobChannelFactory;
import com.bonree.ants.manager.agent.job.impl.DefaultJobController;
import com.bonree.ants.manager.config.AgentConfig;
import com.bonree.ants.manager.config.NettyConfig;
import com.bonree.ants.manager.job.JobProperty;
import com.bonree.ants.manager.netty.NettyClient;
import com.bonree.ants.manager.util.ConfigUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class AgentBoot {

    private final static Logger LOG = LoggerFactory.getLogger(AgentBoot.class);

    public static void main(String[] args) {
        try {
            String configFileName = "D:/eclipse-workspace/j2ee/bonree_ants_manager/conf/agent.yml";
            AgentConfig agentConfig = ConfigUtil.loadAgentConfig(configFileName);

            List<AbstractJobChannelFactory> factorys = Lists.newArrayList();
            factorys.add(new StandardJobChannelFactory(JobProperty.STANARD_LAUNCHER));

            JobController jobController = new DefaultJobController(factorys);

            AgentActionGroup actions = AgentActionGroup.createActionGroup(agentConfig, jobController);

            List<String> serverNodes = Splitter.on(",").trimResults().splitToList(agentConfig.getAntsServer());

            for (String serverNode : serverNodes) {
                NettyConfig config = NettyConfig.newBuilder().setHost(serverNode).setPort(9998).setKeepAlive(true)
                        .build();
                NettyClient client = new NettyClient(config, actions);
                client.start();
            }
        } catch (Exception e) {
            LOG.error("start agent error:{}", e);
        }
    }
}
