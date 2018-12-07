package com.bonree.ants.manager.server.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.proto.FilesRequestProto;
import com.bonree.ants.manager.proto.FilesResponseProto;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.proto.ReturnCode;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.HttpUtil;
import com.bonree.ants.manager.util.ProtoBufUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

public class ServerFileAction implements AntsAction<RequestResponseProto> {

    private final static Logger LOG = LoggerFactory.getLogger(ServerFileAction.class);

    private final static int buf_size = 524288;
    private final static String file_separate = "/";

    private BlockingQueue<FilesContext> requests = new ArrayBlockingQueue<>(100);
    private ExecutorService receiver = Executors.newFixedThreadPool(2, r -> new Thread(r, "files-receiver-" + Thread
            .currentThread().getId()));
    private ExecutorService filesPool = Executors.newSingleThreadExecutor(r -> new Thread(r, "files-action-" + Thread
            .currentThread().getId()));

    public ServerFileAction() {
        filesPool.execute(() -> {
            try {
                while (true) {
                    FilesContext requestContext = requests.take();
                    if (requestContext != null) {
                        filesPool.execute(() -> {
                            try {
                                dealFile(requestContext.getCtx(), requestContext.getRequest());
                            } catch (Exception e) {
                                LOG.error("when sending the file to agent,there are some error!", e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                LOG.error("dealFile error:", e);
            }
        });
    }

    private void dealFile(ChannelHandlerContext ctx, FilesRequestProto msg) throws Exception {
        List<String> filePaths = msg.getFilePaths();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (!file.exists()) {
                sendFileError(ctx, filePath, ReturnCode.FILE_NOT_EXIST);
            }
            if (!file.isDirectory()) {
                sendFileContent(ctx, file, ".", filePath);

            } else {
                findFiles(ctx, file, ".", filePath);
            }

        }
        sendFileOver(ctx, msg.getTransName());
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {

        receiver.execute(() -> {
            try {
                boolean success = requests.offer(new FilesContext(ctx, msg.getFilesRequest()));
                if (!success) {
                    sendFileError(ctx, msg.getFilesRequest().getTransName(), ReturnCode.FILE_QUEUE_FULL);
                }
            } catch (Exception e) {
                LOG.error("when the file put in queue,there are some error!", e);
            }
        });

    }

    public void sendFileOver(ChannelHandlerContext ctx, String transName) throws Exception {
        FilesResponseProto fileResponse = new FilesResponseProto(ReturnCode.OK);
        fileResponse.setTransName(transName);
        fileResponse.setEnd(true);
        RequestResponseProto requestResponseProto = new RequestResponseProto(AntsAction.FILES);
        requestResponseProto.setFilesResponse(fileResponse);

        byte[] bytes = ProtoBufUtil.serialize(requestResponseProto);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_PROTO, byteBuf);
        ctx.channel().writeAndFlush(response);

    }

    public void sendFileError(ChannelHandlerContext ctx, String transName, ReturnCode code) throws Exception {
        sendFileError(ctx, transName, "all", code);
    }

    public void sendFileError(ChannelHandlerContext ctx, String transName, String filePath, ReturnCode code) throws Exception {
        FilesResponseProto fileResponse = new FilesResponseProto(code);
        fileResponse.setSourceFilePath(filePath);
        fileResponse.setTransName(transName);
        RequestResponseProto requestResponseProto = new RequestResponseProto(AntsAction.FILES);
        requestResponseProto.setFilesResponse(fileResponse);

        byte[] bytes = ProtoBufUtil.serialize(requestResponseProto);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_PROTO, byteBuf);
        ctx.channel().writeAndFlush(response);

    }

    public void findFiles(ChannelHandlerContext ctx, File file, String rootPath, String sourceFilePath) throws Exception {
        String parentPath = rootPath + file_separate + file.getName();
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                findFiles(ctx, file, parentPath, sourceFilePath);
            } else {
                sendFileContent(ctx, file, parentPath, sourceFilePath);
            }
        }
    }

    public void sendFileContent(ChannelHandlerContext ctx, File file, String parentPath, String sourceFilePath) throws Exception {
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        FilesResponseProto fileResponse = new FilesResponseProto(ReturnCode.OK);
        fileResponse.setFileName(file.getName());
        fileResponse.setEnd(false);
        fileResponse.setPackageSeq(0);
        fileResponse.setParentDir(parentPath);
        fileResponse.setSourceFilePath(sourceFilePath);
        RequestResponseProto requestResponseProto = new RequestResponseProto(AntsAction.FILES);
        requestResponseProto.setFilesResponse(fileResponse);

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] content = new byte[buf_size];
            int length = 0;
            while (-1 != (length = bis.read(content, 0, buf_size))) {
                requestResponseProto.getFilesResponse().setLength(length);
                if (length < buf_size) {
                    byte[] smallContent = new byte[length];
                    for (int i = 0; i < length; i++) {
                        smallContent[i] = content[i];
                    }
                    requestResponseProto.getFilesResponse().setContent(smallContent);
                } else {
                    requestResponseProto.getFilesResponse().setContent(content);
                }

                byte[] bytes = ProtoBufUtil.serialize(requestResponseProto);
                ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
                FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_PROTO, byteBuf);
                ctx.channel().writeAndFlush(response);
            }

        } finally {
            if (bis != null) {
                bis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
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

    private static class FilesContext {
        private final ChannelHandlerContext ctx;
        private final FilesRequestProto request;

        private FilesContext(ChannelHandlerContext ctx, FilesRequestProto request) {
            this.ctx = ctx;
            this.request = request;
        }

        public ChannelHandlerContext getCtx() {
            return ctx;
        }

        public FilesRequestProto getRequest() {
            return request;
        }

    }

}
