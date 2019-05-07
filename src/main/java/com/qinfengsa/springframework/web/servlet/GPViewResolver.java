package com.qinfengsa.springframework.web.servlet;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 视图解析器
 * @author: qinfengsa
 * @date: 2019/4/25 22:50
 */
public class GPViewResolver {


    /**
     * 模版后缀
     */
    private static final String DEFAULT_TEMPLATE_SUFFIX = ".html";


    /**
     * 模版文件根目录
     */
    private File templateRootDir;

    /**
     * 视图解析器构造
     * @param templateRoot
     */
    public GPViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getPath();
        this.templateRootDir = new File(templateRootPath);
    }


    public GPView resolveViewName(String viewName) {
        // viewName 为空  返回null，无法解析
        if (StringUtils.isBlank(viewName)  ) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);

        File viewFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));

        return new GPView(viewFile);

    }


}
