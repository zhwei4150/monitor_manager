package com.bonree.ants.manager.agent.job.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.QueueTaskPipe;
import com.bonree.ants.manager.job.DataStorage;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:17:36
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 普通数据存储任务
 ******************************************************************************/
public class StorageTask extends QueueTaskPipe<Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(StorageTask.class);

    private final Object storageConfig;

    private final DataStorage dataStorage;

    private final String taskName;

    private final ExecutorService executor;

    private boolean closeFlag = false;

    public StorageTask(String taskName, Object storageConfig, DataStorage dataStorage) {
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
        if (this.upTaker == null) {
            throw new Exception(taskName + " no set upstream!");
        }

        executor.execute(() -> {
            try {
                while (!closeFlag) {
                    Map<String, Object> term = this.upTaker.take(500, TimeUnit.MILLISECONDS);
                    if (term != null) {
                        dataStorage.storage(term, storageConfig);
                        this.queue.put(term);
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

}
