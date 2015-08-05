package com.et.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.et.mvc.binding.Bind;
import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;
import com.et.mvc.binding.binders.ObjectBinder;
import com.et.mvc.routing.RouteResult;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * 控制器基类，所有的控制器类必须继承该类
 * @author stworthy
 */
public class Controller {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ServletContext servletContext;
    protected HttpSession session;
    protected String controllerName;
    protected String actionName;
    protected String controllerPath;
    protected Method actionMethod;
    protected Class<?> actionReturnType;
    protected Exception exception;
    protected Flash flash = new Flash();
    
    protected void init(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, RouteResult routeResult) throws Exception{
        this.setServletContext(servletContext);
        this.setRequest(request);
        this.setResponse(response);
        this.setSession(request.getSession());
        this.setControllerName(routeResult.getControllerName());
        this.setActionName(routeResult.getActionName());
        this.setControllerPath(routeResult.getControllerPath());
        
        for(Method method: this.getClass().getMethods()){
            if (method.getName().equals(getActionName())){
                setActionMethod(method);
                break;
            }
        }
//        actionMethod = this.getClass().getMethod(actionName);
        setActionReturnType(getActionMethod().getReturnType());
        
        Flash sessionFlash = (Flash)this.getSession().getAttribute(Flash.FLASH_KEY);
        if (sessionFlash != null){
            setFlash(sessionFlash);
        }
    }
    
    protected void updateModel(Object model) throws Exception {
    	updateModel(model, "");
    }
    
    protected void updateModel(Object model, String prefix) throws Exception {
    	Class<?> clasz = model.getClass();
    	try{
    		Method method = clasz.getMethod("isProxy");
			Boolean result = (Boolean) method.invoke(model);
			if (result == true) {
				clasz = clasz.getSuperclass();
			}
    		
    	} catch (Exception ex) {
    	}
    	
    	for(Field f : clasz.getDeclaredFields()) {
    		BindingContext ctx = new BindingContext();
    		ctx.setParameterName(f.getName());
    		ctx.setParameterType(f.getType());
    		ctx.setRequest(request);
    		ctx.setPrefix(prefix);
    		
    		DataBinder binder = DataBinders.getDataBinder(f.getType());
    		if (binder != null) {
    			String pname = ctx.getParameterName();
    			if (!ctx.getPrefix().equals("")) {
    				pname = ctx.getPrefix() + "." + pname;
    			}
    			if (request.getParameterMap().containsKey(pname)) {
	    			f.setAccessible(true);
	    			f.set(model, binder.bind(ctx));
    			}
    		} else {
            	BindingContext bc = new BindingContext();
            	bc.setParameterName(f.getName());
            	bc.setParameterType(f.getType());
            	bc.setRequest(ctx.getRequest());
            	if (ctx.getPrefix().equals("")){
            		bc.setPrefix(f.getName());
            	} else {
            		bc.setPrefix(ctx.getPrefix() + "." + f.getName());
            	}
            	
    			Object value = new ObjectBinder().bind(bc);
    			if (value != null) {
    				f.setAccessible(true);
    				Object obj = f.get(model);
    				if (obj != null) {
	    				updateModel(obj, bc.getPrefix());
	    				f.set(model, obj);
    				} else {
    					f.set(model, value);
    				}
    			}
    		}
    	}
    }
    
    protected Object invoke() throws Exception{
        Class<?>[] types = getActionMethod().getParameterTypes();
        Annotation[][] annotations = getActionMethod().getParameterAnnotations();
        Object[] parameters = new Object[types.length];

        if (types.length > 0){
            Paranamer paranamer = new AdaptiveParanamer();
            String[] names = paranamer.lookupParameterNames(getActionMethod());
            for(int i=0; i<parameters.length; i++){
                BindingContext ctx = new BindingContext();
                ctx.setParameterName(names[i]);
                ctx.setParameterType(types[i]);
                ctx.setRequest(getRequest());
                Annotation[] ann = annotations[i];
                if (ann.length > 0 && ann[0] instanceof Bind){
                    Bind bind = (Bind)ann[0];
                    ctx.setPrefix(bind.prefix());
                }
                else{
                    ctx.setPrefix("");
                }
                DataBinder binder = DataBinders.getDataBinder(types[i]);
                if (binder == null){
                    binder = new ObjectBinder();
                }
                parameters[i] = binder.bind(ctx);
            }
        }

        Object result = getActionMethod().invoke(this, parameters);
        
        if (getSession().getAttribute(Flash.FLASH_KEY) == null){
            if (getFlash().getAttributes().size() > 0){
                for(Entry<String,Object> attr: getFlash().getAttributes().entrySet()){
                    getRequest().setAttribute(attr.getKey(), attr.getValue());
                }
                getSession().setAttribute(Flash.FLASH_KEY,getFlash());
            }
        }
        else{
            if (getFlash().getAttributes().size() > 0){
                for(Entry<String,Object> attr: getFlash().getAttributes().entrySet()){
                    getRequest().setAttribute(attr.getKey(), attr.getValue());
                }
            }
            getFlash().sweep();
            if (getFlash().getAttributes().size() == 0){
                getSession().removeAttribute(Flash.FLASH_KEY);
            }
        }
        
        return result;
    }

    protected void forward(String path) throws Exception{
        if (path.indexOf("/") == -1){
            path = getControllerPath() + "/" + path;
        }
        if (!path.startsWith("/")){
            path = "/" + path;
        }
        
        RequestDispatcher rd = getRequest().getRequestDispatcher(path);
        rd.forward(getRequest(), getResponse());
    }

    protected void redirect(String path) throws Exception{
        if (path.indexOf("/") == -1){
            path = getRequest().getContextPath() + getControllerPath() + "/" + path;
        }
        else{
            if (path.startsWith("/")){
                path = getRequest().getContextPath() + path;
            }
            else{
                path = getRequest().getContextPath() + "/" + path;
            }
        }
        
        getResponse().sendRedirect(path);
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

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
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

    public Method getActionMethod() {
        return actionMethod;
    }

    public void setActionMethod(Method actionMethod) {
        this.actionMethod = actionMethod;
    }

    public Class<?> getActionReturnType() {
        return actionReturnType;
    }

    public void setActionReturnType(Class<?> actionReturnType) {
        this.actionReturnType = actionReturnType;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Flash getFlash() {
        return flash;
    }

    public void setFlash(Flash flash) {
        this.flash = flash;
    }
}
