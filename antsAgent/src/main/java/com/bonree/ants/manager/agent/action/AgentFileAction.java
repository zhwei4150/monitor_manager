package com.bonree.ants.manager.agent.action;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.proto.FilesResponseProto;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.proto.ReturnCode;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import io.netty.channel.ChannelHandlerContext;

public class AgentFileAction implements AntsAction<RequestResponseProto> {

    private static final Logger LOG = LoggerFactory.getLogger(AgentFileAction.class);

    private final String file_separator = "/";

    private final String baseDir = "D:/eclipse-workspace/j2ee/bonree_ants_manager/agent/plugins/";

    private final FileRequestProxy proxy;

    public AgentFileAction(FileRequestProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {

        FilesResponseProto fileResponse = msg.getFilesResponse();

        if (fileResponse.getCode() != ReturnCode.OK) {
            proxy.error(fileResponse.getTransName(), fileResponse.getSourceFilePath(), fileResponse.getCode());
        }

        if (fileResponse.isEnd()) {
            proxy.terminal(fileResponse.getTransName());
        }

        String filePath = baseDir + file_separator + fileResponse.getParentDir() + file_separator + fileResponse
                .getFileName();
        LOG.info("recvr file:{}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            Files.createParentDirs(file);
        }
        Files.asByteSink(file, FileWriteMode.APPEND).write(fileResponse.getContent());
        proxy.process(fileResponse.getTransName(), fileResponse.getSourceFilePath(), fileResponse
                .getOffset(), fileResponse.getFileSize());
    }

    @Override
    public void dealIdleStateEvent(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void dealexceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void close() throws Exception {

    }
}
