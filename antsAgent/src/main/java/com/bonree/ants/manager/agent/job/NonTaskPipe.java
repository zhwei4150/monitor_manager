package com.bonree.ants.manager.agent.job;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:38:12
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 带有管道的任务
 ******************************************************************************/
public abstract class NonTaskPipe<T> implements TaskPipe<T> {

    protected MessageTaker<T> upTaker;

    public NonTaskPipe() {
    }

    public void setMessageTaker(MessageTaker<T> mt) {
        this.upTaker = mt;
    }

    public MessageTaker<T> getMessageTaker() {
        return upTaker;
    }

}
