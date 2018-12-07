package com.bonree.ants.manager.agent.job;

import java.util.concurrent.TimeUnit;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:37:20
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 取样接口
 ******************************************************************************/
public interface MessageTaker<T> {

    public T take(long time, TimeUnit util) throws InterruptedException;

}
