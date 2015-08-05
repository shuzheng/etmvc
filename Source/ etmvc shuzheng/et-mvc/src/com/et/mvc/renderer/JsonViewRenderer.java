package com.et.mvc.renderer;

import com.et.mvc.JsonView;
import com.et.mvc.ViewContext;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class JsonViewRenderer extends AbstractViewRenderer<JsonView>{
    public void renderView(JsonView view, ViewContext viewContext) throws Exception{
        if (view == null){
            return;
        }
        
        HttpServletResponse response = viewContext.getResponse();
        if (view.getContentType() != null){
            response.setContentType(view.getContentType());
        }
        else{
            response.setContentType("application/json;charset=utf-8");
        }
        PrintWriter out = response.getWriter();
        out.print(view.toString());
        out.close();
    }
}
