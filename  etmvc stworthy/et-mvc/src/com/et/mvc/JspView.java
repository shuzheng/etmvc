package com.et.mvc;

import com.et.mvc.renderer.JspViewRenderer;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用JSP的视图类
 * <pre>
 * request.setAttribute("info","...");
 * return new JspView("/user/show.jsp");
 * </pre>
 * 或者
 * <pre>
 * JspView view = new JspView("/user/show.jsp");
 * view.setAttribute("info","...");
 * return view;
 * </pre>
 * @author stworthy
 */
@ViewRendererClass(JspViewRenderer.class)
public class JspView extends View{
    private String path;
    private Map<String,Object> attributes = new HashMap<String,Object>();
    
    public JspView(){
        this.path = null;
    }
    
    public JspView(String path){
        this.path = path;
    }
    
    public JspView(String path, String key, Object value){
        this.path = path;
        this.setAttribute(key, value);
    }
    
    public JspView(String key, Object value){
        this.path = null;
        this.setAttribute(key, value);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public void setAttribute(String key, Object value){
        attributes.put(key, value);
    }

    public Map<String,Object> getAttributes(){
        return attributes;
    }
}
