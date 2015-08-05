package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class ShortArrayBinder implements DataBinder{

	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}
		
		String[] values = (String[])ctx.getRequest().getParameterMap().get(parameterName);
		if (values == null){
			return null;
		} else {
			if (ctx.getParameterType().equals(short[].class)) {
				short[] aa = new short[values.length];
				for(int i=0; i<values.length; i++) {
					if (DataBinders.isAllowEmpty() && values[i].equals("")) {
						aa[i] = 0;
					} else {
						aa[i] = Short.parseShort(values[i]);
					}
				}
				return aa;
			} else {
				Short[] aa = new Short[values.length];
				for(int i=0; i<values.length; i++){
					if (DataBinders.isAllowEmpty() && values[i].equals("")) {
						aa[i] = null;
					} else {
						aa[i] = Short.parseShort(values[i]);
					}
				}
				return aa;
			}
		}
	}

}
