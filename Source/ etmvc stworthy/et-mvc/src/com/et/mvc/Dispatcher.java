package com.et.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.et.mvc.filter.Filter;
import com.et.mvc.filter.FilterUtils;
import com.et.mvc.renderer.StringViewRenderer;
import com.et.mvc.renderer.VoidViewRenderer;
import com.et.mvc.util.Json;

/**
 * 分发器类
 * @author stworthy
 */
public class Dispatcher {
	private static Log log = LogFactory.getLog("etmvc");
	
	private static String controllerBasePackage;
	private static String viewBasePath;
	private static List<String> controllerPaths;

	public static String getControllerBasePackage() {
		return controllerBasePackage;
	}

	public static void setControllerBasePackage(String aControllerBasePackage) {
		controllerBasePackage = aControllerBasePackage;
	}

	public static String getViewBasePath() {
		return viewBasePath;
	}

	public static void setViewBasePath(String aViewBasePath) {
		viewBasePath = aViewBasePath;
	}

	public static List<String> getControllerPaths() {
		return controllerPaths;
	}

	public static void setControllerPaths(List<String> aControllerPaths) {
		controllerPaths = aControllerPaths;
	}

	/**
	 * 反序执行AroundFilters的after方法，所有的过滤器都不执行到，但只要有一个返回false，则函数返回false，否则返回true
	 */
	private boolean afterInvokeAroundFilters(Controller controller,
			List<Filter> arounds) throws Exception {
		boolean result = true;
		Exception exception = null;
		for (int i = arounds.size() - 1; i >= 0; i--) {
			Filter filter = arounds.get(i);
			try {
				boolean ret = filter.afterInvoke(controller);
				if (!ret) {
					result = false;
				}
			} catch (Exception ex) {
				if (exception == null) {
					exception = ex;
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
		return result;
	}

	public void service(Controller controller) throws Exception {
		long t1 = System.currentTimeMillis();
		if (log.isDebugEnabled()){
			String logInfo = "Processing " + controller.request.getRequestURL()
					+ " (for " + controller.request.getRemoteAddr() + ")"
					+ " [" + controller.request.getMethod() + "]\n"
					+ " Session ID: " + controller.session.getId() + "\n"
					+ " controller: " + controller.controllerName + "\n"
					+ " action:" + controller.actionName + "\n"
					+ " Parameters: " + Json.toJson(Json.castMap(controller.request.getParameterMap()));
			log.debug(logInfo);
		}
		
		// Before和Around过滤器
		List<Filter> arounds = new ArrayList<Filter>(); // 已执行的Around过滤器
		List<Filter> chains = FilterUtils.getFilterChain(controller);
		boolean chainBroken = false;
		try {
			for (int i = 0; i < chains.size(); i++) {
				Filter filter = chains.get(i);
				if (filter.canInvoke(controller.getActionName())) {
					boolean ret = filter.beforeInvoke(controller);
					if (ret == true && filter.getAroundFilter() != null) {
						arounds.add(filter);
					}
					if (!ret) {
						chainBroken = true;
						break;
					}
				}
			}
		} catch (Exception ex) {
			try {
				afterInvokeAroundFilters(controller, arounds);
			} catch (Exception ex2) {
			}
			throw ex;
		}
		if (chainBroken == true) { // 过滤器链执行中断
			afterInvokeAroundFilters(controller, arounds);
			return;
		}

		// 执行控制器方法
		Object actionResult = null;
		try {
			actionResult = controller.invoke();
		} catch (Exception ex) {
			controller.exception = ex;
		}

		// Around过滤器after方法，反序执行
		if (afterInvokeAroundFilters(controller, arounds) == false) {
			return; // 不继续执行后续代码
		}

		if (controller.exception != null) {
			throw controller.exception;
		}

		// After过滤器，顺序执行
		for (int i = 0; i < chains.size(); i++) {
			Filter filter = chains.get(i);
			if (filter.canInvoke(controller.getActionName())
					&& filter.getAfterFilter() != null) {
				boolean ret = filter.afterInvoke(controller);
				if (!ret) {
					return;
				}
			}
		}

		// 渲染视图
		ViewContext viewContext = new ViewContext();
		viewContext.setRequest(controller.getRequest());
		viewContext.setResponse(controller.getResponse());
		viewContext.setServletContext(controller.getServletContext());
		viewContext.setViewBasePath(viewBasePath);
		viewContext.setControllerPath(controller.getControllerPath());
		viewContext.setActionName(controller.getActionName());

		renderView(controller.getActionReturnType(), actionResult, viewContext);

		long t2 = System.currentTimeMillis();
		if (log.isDebugEnabled()){
			String logInfo = "Completed in " + ((t2-t1)/1000.0) + "s "
					+ "[" + controller.request.getRequestURL() + "]\n";
			log.debug(logInfo);
		}
	}

	protected void renderView(Class<?> viewClass, Object viewObject,
			ViewContext viewContext) throws Exception {
		if (viewClass.getName().equals("void")) {
			VoidViewRenderer renderer = new VoidViewRenderer();
			renderer.render((Void) viewObject, viewContext);
		} else if (viewClass.getName().equals("java.lang.String")) {
			StringViewRenderer renderer = new StringViewRenderer();
			renderer.render((String) viewObject, viewContext);
		} else if (viewObject instanceof View) {
			ViewRendererClass vrc = viewObject.getClass().getAnnotation(ViewRendererClass.class);
			
			Class<?> clasz = ((View)viewObject).getRendererClass();
			if (clasz == null && vrc != null) {
				clasz = vrc.value();
			}
			if (clasz != null) {
				Object viewRenderer = clasz.newInstance();
				Method method = clasz.getMethod("render", Object.class, ViewContext.class);
				method.invoke(viewRenderer, viewObject, viewContext);
			}
			
//			if (vrc != null) {
//				Class<?> clasz = vrc.value();
//				Object viewRenderer = clasz.newInstance();
//				Method method = clasz.getMethod("render", Object.class,
//						ViewContext.class);
//				method.invoke(viewRenderer, viewObject, viewContext);
//			}
		}
	}
}
