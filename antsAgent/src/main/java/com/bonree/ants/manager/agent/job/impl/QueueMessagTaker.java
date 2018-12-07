package com.bonree.ants.manager.agent.job.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.bonree.ants.manager.agent.job.MessageTaker;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:15:50
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 队列采取器
 ******************************************************************************/
public class QueueMessagTaker<T> implements MessageTaker<T> {

    private BlockingQueue<T> queue;

    public QueueMessagTaker(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public T take(long time, TimeUnit util) throws InterruptedException {
        return queue.poll(time, util);
    }

}
