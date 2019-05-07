package com.qinfengsa.springframework.beans.support;

import com.qinfengsa.springframework.beans.config.GPBeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: qinfengsa
 * @date: 2019/4/23 22:59
 */
public class GPDefaultListableBeanFactory {

    /**
     * beanDefinitionMap 伪IOC容器
     */
    protected Map<String,GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
}
