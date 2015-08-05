package com.et.mvc.renderer;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.et.mvc.FreeMarkerView;
import com.et.mvc.ViewContext;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkerViewRenderer extends AbstractViewRenderer<FreeMarkerView>{
    public static final String FREEMARKER_CONFIG = "freemarker_config";
    
    public void initConfiguration(Configuration cfg, ViewContext viewContext) throws Exception {
        cfg.setServletContextForTemplateLoading(viewContext.getServletContext(), viewContext.getViewBasePath());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }
    
    public void renderView(FreeMarkerView view, ViewContext viewContext) throws Exception{
        if (view == null){
            return;
        }
        ServletContext servletContext = viewContext.getServletContext();
        Configuration cfg = (Configuration)servletContext.getAttribute(FreeMarkerViewRenderer.FREEMARKER_CONFIG);
        if (cfg == null){
            cfg = new Configuration();
            cfg.setDefaultEncoding("UTF-8");
            initConfiguration(cfg, viewContext);
            servletContext.setAttribute(FreeMarkerViewRenderer.FREEMARKER_CONFIG, cfg);
        }
        
        HttpServletRequest request = viewContext.getRequest();
        HttpServletResponse response = viewContext.getResponse();
        String controllerPath = viewContext.getControllerPath();
        
        Enumeration<?> attrNames = request.getAttributeNames();
        while(attrNames.hasMoreElements()){
            String attr = (String)attrNames.nextElement();
            Object value = request.getAttribute(attr);
            view.setAttribute(attr, value);
        }
        
        String path = view.getPath();
        if (path == null){
            path = controllerPath.toLowerCase() + "/" + viewContext.getActionName() + ".ftl";
        }
        else{
            if (path.indexOf("/") == -1){
                path = controllerPath.toLowerCase() + "/" + path;
            }
            else if (!path.startsWith("/")){
                path = "/" + path;
            }
            if (!path.endsWith(".ftl")){
                path += ".ftl";
            }
        }
        
        if (view.getContentType() != null){
            response.setContentType(view.getContentType());
        }
        
        Template t = cfg.getTemplate(path, request.getLocale());
        t.process(view.getAttributes(), response.getWriter());
    }
}
