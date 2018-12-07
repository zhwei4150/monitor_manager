package com.bonree.ants.manager.agent.job.factory.impl;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.bonree.ants.manager.agent.job.AgentJobDetail;
import com.bonree.ants.manager.agent.job.AntsKafkaConsumer;
import com.bonree.ants.manager.agent.job.factory.AbstractJobChannelFactory;
import com.bonree.ants.manager.agent.job.impl.KafkaStorageTask;
import com.bonree.ants.manager.agent.job.impl.NonTimedCollectionTask;
import com.bonree.ants.manager.agent.job.impl.StorageTask;
import com.bonree.ants.manager.agent.job.impl.TerminalTask;
import com.bonree.ants.manager.agent.job.impl.TimedCollectionTask;
import com.bonree.ants.manager.agent.job.launcher.JobChannel;
import com.bonree.ants.manager.agent.job.launcher.impl.StandardJobChannel;
import com.bonree.ants.manager.job.DataCollection;
import com.bonree.ants.manager.job.DataStorage;
import com.bonree.ants.manager.job.JobProperty;
import com.bonree.ants.manager.job.JobProperty.StorageProperty;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:24:05
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: jobchannel创建工厂
 ******************************************************************************/
public class StandardJobChannelFactory extends AbstractJobChannelFactory {

    public StandardJobChannelFactory(String name) {
        super(name);
    }

    @Override
    public JobChannel<Map<String,Object>> createJobChannel(AgentJobDetail jobDetail) throws Exception {
        JobChannel<Map<String,Object>> channel = new StandardJobChannel(jobDetail);
        JobProperty jobProperty = jobDetail.getJobProperty();
        Yaml yaml = new Yaml();
        Class<?> collectionCls = Class.forName(jobProperty.getCollectionExecClass());
        DataCollection<?> dataCollection = (DataCollection<?>) collectionCls.newInstance();
        Class<?> collectionConfigCls = Class.forName(jobProperty.getCollectionConfigClass());
        Object collectionProperty = yaml.loadAs(jobProperty.getCollectionConfig(), collectionConfigCls);

        if (jobProperty.getCollectionType().equals(JobProperty.TIMED_COLLECTION)) {
            channel.channelInitPipe(new TimedCollectionTask(jobProperty.getName(), collectionProperty, dataCollection));
        } else {
            channel.channelInitPipe(new NonTimedCollectionTask(jobProperty
                    .getName(), collectionProperty, dataCollection));
        }

        List<StorageProperty> storageProperties = jobProperty.getDataStorages();

        for (int i = 0; i < storageProperties.size(); i++) {
            Class<?> storageCls = Class.forName(storageProperties.get(i).getStorageExecClass());
            DataStorage<?> dataStorage = (DataStorage<?>) storageCls.newInstance();

            Class<?> storageConfigCls = Class.forName(storageProperties.get(i).getStorageConfigClass());
            Object storageProperty = yaml.loadAs(storageProperties.get(i).getStorageConfig(), storageConfigCls);
            if (i == storageProperties.size() - 1) {
                channel.channelInitPipe(new TerminalTask(jobProperty.getName(), storageProperty, dataStorage));
            } else {
                if (storageProperties.get(i).getStorageType().equals(JobProperty.QUEUE_STORAGE)) {
                    AntsKafkaConsumer<?,?> consumer = (AntsKafkaConsumer<?,?>) Class
                            .forName("com.bonree.ants.manager.agent.job.example.AntsKafkaConsumerV10").newInstance();
                    channel.channelInitPipe(new KafkaStorageTask(jobProperty
                            .getName(), storageProperty, dataStorage, consumer));
                } else {
                    channel.channelInitPipe(new StorageTask(jobProperty.getName(), storageProperty, dataStorage));
                }
            }

        }
        return channel;
    }

}
