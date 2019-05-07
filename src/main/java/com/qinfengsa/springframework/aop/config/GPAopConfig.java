package com.qinfengsa.springframework.aop.config;

import lombok.Data;

/**
 * AOP配置属性
 * @author: qinfengsa
 * @date: 2019/5/5 14:33
 */
@Data
public class GPAopConfig {

    /**
     * 切点
     */
    private String pointCut;

    /**
     * 切面之前
     */
    private String aspectBefore;

    /**
     * 切面之后
     */
    private String aspectAfter;

    /**
     * 切面所在Class
     */
    private String aspectClass;

    /**
     * 抛出异常
     */
    private String aspectAfterThrow;

    /**
     * 异常名称
     */
    private String aspectAfterThrowingName;
}
