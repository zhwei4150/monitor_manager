package com.bonree.ants.manager.agent.job.factory;


import com.bonree.ants.manager.agent.job.AgentJobDetail;
import com.bonree.ants.manager.agent.job.launcher.JobChannel;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:25:23
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: jobchannel工厂创建接口
 ******************************************************************************/
public interface JobChannelFactory<T> {
    
    public JobChannel<T> createJobChannel(AgentJobDetail jobDetail) throws Exception;

}
