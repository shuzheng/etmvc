package com.et.mvc.renderer;

import com.et.mvc.ViewContext;
import javax.servlet.RequestDispatcher;

public class VoidViewRenderer extends AbstractViewRenderer<Void>{
    public void renderView(Void viewObject, ViewContext viewContext) throws Exception{
        String path = viewContext.getViewBasePath() + viewContext.getControllerPath().toLowerCase() + "/" + viewContext.getActionName() + ".jsp";
        RequestDispatcher rd = viewContext.getRequest().getRequestDispatcher(path);
        rd.forward(viewContext.getRequest(), viewContext.getResponse());
    }
}
