package com.et.mvc.routing;

/**
 * 路由定义，由默认路由处理器处理
 * @author stworthy
 */
public class Route {
    private String url;
    private String controller;
    private String action;
    private Class<?> handlerClass;
    
    public Route(String url, Class<?> handlerClass){
        this.url = url;
        this.handlerClass = handlerClass;
    }
    
    public Route(String url, String controller, Class<?> handlerClass){
        this.url = url;
        this.controller = controller;
        this.handlerClass = handlerClass;
    }

    public Route(String url, String controller, String action, Class<?> handlerClass){
        this.url = url;
        this.controller = controller;
        this.action = action;
        this.handlerClass = handlerClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<?> getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(Class<?> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
