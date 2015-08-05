package com.et.mvc;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 同spring集成的分发器
 * @author stworthy
 */
public class SpringDispatcherFilter extends DispatcherFilter{
    private WebApplicationContext webApplicationContext;
    
    @Override
    public void init(FilterConfig config){
        super.init(config);
        this.setWebApplicationContext(initWebApplicationContext(config.getServletContext()));
    }
    
    protected WebApplicationContext initWebApplicationContext(ServletContext servletContext){
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
    
    @Override
    public Object getController(String controllerClassName) throws Exception{
        if (this.webApplicationContext.containsBean(controllerClassName)){
            return this.webApplicationContext.getBean(controllerClassName);
        }
        else{
            return super.getController(controllerClassName);
        }
    }

    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

}
