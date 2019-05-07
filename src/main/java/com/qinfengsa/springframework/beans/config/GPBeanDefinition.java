package com.qinfengsa.springframework.beans.config;

import lombok.Data;

/**
 * 保存bean的配置信息
 * @author: qinfengsa
 * @date: 2019/4/21 23:57
 */
@Data
public class GPBeanDefinition {

    /**
     * className
     */
    private String beanClassName;

    /**
     * 是否懒加载，默认false
     */
    private boolean lazyInit = false;

    /**
     * factoryBeanName
     */
    private String factoryBeanName;

    /**
     * 是否单例，默认为true
     */
    private boolean isSingleton = true;
}
