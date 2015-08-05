package com.et.mvc;

import com.et.mvc.renderer.FreeMarkerViewRenderer;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用FreeMarker模板的视图类<br/>
 * 范例：
 * <pre>
 * FreeMarkerView view = new FreeMarkerView();
 * view.setAttribute("info", "...");
 * return view;
 * </pre>
 * 或者:
 * <pre>
 * request.setAttribute("info", "...");
 * return new FreeMarkerView();
 * </pre>
 * @author stworthy
 */
@ViewRendererClass(FreeMarkerViewRenderer.class)
public class FreeMarkerView extends View{
    private String path;
    private Map<String,Object> attributes = new HashMap<String,Object>();
    
    public FreeMarkerView(){
        this.path = null;
    }
    
    public FreeMarkerView(String path){
        this.path = path;
    }
    
    public FreeMarkerView(String path, String key, Object value){
        this.path = path;
        this.setAttribute(key, value);
    }
    
    public FreeMarkerView(String key, Object value){
        this.path = null;
        this.setAttribute(key, value);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttribute(String key, Object value){
        attributes.put(key, value);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
