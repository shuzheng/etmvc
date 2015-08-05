package com.et.mvc.renderer;

import com.et.mvc.TextView;
import com.et.mvc.ViewContext;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class TextViewRenderer extends AbstractViewRenderer<TextView>{
    public void renderView(TextView view, ViewContext viewContext) throws Exception{
        if (view == null){
            return;
        }
        
        HttpServletResponse response = viewContext.getResponse();
        if (view.getContentType() != null){
            response.setContentType(view.getContentType());
        }
        PrintWriter out = response.getWriter();
        out.print(view.toString());
        out.close();
    }
}
