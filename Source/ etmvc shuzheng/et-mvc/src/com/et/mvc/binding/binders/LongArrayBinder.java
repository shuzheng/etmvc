package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class LongArrayBinder implements DataBinder{

	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}
		
		String[] values = (String[])ctx.getRequest().getParameterMap().get(parameterName);
		if (values == null){
			return null;
		} else if (ctx.getParameterType().equals(long[].class)){
			long[] aa = new long[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = 0;
				} else {
					aa[i] = Long.parseLong(values[i]);
				}
			}
			return aa;
		} else {
			Long[] aa = new Long[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = null;
				} else {
					aa[i] = Long.parseLong(values[i]);
				}
			}
			return aa;
			
		}
	}

}
