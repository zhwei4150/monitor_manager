package com.bonree.ants.manager.agent.job.launcher;

import com.bonree.ants.manager.agent.job.TaskPipe;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:36:28
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 一个接口，方便后面扩展
 ******************************************************************************/
public interface JobChannel<T> {

    public void channelInitPipe(TaskPipe<T> pipe) throws Exception;

    public void startChannel() throws Exception;

    public void stopChannel() throws Exception;

}
