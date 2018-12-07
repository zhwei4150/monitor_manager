package com.bonree.ants.manager.job;

import java.util.Map;

public interface DataTransform<T> {

    public void init(T transConfig) throws Exception;

    public Map<String,Object> transform(Map<String, Object> term, T transConfig) throws Exception;

    public void stop(T transConfig) throws Exception;

}
