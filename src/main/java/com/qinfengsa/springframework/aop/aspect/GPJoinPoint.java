package com.qinfengsa.springframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 切面点
 * @author: qinfengsa
 * @date: 2019/4/22 00:12
 */
public interface GPJoinPoint {

    /**
     * 获取代理类的实例
     * @return
     */
    Object getThis();

    /**
     * 获取代理方法的参数
     * @return
     */
    Object[] getArguments();

    /**
     * 获取代理方法
     * @return
     */
    Method getMethod();


    /**
     * 添加参数，实现几个切面方法共享参数
     * @param key
     * @param value
     */
    void setUserAttribute(String key, Object value);

    /**
     * 获取参数
     * @param key
     * @return
     */
    Object getUserAttribute(String key);
}
