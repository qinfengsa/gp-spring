package com.qinfengsa.springframework.core;

/**
 * BeanFactory从IOC容器中拿到实例Bean
 * @author: qinfengsa
 * @date: 2019/4/20 18:11
 */
public interface GPBeanFactory {

    /**
     * 通过beanName从IOC容器中拿到实例Bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    /***
     * 通过class(通常是接口)从IOC容器中拿到实例Bean
     * @param beanClass
     * @return
     * @throws Exception
     */

    Object getBean(Class<?> beanClass) throws Exception;
}
