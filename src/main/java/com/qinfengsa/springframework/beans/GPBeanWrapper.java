package com.qinfengsa.springframework.beans;

import lombok.Getter;

/**
 * BeanWrapper 包装bean
 * @author: qinfengsa
 * @date: 2019/4/24 00:26
 */
@Getter
public class GPBeanWrapper {

    /**
     * bean 实例
     */
    private Object wrappedInstance;

    /**
     * class
     */
    private Class<?> wrappedClass;


    /**
     * 构造方法
     * @param wrappedInstance
     */
    public GPBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
        this.wrappedClass = wrappedInstance.getClass();

    }
}
