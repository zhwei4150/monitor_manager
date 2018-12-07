package com.bonree.ants.manager.agent.job.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.QueueTaskPipe;
import com.bonree.ants.manager.agent.job.TaskPipe;
import com.bonree.ants.manager.job.DataTransform;

public class TransformTask extends QueueTaskPipe<Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(TransformTask.class);
    private final Object transConfig;

    private final DataTransform transform;

    private final String taskName;

    private final ExecutorService executor;

    private boolean closeFlag = false;

    public TransformTask(String taskName, Object transConfig, DataTransform transform) {
        this.taskName = taskName;
        this.transConfig = transConfig;
        this.transform = transform;
        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, taskName + "-storage-" + Thread.currentThread()
                .getId()));
    }

    public Object getTransConfig() {
        return transConfig;
    }

    public DataTransform getTransform() {
        return transform;
    }

    public String getTaskName() {
        return taskName;
    }

    @Override
    public void init() throws Exception {
        closeFlag = true;
        transform.init(transConfig);
        executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        executor.shutdown();

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
                        Map<String, Object> newTerm = transform.transform(term, transConfig);
                        this.queue.put(newTerm);
                    }
                }
            } catch (Exception e) {
                LOG.error("failed to start transform task! job:{},error:", taskName, e);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        transform.stop(transConfig);
    }

}
