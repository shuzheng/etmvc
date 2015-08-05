package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;

public class StringBinder implements DataBinder{
    public Object bind(BindingContext ctx) throws Exception{
        String parameterName = ctx.getParameterName();
        if (!ctx.getPrefix().equals("")){
            parameterName = ctx.getPrefix() + "." + parameterName;
        }
        
        return ctx.getRequest().getParameter(parameterName);
    }
}
