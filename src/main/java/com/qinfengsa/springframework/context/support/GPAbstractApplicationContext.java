package com.qinfengsa.springframework.context.support;

/**
 * IOC容器实现的顶层设计 提供refresh方法
 * @author: qinfengsa
 * @date: 2019/4/21 16:39
 */
public abstract class GPAbstractApplicationContext {


    /**
     * 提供refresh方法 供子类重写
     * @throws Exception
     */
    protected void refresh() throws Exception {
    }

}
