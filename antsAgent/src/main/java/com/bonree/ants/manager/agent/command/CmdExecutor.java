package com.bonree.ants.manager.agent.command;

import com.bonree.ants.manager.command.CmdProperty.CmdCellDetail;

public interface CmdExecutor {

    public String executeCmd(CmdCellDetail cell, String cmdJson) throws Exception;

}
