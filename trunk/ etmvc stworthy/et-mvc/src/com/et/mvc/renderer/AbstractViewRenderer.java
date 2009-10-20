package com.et.mvc.renderer;

import com.et.mvc.ViewContext;
import com.et.mvc.ViewRenderer;

public abstract class AbstractViewRenderer<T> implements ViewRenderer<T>{
    public void render(T viewObject, ViewContext viewContext) throws Exception{
        if (viewContext.getResponse().isCommitted() == true){
            return;
        }
        renderView(viewObject, viewContext);
    }
    
    protected abstract void renderView(T viewObject, ViewContext viewContext) throws Exception;
}
