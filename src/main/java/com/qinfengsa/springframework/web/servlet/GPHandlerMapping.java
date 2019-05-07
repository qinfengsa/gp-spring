package com.qinfengsa.springframework.web.servlet;

import com.qinfengsa.springframework.annotation.GPRequestParam;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * HandlerMapping
 * @author: qinfengsa
 * @date: 2019/4/25 00:27
 */
@Data
public class GPHandlerMapping {

    /**
     * url路径 java提供的正则表达式
     */
    private Pattern url;
    /**
     * 控制器类
     */
    private Object controller;



    /**
     * 方法
     */
    private Method method;


    /**
     * 构造方法
     * @param url
     * @param controller
     * @param method
     */
    public GPHandlerMapping(Pattern url, Object controller, Method method) {
        this.url = url;
        this.controller = controller;
        this.method = method;
    }
}
