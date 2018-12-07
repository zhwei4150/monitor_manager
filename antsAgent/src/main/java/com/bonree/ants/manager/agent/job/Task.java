package com.bonree.ants.manager.agent.job;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:38:00
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 一个子任务
 ******************************************************************************/
public interface Task {
    public void init() throws Exception;
    
    public void start() throws Exception;

    public void stop() throws Exception;
    
}
