package com.et.mvc.renderer;

import com.et.mvc.JspView;
import com.et.mvc.ViewContext;
import java.util.Map.Entry;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspViewRenderer extends AbstractViewRenderer<JspView>{
    public void renderView(JspView view, ViewContext viewContext) throws Exception{
        if (view == null){
            return;
        }
        
        HttpServletRequest request = viewContext.getRequest();
        HttpServletResponse response = viewContext.getResponse();
        String controllerPath = viewContext.getControllerPath();
        String actionName = viewContext.getActionName();
        
        if (view.getContentType() != null){
            response.setContentType(view.getContentType());
        }
        
        for(Entry<String,Object> entry: view.getAttributes().entrySet()){
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        
        
        String path = view.getPath();
        if (path == null){
            path = viewContext.getViewBasePath() + controllerPath.toLowerCase() + "/" + actionName + ".jsp";
        }
        else{
            if (path.indexOf("/") == -1){
                path = viewContext.getViewBasePath() + controllerPath.toLowerCase() + "/" + path;
            }
            else if (path.startsWith("/")){
                path = viewContext.getViewBasePath() + path;
            }
            else{
                path = viewContext.getViewBasePath() + "/" + path;
            }
            if (!path.endsWith(".jsp")){
                path += ".jsp";
            }
        }

        RequestDispatcher rd = request.getRequestDispatcher(path);
        rd.forward(request, response);
    }
}
