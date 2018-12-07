package com.bonree.ants.manager.job;

import java.util.List;

import com.bonree.ants.manager.agent.job.AgentJobDetail;
import com.bonree.ants.manager.agent.job.JobController;
import com.bonree.ants.manager.agent.job.factory.AbstractJobChannelFactory;
import com.bonree.ants.manager.agent.job.factory.impl.StandardJobChannelFactory;
import com.bonree.ants.manager.agent.job.impl.DefaultJobController;
import com.bonree.ants.manager.config.ServerConfig;
import com.bonree.ants.manager.util.ConfigUtil;
import com.google.common.collect.Lists;

public class JobControllerTest {

    public JobControllerTest() {
    }

    public static void main(String[] args) {
        try {

            String configFileName = "D:/eclipse-workspace/j2ee/bonree_ants_manager/conf/server.yml";
            ServerConfig serverConfig = ConfigUtil.loadServerConifg(configFileName);

            List<AbstractJobChannelFactory> factorys = Lists.newArrayList();
            factorys.add(new StandardJobChannelFactory("standard"));

            JobController controller = new DefaultJobController(factorys);
            AgentJobDetail jobDetail = new AgentJobDetail(serverConfig.getJobs().get(0));
            controller.startJob(jobDetail);
            Thread.sleep(60000);
            controller.stopJob(serverConfig.getJobs().get(0).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
