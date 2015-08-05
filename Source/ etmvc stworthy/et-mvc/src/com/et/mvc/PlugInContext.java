package com.et.mvc;

import java.util.Map;
import javax.servlet.ServletContext;

/**
 * 插件上下文类
 * @author stworthy
 */
public class PlugInContext {
    private ServletContext servletContext;
    private Map<String,String> configParams;

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Map<String, String> getConfigParams() {
        return configParams;
    }

    public void setConfigParams(Map<String, String> configParams) {
        this.configParams = configParams;
    }
}
