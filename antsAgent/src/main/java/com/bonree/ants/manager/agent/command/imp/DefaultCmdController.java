package com.bonree.ants.manager.agent.command.imp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.command.CmdController;
import com.bonree.ants.manager.agent.command.CmdExecutor;
import com.bonree.ants.manager.command.CmdProperty;
import com.bonree.ants.manager.command.CmdProperty.CmdCellDetail;

public class DefaultCmdController implements CmdController {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCmdController.class);

    private Map<String, CmdExecutor> cmdExecutors;

    public DefaultCmdController() {
        cmdExecutors = new ConcurrentHashMap<>();
    }

    @Override
    public String executeCmd(CmdProperty cmd, String cmdJson) throws Exception {
        LOG.info("execute cmd:{},json:{}", cmd, cmdJson);
        CmdExecutor executor = cmdExecutors.get(cmd.getName());
        if (cmd.getCommands() == null || cmd.getCommands().isEmpty()) {
            throw new Exception("no command need to execute for cmd:" + cmd);
        }
        // 一次只能执行一个命令
        CmdCellDetail cmdDetail = cmd.getCommands().get(0);
        if (executor != null) {
            return executor.executeCmd(cmdDetail, cmdJson);
        }
        executor = DefaultCmdExecutor.getInstance(cmd);
        cmdExecutors.put(cmd.getName(), executor);
        return executor.executeCmd(cmdDetail, cmdJson);
    }

}
