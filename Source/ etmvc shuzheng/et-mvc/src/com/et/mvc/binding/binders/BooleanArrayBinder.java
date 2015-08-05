package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class BooleanArrayBinder implements DataBinder{

	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}
		
		String[] values = (String[])ctx.getRequest().getParameterMap().get(parameterName);
		if (values == null){
			return null;
		} else if (ctx.getParameterType().equals(boolean[].class)){
			boolean[] aa = new boolean[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = false;
				} else {
					aa[i] = Boolean.parseBoolean(values[i]);
				}
			}
			return aa;
		} else {
			Boolean[] aa = new Boolean[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = null;
				} else {
					aa[i] = Boolean.parseBoolean(values[i]);
				}
			}
			return aa;
			
		}
	}

}
