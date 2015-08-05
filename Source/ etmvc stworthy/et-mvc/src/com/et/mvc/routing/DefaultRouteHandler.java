package com.et.mvc.routing;

import com.et.mvc.RequestContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认路由处理器
 * @author stworthy
 */
public class DefaultRouteHandler implements RouteHandler{
    private String getControllerPath(RequestContext requestContext, String url){
        String thePath = null;
        for(String path: requestContext.getControllerPaths()){
            if (url.toLowerCase().startsWith(path.toLowerCase())){
                if (thePath == null || thePath.length() < path.length()){
                    thePath = path;
                }
            }
        }
        return thePath;
    }
    
    private String buildUrlPath(List<String> parts){
        String path = "";
        for(String part: parts){
            path += "/" + part;
        }
        return path;
    }
    
    public RouteResult getResult(RequestContext requestContext, Route route){
        String contextPath = requestContext.getRequest().getContextPath();
        String url = requestContext.getRequest().getRequestURI().substring(contextPath.length());
        if (url.contains(".")){
            return null;
        }
        
        //分割URL路径各组成部分
        List<String> urlParts = new ArrayList<String>();
        for(String urlPart: url.substring(1).split("/")){
            urlParts.add(urlPart);
        }
        
        //分割路由匹配规则各组成部分
        String routeUrl = route.getUrl();
        if (routeUrl.startsWith("/")){
            routeUrl = routeUrl.substring(1);
        }
        String[] routeParts = routeUrl.split("/");
        
        RouteResult routeResult = new RouteResult();
        routeResult.setSubPackageName("");
        
        for(String routePart: routeParts){
            if (routePart.equals("$controller")){
                String aurl = buildUrlPath(urlParts);
                String path = getControllerPath(requestContext, aurl);  //获取控制器路径
                if (path != null){
                    routeResult.setControllerPath(path);
                    int pos = path.lastIndexOf("/");
                    String packageName = path.substring(0, pos);
                    if (packageName.startsWith("/")){
                        packageName = packageName.substring(1).replaceAll("/", "\\.");
                    }
                    else{
                        packageName = packageName.replaceAll("/", "\\.");
                    }
                    routeResult.setSubPackageName(packageName);
                    routeResult.setControllerName(path.substring(pos + 1));
                    
                    //移除URL中已匹配的部分
                    if (packageName.equals("")){    //仅匹配控制器
                        urlParts.remove(0);
                    }
                    else if (packageName.contains(".") == false){   //匹配一个子包及一个控制器
                        urlParts.remove(0);
                        urlParts.remove(0);
                    }
                    else{
                        for(int i=0; i<packageName.split("\\.").length; i++){ //匹配多个子包及一个控制器
                            urlParts.remove(0);
                        }
                        urlParts.remove(0);
                    }
                }
                else{
                    return null;
                }
            }
            else if (routePart.equals("$action")){
                try{
                    routeResult.setActionName(urlParts.remove(0));
                }
                catch(IndexOutOfBoundsException ex){
                    routeResult.setActionName("index");
                }
            }
            else if (routePart.equals("$id")){
                try{
                    routeResult.getParams().put("id", urlParts.remove(0));
                }
                catch(IndexOutOfBoundsException ex){
                }
            }
            else if (routePart.startsWith("$")){
                String paramName = routePart.substring(1);
                String paramValue = null;
                try{
                    paramValue = urlParts.remove(0);
                }
                catch(IndexOutOfBoundsException ex){
                }
                routeResult.getParams().put(paramName, paramValue);
            }
            else {  //匹配常量部分
                if (urlParts.isEmpty()){
                    return null;
                }
                String urlPart = urlParts.remove(0);
                if (routePart.toLowerCase().equals(urlPart.toLowerCase()) == false){
                    return null;
                }
            }
        }
        
        if (route.getController() != null){
            String controller = route.getController();
            if (controller.startsWith("/")){
                controller = controller.substring(1);
            }
            controller = controller.replaceAll("/", "\\.");
            if (controller.contains(".")){  //包含子包的控制器
            	int pos = controller.lastIndexOf(".");
            	String packageName = controller.substring(0, pos);
            	routeResult.setSubPackageName(packageName);
            	routeResult.setControllerName(controller.substring(pos + 1));
            	routeResult.setControllerPath("/" + controller.replaceAll("\\.", "/"));
            } else {
            	routeResult.setControllerName(controller);
            	routeResult.setSubPackageName("");
            	routeResult.setControllerPath("/" + controller);
            }
        }
        if (route.getAction() != null){
            routeResult.setActionName(route.getAction());
        }
        return routeResult;
    }
}
