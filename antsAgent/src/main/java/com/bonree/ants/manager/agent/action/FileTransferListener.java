package com.bonree.ants.manager.agent.action;

import com.bonree.ants.manager.proto.ReturnCode;

public interface FileTransferListener {

    public void process(String transName, String fileName, long offset, long fileSize);

    public void terminal(String transName);

    public void error(String transName, String fileName, ReturnCode code);
}
