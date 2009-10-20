package com.et.mvc.routing;

import java.util.HashMap;
import java.util.Map;

/**
 * 路由选择结果
 * @author stworthy
 */
public class RouteResult {
    private String subPackageName;
    private String controllerPath;
    private String controllerName;
    private String actionName;
    private Map<String,String> params = new HashMap<String,String>();

    public String getSubPackageName() {
        return subPackageName;
    }

    public void setSubPackageName(String subPackageName) {
        this.subPackageName = subPackageName;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }


}
