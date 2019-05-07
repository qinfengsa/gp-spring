package com.qinfengsa.springframework.aop;

/**
 * AOP代理接口
 * @author: qinfengsa
 * @date: 2019/4/27 23:47
 */
public interface GPAopProxy {

    /**
     * 获取代理对象
     * @return
     */
    Object getProxy();

    /**
     * 获取代理对象，传入类加载器
     * @param classLoader
     * @return
     */
    Object getProxy(ClassLoader classLoader);
}
