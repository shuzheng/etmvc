package com.et.mvc.routing;

import com.et.mvc.RequestContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 路由表管理类
 * @author stworthy
 */
public class RouteTable {
    private static List<Route> routes = new ArrayList<Route>();
    
    static{
        Route defaultRoute = new Route("$controller/$action/$id", DefaultRouteHandler.class);
        routes.add(defaultRoute);
    }
    
    public static RouteResult selectRoute(RequestContext requestContext) throws Exception{
        for(Route route: routes){
            RouteHandler handler = (RouteHandler)route.getHandlerClass().newInstance();
            RouteResult result = handler.getResult(requestContext, route);
            if (result != null){
                return result;
            }
        }
        return null;
    }

    public static List<Route> getRoutes() {
        return routes;
    }

    public static void addRoute(Route route){
        routes.add(route);
    }
    
    public static void addRoute(int pos, Route route){
        routes.add(pos, route);
    }
}
