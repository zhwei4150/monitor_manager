package com.bonree.ants.manager.agent.action;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.job.JobProperty;
import com.bonree.ants.manager.proto.FilesRequestProto;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.proto.ReturnCode;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.HttpUtil;
import com.bonree.ants.manager.util.ProtoBufUtil;
import com.bonree.ants.manager.util.UriUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

public class FileRequestProxy implements FileTransferListener {

    private static final Logger LOG = LoggerFactory.getLogger(FileRequestProxy.class);
    private Map<String, FileTransferListener> listeners = new ConcurrentHashMap<>();

    public void requestFile(String transName, Channel channel, JobProperty jobProperty) throws Exception {
        requestFile(transName, channel, jobProperty, new FileTransferListener() {
            @Override
            public void process(String transName, String fileName, long offset, long fileSize) {

            }

            @Override
            public void error(String transName, String filePath, ReturnCode code) {
                if (code == ReturnCode.FILE_NOT_EXIST) {
                    LOG.error(filePath + ": is not exist!");
                } else if (code == ReturnCode.FILE_QUEUE_FULL) {
                    LOG.error(filePath + ": queue full");
                }
            }

            @Override
            public void terminal(String transName) {

            }
        });
    }

    public void requestFile(String transName, Channel channel, JobProperty jobProperty, FileTransferListener listener) throws Exception {
        String files = jobProperty.getDependentFiles();
        if (StringUtils.equalsIgnoreCase(files, null)) { // 配置为空
            listener.terminal(transName);
            return;
        }

        List<String> filePaths = Splitter.on(",").trimResults().splitToList(files);
        List<String> filterFile = filterExistFiles(filePaths);

        if (filterFile.isEmpty()) { // 文件已经存在
            listener.terminal(transName);
            return;
        }
        FilesRequestProto fileRequest = new FilesRequestProto();
        fileRequest.setFilePaths(filterFile);
        RequestResponseProto requestResponseProto = new RequestResponseProto(AntsAction.FILES);
        requestResponseProto.setFilesRequest(fileRequest);

        byte[] bytes = ProtoBufUtil.serialize(requestResponseProto);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);

        FullHttpRequest request = HttpUtil.constructRequest(UriUtil
                .makeUri(AntsAction.FILES), AntsHeaders.CONTENT_PROTO, byteBuf);
        listeners.put(transName, listener);
        channel.writeAndFlush(request);

    }

    private static List<String> filterExistFiles(List<String> filePaths) {
        return filePaths.stream().filter(str -> {
            File file = new File(str);
            return !file.exists();
        }).collect(Collectors.toList());
    }

    @Override
    public void process(String transName, String fileName, long offset, long fileSize) {
        listeners.get(transName).process(transName, fileName, offset, fileSize);
    }

    @Override
    public void terminal(String transName) {
        listeners.get(transName).terminal(transName);
    }

    @Override
    public void error(String transName, String filePath, ReturnCode code) {
        listeners.get(transName).error(transName, filePath, code);
    }

    public static void main(String[] args) {
        List<String> files = Lists.newArrayList();
        files.add("D:/123123/ccccc.xml");
        files.add("D:/123123/songziying.pem");
        System.out.println(filterExistFiles(files));
    }

}
