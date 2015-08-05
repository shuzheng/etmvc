package com.et.mvc.binding;

import javax.servlet.http.HttpServletRequest;

/**
 * 绑定上下文
 * @author stworthy
 */
public class BindingContext {
    private String parameterName;
    private Class<?> parameterType;
    private HttpServletRequest request;
    private String prefix;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
