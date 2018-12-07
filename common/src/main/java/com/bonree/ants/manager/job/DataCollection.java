package com.bonree.ants.manager.job;

import java.util.Map;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月8日 下午3:11:57
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 
 ******************************************************************************/
public interface DataCollection<T> {

    public void init(T taskConfig) throws Exception;

    public Map<String, Object> collect(T taskConfig) throws Exception;

    public void stop(T taskConfig) throws Exception;

}
