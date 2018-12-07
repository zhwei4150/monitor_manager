package com.bonree.ants.manager.server.action;

import java.net.InetSocketAddress;
import java.util.stream.Collectors;

import com.bonree.ants.manager.action.ActionGroup;
import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.cache.HostNodeManager;
import com.bonree.ants.manager.config.ServerConfig;
import com.bonree.ants.manager.node.AgentHostNode;
import com.bonree.ants.manager.node.ServerHostNode;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.server.cache.DataCacheManager;
import com.bonree.ants.manager.server.command.CmdManager;
import com.bonree.ants.manager.server.job.JobManager;
import com.bonree.ants.manager.util.NodeUtil;

public class ServerActionGroup extends ActionGroup<RequestResponseProto> {

    private ServerActionGroup() {
    }

    public static ServerActionGroup createActionGroup(ServerConfig serverConfig) throws Exception {

        ServerActionGroup actionGroup = new ServerActionGroup();

        CmdManager cmdManager = new CmdManager(serverConfig.getCmds());

        JobManager jobManager = new JobManager(serverConfig.getJobs());

        HostNodeManager<AgentHostNode> agentManager = HostNodeManager.getAgentManager();

        InetSocketAddress address = (serverConfig.getHost() == null ? new InetSocketAddress(serverConfig
                .getPort()) : new InetSocketAddress(serverConfig.getHost(), serverConfig.getPort()));

        ServerHostNode serverNode = NodeUtil.parseInetAddress(address, ServerHostNode.class);
        serverNode.setTimeMillis(System.currentTimeMillis());// 启动时，需要设置服务时间戳

        DataCacheManager dataCache = new DataCacheManager(serverConfig.getJobs().stream().map(task -> task.getName())
                .collect(Collectors.toList()));

        AntsAction<RequestResponseProto> beatsAction = new ServerBeatsAction(serverNode, agentManager, jobManager);
       
        AntsAction<RequestResponseProto> commandAction = new ServerCommandAction(cmdManager, agentManager);
        
        AntsAction<RequestResponseProto> dataAction = new ServerDataAction(dataCache);
        
        AntsAction<RequestResponseProto> filesAction = new ServerFileAction();
        
        actionGroup.putAction(AntsAction.BEATS, beatsAction);
        actionGroup.putAction(AntsAction.COMMANDS, commandAction);
        actionGroup.putAction(AntsAction.DATAS, dataAction);
        actionGroup.putAction(AntsAction.FILES, filesAction);
        
        return actionGroup;
    }

}
