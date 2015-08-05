package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;
import java.lang.reflect.Field;

public class ObjectBinder implements DataBinder{
    public Object bind(BindingContext ctx) throws Exception{
        Object obj = null;
        try {
        	obj = ctx.getParameterType().newInstance();
        } catch (InstantiationException ex) {
        	return null;
        }
        
        int bindCount = 0;	//success bind field count
        
        for(Field f: ctx.getParameterType().getDeclaredFields()){
            DataBinder binder = DataBinders.getDataBinder(f.getType());
            if (binder != null){
                BindingContext bc = new BindingContext();
                bc.setParameterName(f.getName());
                bc.setParameterType(f.getType());
                bc.setRequest(ctx.getRequest());
                bc.setPrefix(ctx.getPrefix());

                Object value = binder.bind(bc);
                if (value != null) {
                	bindCount ++;
                }
                f.setAccessible(true);
                f.set(obj, value);
            } else {
            	BindingContext bc = new BindingContext();
            	bc.setParameterName(f.getName());
            	bc.setParameterType(f.getType());
            	bc.setRequest(ctx.getRequest());
            	if (ctx.getPrefix().equals("")){
            		bc.setPrefix(f.getName());
            	} else {
            		bc.setPrefix(ctx.getPrefix() + "." + f.getName());
            	}
            	
            	f.setAccessible(true);
            	f.set(obj, bind(bc));
            }
        }
        
        if (bindCount == 0) {
        	return null;
        } else {
        	return obj;
        }
    }
}
