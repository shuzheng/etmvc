package com.et.mvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视图上下文
 * @author stworthy
 */
public class ViewContext {
    private String viewBasePath;
    private String controllerPath;
    private String actionName;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    public String getViewBasePath() {
        return viewBasePath;
    }

    public void setViewBasePath(String viewBasePath) {
        this.viewBasePath = viewBasePath;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }
}
