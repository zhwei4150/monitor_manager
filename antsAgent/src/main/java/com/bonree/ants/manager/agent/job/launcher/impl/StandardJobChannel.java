package com.bonree.ants.manager.agent.job.launcher.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bonree.ants.manager.agent.job.AgentJobDetail;
import com.bonree.ants.manager.agent.job.MessageTaker;
import com.bonree.ants.manager.agent.job.TaskPipe;
import com.bonree.ants.manager.agent.job.launcher.JobChannel;
import com.bonree.ants.manager.job.AgentJobStatus;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:34:28
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: jobchannel
 ******************************************************************************/
public class StandardJobChannel implements JobChannel<Map<String,Object>> {

    private final AgentJobDetail details;
    List<TaskPipe<Map<String,Object>>> pipes;

    public StandardJobChannel(AgentJobDetail details) {
        this.details = details;
        pipes = new ArrayList<>();
    }

    @Override
    public void channelInitPipe(TaskPipe<Map<String,Object>> pipe) throws Exception {
        pipe.init();
        if (pipes.isEmpty()) {
            pipes.add(pipe);
        } else {
            TaskPipe<Map<String,Object>> beforePipe = pipes.get(pipes.size() - 1);
            MessageTaker<Map<String,Object>> beforeTaker = beforePipe.taker();
            pipe.setMessageTaker(beforeTaker);
            pipes.add(pipe);
        }
    }

    @Override
    public void startChannel() throws Exception {
        for (TaskPipe<Map<String,Object>> pipe : pipes) {
            pipe.start();
        }
        details.setStatus(AgentJobStatus.RUNNING);
    }

    @Override
    public void stopChannel() throws Exception {
        for (TaskPipe<Map<String,Object>> pipe : pipes) {
            pipe.stop();
        }
        details.setStatus(AgentJobStatus.STOP);
    }

}
