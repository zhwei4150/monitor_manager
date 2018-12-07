package com.bonree.ants.manager.agent.command;

import com.bonree.ants.manager.command.CmdProperty;

public interface CmdController {

    public String executeCmd(CmdProperty cmd,String cmdJson) throws Exception;

}
