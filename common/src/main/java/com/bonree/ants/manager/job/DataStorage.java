package com.bonree.ants.manager.job;

import java.util.Map;

public interface DataStorage<T> {

    public void init(T storageConfig) throws Exception;

    public void storage(Map<String, Object> term, T storageConfig) throws Exception;

    public void stop(T storageConfig) throws Exception;

}
