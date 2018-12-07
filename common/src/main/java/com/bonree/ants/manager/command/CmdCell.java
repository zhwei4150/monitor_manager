package com.bonree.ants.manager.command;

public interface CmdCell<T> {

    public String executeCmd(String jsonCommand, T configEntity) throws Exception;

}
