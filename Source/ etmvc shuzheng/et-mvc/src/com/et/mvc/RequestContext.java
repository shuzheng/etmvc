package com.et.mvc;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 请求的上下文
 * @author stworthy
 */
public class RequestContext {
    private String controllerBasePackage;
    private List<String> controllerPaths;
    private HttpServletRequest request;

    public String getControllerBasePackage() {
        return controllerBasePackage;
    }

    public void setControllerBasePackage(String controllerBasePackage) {
        this.controllerBasePackage = controllerBasePackage;
    }

    public List<String> getControllerPaths() {
        return controllerPaths;
    }

    public void setControllerPaths(List<String> controllerPaths) {
        this.controllerPaths = controllerPaths;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
