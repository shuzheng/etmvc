package com.et.mvc.binding.binders;

import com.et.mvc.MultipartRequest;
import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;

public class MultipartFileBinder implements DataBinder{
    public Object bind(BindingContext ctx) throws Exception{
        MultipartRequest req = (MultipartRequest)ctx.getRequest();
        String parameterName = ctx.getParameterName();
        if (!ctx.getPrefix().equals("")){
            parameterName = ctx.getPrefix() + "." + parameterName;
        }
        return req.getFile(parameterName);
    }
}
