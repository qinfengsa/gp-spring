package com.qinfengsa.springframework.web.servlet;

import java.util.Map;

/**
 * ModelAndView
 * @author: qinfengsa
 * @date: 2019/4/22 00:09
 */
public class GPModelAndView {

    /**
     * 模版名称
     */
    private String viewName;



    /**
     * model Spring中用ModelMap实现，addAttributes方法
     */
    private Map<String,?> model;


    /**
     * 构造方法
     * @param viewName
     * @param model
     */
    public GPModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    /**
     * 构造
     * @param viewName
     */
    public GPModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
