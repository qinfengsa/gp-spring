package com.qinfengsa.demo.aspect;

import com.qinfengsa.springframework.aop.aspect.GPJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


/**
 * 切面实现类，实现功能增强
 * @author qinfengsa
 * @date 2019/4/22 0:15
 */
@Slf4j
public class LogAspect {

    /**
     * 在调用一个方法之前，执行before方法
     * @param joinPoint
     */
    public void before(GPJoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }


    /**
     * 在调用一个方法之后，执行after方法
     * @param joinPoint
     */
    public void after(GPJoinPoint joinPoint){
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    /**
     * 出现异常
     * @param joinPoint
     * @param ex
     */
    public void afterThrowing(GPJoinPoint joinPoint, Throwable ex){
        log.info("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }

}
