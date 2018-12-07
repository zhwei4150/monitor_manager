package com.bonree.ants.manager.agent.job.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.MessageTaker;
import com.bonree.ants.manager.agent.job.NonTaskPipe;
import com.bonree.ants.manager.job.DataStorage;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:26:23
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 终端类型的storage任务
 ******************************************************************************/
public class TerminalTask extends NonTaskPipe<Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(TerminalTask.class);

    private final Object storageConfig;

    private final DataStorage dataStorage;

    private final String taskName;

    private final ExecutorService executor;

    private boolean closeFlag = false;

    public TerminalTask(String taskName, Object storageConfig, DataStorage dataStorage) {
        this.taskName = taskName;
        this.storageConfig = storageConfig;
        this.dataStorage = dataStorage;
        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, taskName + "-storage-" + Thread.currentThread()
                .getId()));
    }

    public Object getStorageConfig() {
        return storageConfig;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    @Override
    public void start() throws Exception {
        if (upTaker == null) {
            throw new Exception(taskName + " no set upstream!");
        }

        executor.execute(() -> {
            try {
                while (!closeFlag) {
                    Map<String, Object> term = upTaker.take(500, TimeUnit.MILLISECONDS);
                    if (term != null) {
                        dataStorage.storage(term, storageConfig);
                    }
                }
            } catch (Exception e) {
                LOG.error("failed to start storage task! job:{},error:", taskName, e);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        closeFlag = true;
        dataStorage.stop(storageConfig);
        executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        executor.shutdown();
    }

    public String getTaskName() {
        return taskName;
    }

    @Override
    public void init() throws Exception {
        dataStorage.init(storageConfig);
    }

    @Override
    public MessageTaker<Map<String, Object>> taker() {
        return null;
    }

}
