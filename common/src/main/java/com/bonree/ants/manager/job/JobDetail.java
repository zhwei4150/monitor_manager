package com.bonree.ants.manager.job;

import com.google.common.base.Preconditions;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月9日 下午3:17:59
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 监控任务详情
 ******************************************************************************/
public class JobDetail {

    public static final String UNKNOW_OWNER = "unknow";

    private String owner;
    private ServerJobStatus state;
    private final JobProperty job;

    public JobDetail(JobProperty job) {
        Preconditions.checkNotNull(job, "Illegal job");
        this.job = job;
        this.owner = UNKNOW_OWNER;
        this.state = ServerJobStatus.NONE;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ServerJobStatus getState() {
        return state;
    }

    public void setState(ServerJobStatus state) {
        this.state = state;
    }

    public JobProperty getJob() {
        return job;
    }
}
