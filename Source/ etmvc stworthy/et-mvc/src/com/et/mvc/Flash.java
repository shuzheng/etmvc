package com.et.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * 闪存类，用于在二次action操作中传递数据，传递后自动丢弃
 * @author stworthy
 */
public class Flash {
    public final static String FLASH_KEY = "flash_key";
    
    private Map<String,Object> attr = new HashMap<String,Object>();
    private Map<String,Object> keepAttr = new HashMap<String,Object>();
    
    public void setAttribute(String key, Object value){
        attr.put(key, value);
    }
    
    public Map<String,Object> getAttributes(){
        return attr;
    }
    
    public Object getAttribute(String attributeName){
        return attr.get(attributeName);
    }
    
    public void keep(){
        keepAttr = attr;
    }
    
    public void keep(String key){
        keepAttr.put(key, attr.get(key));
    }
    
    protected void sweep(){
        attr = keepAttr;
        keepAttr = new HashMap<String,Object>();
    }
}
