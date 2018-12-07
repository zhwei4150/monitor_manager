package com.bonree.ants.manager.agent.job.example;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:23:48
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: demo
 ******************************************************************************/
public class SampleJobConfig {

    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "SampleEntity [name=" + name + ", age=" + age + "]";
    }

}
