package com.qinfengsa.springframework.beans.config;

/**
 * 提供回调入口
 * @author: qinfengsa
 * @date: 2019/5/7 16:42
 */
public class GPBeanPostProcessor {

    /**
     * 实例化之前通知
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    /**
     * 实例化之后通知
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

}
