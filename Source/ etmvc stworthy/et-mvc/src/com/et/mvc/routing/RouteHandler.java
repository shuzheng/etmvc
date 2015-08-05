package com.et.mvc.routing;

import com.et.mvc.RequestContext;

/**
 * 路由处理接口
 * @author stworthy
 */
public interface RouteHandler {
	/**
	 * 获取路由选择结果
	 * @param requestContext 请求上下文
	 * @param route 路由定义
	 * @return 路由选择结果
	 */
    public RouteResult getResult(RequestContext requestContext, Route route);
}
