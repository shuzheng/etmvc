package com.et.mvc.renderer;

import com.et.mvc.ViewContext;
import java.io.PrintWriter;

public class StringViewRenderer extends AbstractViewRenderer<String>{
    public void renderView(String view, ViewContext viewContext) throws Exception{
        if (view == null){
            return;
        }
        PrintWriter out = viewContext.getResponse().getWriter();
        out.print(view);
        out.close();
    }
}
