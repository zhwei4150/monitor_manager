package com.bonree.ants.manager.agent.job.factory;

import java.util.Map;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:24:49
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 一个具有名字的jobchannel工厂
 ******************************************************************************/
public abstract class AbstractJobChannelFactory implements JobChannelFactory<Map<String,Object>> {

    protected final String factoryName;
   

    public AbstractJobChannelFactory(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getFactoryName() {
        return factoryName;
    }

}
