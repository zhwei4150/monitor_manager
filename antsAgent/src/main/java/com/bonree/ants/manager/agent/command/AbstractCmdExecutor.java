package com.bonree.ants.manager.agent.command;

public abstract class AbstractCmdExecutor implements CmdExecutor{

    private final String name;

    public AbstractCmdExecutor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
