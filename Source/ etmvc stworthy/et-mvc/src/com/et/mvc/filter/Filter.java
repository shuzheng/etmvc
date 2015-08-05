package com.et.mvc.filter;

import com.et.mvc.Controller;
import java.lang.reflect.Method;

/**
 * 过滤器类
 * @author stworthy
 */
public class Filter {
    private Class<?> clasz;
    private BeforeFilter beforeFilter;
    private AfterFilter afterFilter;
    private AroundFilter aroundFilter;
    private Object aroundInstance;
    
    public Filter(Class<?> clasz, BeforeFilter beforeFilter){
        this.clasz = clasz;
        this.beforeFilter = beforeFilter;
    }
    
    public Filter(Class<?> clasz, AfterFilter afterFilter){
        this.clasz = clasz;
        this.afterFilter = afterFilter;
    }
    
    public Filter(Class<?> clasz, AroundFilter aroundFilter){
        this.clasz = clasz;
        this.aroundFilter = aroundFilter;
    }
    
    public boolean canInvoke(String actionName){
        String[] only = new String[0];
        String[] except = new String[0];
        if (beforeFilter != null){
            only = beforeFilter.only();
            except = beforeFilter.except();
        }
        else if (afterFilter != null){
            only = afterFilter.only();
            except = afterFilter.except();
        }
        else if (aroundFilter != null){
            only = aroundFilter.only();
            except = aroundFilter.except();
        }
        
        if (only.length == 0 && except.length == 0){
            return true;
        }
        else if (only.length > 0 && isContains(only, actionName)){
            return true;
        }
        else if (except.length > 0 && !isContains(except, actionName)){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean beforeInvoke(Controller controller) throws Exception{
        if (this.getBeforeFilter() != null){
            Class<?> c = clasz;
            while(!c.equals(Controller.class)){
                try{
                    Method method = c.getDeclaredMethod(beforeFilter.execute());
                    method.setAccessible(true);
                    Boolean ret = (Boolean)method.invoke(controller);
                    return ret;
                }
                catch(NoSuchMethodException ex){
                    c = c.getSuperclass();
                }
            }
            throw new Exception("cannot find the filter:" + beforeFilter.execute());
//            Method method = clasz.getDeclaredMethod(beforeFilter.execute());
//            if (!method.getReturnType().getCanonicalName().equals("boolean")){
//                throw new Exception("the execute method must return a boolean type.");
//            }
//            method.setAccessible(true);
//            Boolean ret = (Boolean)method.invoke(controller);
//            return ret;
        }
        else if (this.getAroundFilter() != null){
            Method method = aroundFilter.execute().getMethod("before", Controller.class);
            if (!method.getReturnType().getCanonicalName().equals("boolean")){
                throw new Exception("the execute method must return a boolean type.");
            }
            method.setAccessible(true);
            Boolean ret = (Boolean)method.invoke(aroundInstance, controller);
            return ret;
        }
        else{
            return true;
        }
    }
    
    public boolean afterInvoke(Controller controller) throws Exception{
        if (this.getAfterFilter() != null){
            Class<?> c = clasz;
            while(!c.equals(Controller.class)){
                try{
                    Method method = c.getDeclaredMethod(afterFilter.execute());
                    method.setAccessible(true);
                    Boolean ret = (Boolean)method.invoke(controller);
                    return ret;
                }
                catch(NoSuchMethodException ex){
                    c = c.getSuperclass();
                }
            }
            throw new Exception("cannot find the filter:" + afterFilter.execute());
//            Method method = clasz.getDeclaredMethod(afterFilter.execute());
//            if (!method.getReturnType().getCanonicalName().equals("boolean")){
//                throw new Exception("the execute method must return a boolean type.");
//            }
//            method.setAccessible(true);
//            Boolean ret = (Boolean)method.invoke(controller);
//            return ret;
        }
        else if (this.getAroundFilter() != null){
            Method method = aroundFilter.execute().getMethod("after", Controller.class);
            if (!method.getReturnType().getCanonicalName().equals("boolean")){
                throw new Exception("the execute method must return a boolean type.");
            }
            method.setAccessible(true);
            Boolean ret = (Boolean)method.invoke(aroundInstance, controller);
            return ret;
        }
        else{
            return true;
        }
    }
    
    public BeforeFilter getBeforeFilter() {
        return beforeFilter;
    }

    public void setBeforeFilter(BeforeFilter beforeFilter) {
        this.beforeFilter = beforeFilter;
    }

    public AfterFilter getAfterFilter() {
        return afterFilter;
    }

    public void setAfterFilter(AfterFilter afterFilter) {
        this.afterFilter = afterFilter;
    }

    private boolean isContains(String[] items, String item){
        if (items == null){
            return false;
        }
        for(String s: items){
            if (s.equals(item)){
                return true;
            }
        }
        return false;
    }

    public AroundFilter getAroundFilter() {
        return aroundFilter;
    }

    public void setAroundFilter(AroundFilter aroundFilter) {
        this.aroundFilter = aroundFilter;
    }

    public Object getAroundInstance() {
        return aroundInstance;
    }

    public void setAroundInstance(Object aroundInstance) {
        this.aroundInstance = aroundInstance;
    }
}
