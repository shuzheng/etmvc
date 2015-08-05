package com.et.mvc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 一般的HTTP请求包装类
 * @author stworthy
 */
public class NormalRequest extends HttpServletRequestWrapper{
    private Map<String,String[]> parameters = new HashMap<String,String[]>();
    
    public NormalRequest(HttpServletRequest request, Map<String,String> params) {
        super(request);
        for(Object entry: request.getParameterMap().entrySet()){
            Entry<?,?> e = (Entry<?,?>)entry;
            String key = (String)e.getKey();
            String[] value = (String[])e.getValue();
            parameters.put(key, value);
        }
        for(Entry<String,String> param: params.entrySet()){
            parameters.put(param.getKey(), new String[]{param.getValue()});
        }
    }
    
    @Override
    public String getParameter(String name){
        String[] params = parameters.get(name);
        return params == null ? null : params[0];
    }
    
    @Override
    public String[] getParameterValues(String name){
        return parameters.get(name);
    }
    
    @Override
    public Map<?,?> getParameterMap(){
//        return parameters;
        return Collections.unmodifiableMap(parameters);
    }
    
    @Override
    public Enumeration<?> getParameterNames(){
        Enumeration<?> names = new Enumeration<?>(){
            private Iterator<String> it = parameters.keySet().iterator();
            public boolean hasMoreElements(){
                return it.hasNext();
            }
            public Object nextElement(){
                return it.next();
            }
        };
        return names;
    }
}
