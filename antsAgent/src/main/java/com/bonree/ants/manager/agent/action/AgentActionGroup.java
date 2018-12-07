package com.bonree.ants.manager.agent.action;

import java.util.List;

import com.bonree.ants.manager.action.ActionGroup;
import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.agent.job.JobController;
import com.bonree.ants.manager.cache.HostNodeManager;
import com.bonree.ants.manager.config.AgentConfig;
import com.bonree.ants.manager.node.ServerHostNode;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.google.common.base.Splitter;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月29日 下午3:57:53
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: agent需要的actions
 ******************************************************************************/
public class AgentActionGroup extends ActionGroup<RequestResponseProto> {

    private AgentActionGroup() {

    }

    public static AgentActionGroup createActionGroup(AgentConfig agentConfig, JobController jobController) throws Exception {
        AgentActionGroup actions = new AgentActionGroup();

        HostNodeManager<ServerHostNode> hostManager = HostNodeManager.getServerManager();
        List<String> hosts = Splitter.on(",").trimResults().splitToList(agentConfig.getAntsServer());
        for (String addr : hosts) {
            List<String> addrTuple = Splitter.on(":").splitToList(addr);
            ServerHostNode serverNode = null;
            if (addrTuple.size() > 1) {
                serverNode = new ServerHostNode(addrTuple.get(0), Integer.parseInt(addrTuple.get(1)));
            } else if (addrTuple.size() > 0) {
                serverNode = new ServerHostNode(addrTuple.get(0), 9998);
            }
            hostManager.addHostNode(serverNode);
        }

        FileRequestProxy fileRequestProxy = new FileRequestProxy();

        AntsAction<RequestResponseProto> beatsAction = new AgentBeatsAction(hostManager, fileRequestProxy, jobController);

        AntsAction<RequestResponseProto> commandAction = new AgentCommandAction();

        AntsAction<RequestResponseProto> dataAction = new AgentDataAction();

        AntsAction<RequestResponseProto> fileAction = new AgentFileAction(fileRequestProxy);

        actions.putAction(AntsAction.BEATS, beatsAction);
        actions.putAction(AntsAction.COMMANDS, commandAction);
        actions.putAction(AntsAction.DATAS, dataAction);
        actions.putAction(AntsAction.FILES, fileAction);
        return actions;
    }

}
