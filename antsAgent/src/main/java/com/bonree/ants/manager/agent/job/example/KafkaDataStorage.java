package com.bonree.ants.manager.agent.job.example;

import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.job.DataStorage;
import com.bonree.ants.manager.util.ConfigUtil;
import com.bonree.ants.manager.util.JsonUtil;

public class KafkaDataStorage implements DataStorage<Map<String, String>> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaDataStorage.class);
    private Properties props = new Properties();
    private KafkaProducer<Integer, String> producer;
    private String topic;

    public KafkaDataStorage() {
    }

    @Override
    public void init(Map<String, String> storageConfig) throws Exception {
        this.topic = storageConfig.get("topic");
        for (Entry<String, String> cf : storageConfig.entrySet()) {
            if (ConfigUtil.isKafkaConfig(cf.getKey())) {
                LOG.info("overwrite kafka:{}:{}",ConfigUtil.trimKafkaConfig(cf.getKey()),cf.getValue());
                props.put(ConfigUtil.trimKafkaConfig(cf.getKey()), cf.getValue());
            }
        }
        producer = new KafkaProducer<>(props);

    }

    @Override
    public void storage(Map<String, Object> term, Map<String, String> storageConfig) throws Exception {
        if (topic == null) {
            throw new Exception("no topic!please config the topic");
        }

        String str = JsonUtil.toJsonString(term);
        ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(topic, str);
        producer.send(record);
    }

    @Override
    public void stop(Map<String, String> storageConfig) throws Exception {
        producer.close();
    }

}
