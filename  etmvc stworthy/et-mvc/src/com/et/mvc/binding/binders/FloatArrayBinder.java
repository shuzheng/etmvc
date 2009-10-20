package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class FloatArrayBinder implements DataBinder{

	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}
		
		String[] values = (String[])ctx.getRequest().getParameterMap().get(parameterName);
		if (values == null){
			return null;
		} else if (ctx.getParameterType().equals(float[].class)){
			float[] aa = new float[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = 0;
				} else {
					aa[i] = Float.parseFloat(values[i]);
				}
			}
			return aa;
		} else {
			Float[] aa = new Float[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = null;
				} else {
					aa[i] = Float.parseFloat(values[i]);
				}
			}
			return aa;
			
		}
	}

}
