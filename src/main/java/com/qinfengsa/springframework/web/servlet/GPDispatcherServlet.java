package com.qinfengsa.springframework.web.servlet;

import com.qinfengsa.demo.action.MyAction;
import com.qinfengsa.springframework.annotation.GPController;
import com.qinfengsa.springframework.annotation.GPRequestMapping;
import com.qinfengsa.springframework.context.GPApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GPDispatcherServlet
 * @author: qinfengsa
 * @date: 2019/4/18 23:58
 */
@Slf4j
public class GPDispatcherServlet extends HttpServlet {
    /**
     * Servlet 启动加载参数
     */
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    /**
     * HandlerMapping
     */
    private List<GPHandlerMapping> handlerMappings = new ArrayList<>();


    /**
     * 通过HandlerMapping找到对应的HandlerAdapter
     */
    private Map<GPHandlerMapping,GPHandlerAdapter> handlerAdapters = new HashMap<>();
    private List<GPViewResolver> viewResolvers = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            this.doDispatch(req,resp);
        }catch(Exception e){
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 处理url请求
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        GPHandlerMapping handler = getHandler(req);

        if(handler == null){
            processDispatchResult(req,resp,new GPModelAndView("404"));
            return;
        }

        //2、准备调用前的参数
        GPHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        GPModelAndView mv = ha.handle(req,resp,handler);

        // 这一步才是真正的输出
        processDispatchResult(req, resp, mv);
    }

    /**
     * 匹配对应的handler
     * @param handler
     * @return
     */
    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;

        }
        GPHandlerAdapter ha = this.handlerAdapters.get(handler);
        if (ha.supports(handler)) {
            return ha;
        }

        return null;
    }

    /**
     * 输出返回的结果
     * @param req
     * @param resp
     * @param mv
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GPModelAndView mv) throws Exception {

        if (Objects.isNull(mv)) {
            return;
        }
        if(this.viewResolvers.isEmpty()){return;}

        for (GPViewResolver viewResolver : this.viewResolvers) {
            GPView view = viewResolver.resolveViewName(mv.getViewName() );
            view.render(mv.getModel(),req,resp);
            return;
        }
    }

    /**
     * 用url匹配对应的handerMapping
     * @param req
     * @return
     */
    private GPHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");


        for (GPHandlerMapping handler: this.handlerMappings) {
            try{
                Matcher matcher = handler.getUrl().matcher(url);
                // 如果没有匹配上继续下一个匹配
                if (!matcher.matches()) {
                    continue;
                }
                return handler;
            }catch(Exception e){
                log.error(e.getMessage(),e);
            }
        }

        return null;
    }

    /**
     * 重写初始化方法
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // 加载ApplicationContext ,初始化IOC容器并进行依赖注入
        GPApplicationContext context = initApplicationContext(config);

        // servlet初始化策略
        initStrategies(context);
    }

    /**
     * servlet初始化策略，加载9大组件
     * @param context
     */
    private void initStrategies(GPApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);
        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context); 
        //初始化视图预处理器
        initRequestToViewNameTranslator(context); 

        //初始化视图转换器，必须实现
        initViewResolvers(context);
        
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(GPApplicationContext context) {
    }

    /**
     * 视图转换
     * @param context
     */
    private void initViewResolvers(GPApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            this.viewResolvers.add(new GPViewResolver(templateRoot));
        }
        
    }

    private void initRequestToViewNameTranslator(GPApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GPApplicationContext context) {
    }

    /**
     * 初始化handerAdapter
     * @param context
     */
    private void initHandlerAdapters(GPApplicationContext context) {

        if (this.handlerMappings.isEmpty()) {
            return;
        }
        for (GPHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new GPHandlerAdapter());
        }
    }

    /**
     * 初始化handlerMapping
     * @param context
     */
    private void initHandlerMappings(GPApplicationContext context) {

        String [] beanNames = context.getBeanDefinitionNames();

        try {
            StringBuilder sbHead = new StringBuilder();
            StringBuilder sbUrl = new StringBuilder();
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(GPController.class)) {
                    continue;
                }


                sbHead.setLength(0);
                if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);

                    if (StringUtils.isNotBlank(requestMapping.value())) {
                        sbHead.append("/");
                        sbHead.append(requestMapping.value());
                    }
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    // 没有GPRequestMapping注解,跳过
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }
                    sbUrl.setLength(0);
                    sbUrl.append(sbHead);
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    if (StringUtils.isNotBlank(requestMapping.value())) {
                        sbUrl.append("/");
                        sbUrl.append(requestMapping.value());
                    }
                    String url = sbUrl.toString();
                    url = url.replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(url);
                    handlerMappings.add(new GPHandlerMapping(pattern,controller,method));
                    log.info("Mapped :{}->{}", url ,method);
                }

            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        
    }

    private void initThemeResolver(GPApplicationContext context) {
    }

    private void initLocaleResolver(GPApplicationContext context) {
    }


    private void initMultipartResolver(GPApplicationContext context) {

    }

    /**
     * 初始化IOC容器并进行依赖注入
     * @param config 配置信息
     * @return
     */
    private GPApplicationContext initApplicationContext(ServletConfig config) {
        config.getInitParameter(CONTEXT_CONFIG_LOCATION);
        GPApplicationContext context = new GPApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        return context;
    }
}
