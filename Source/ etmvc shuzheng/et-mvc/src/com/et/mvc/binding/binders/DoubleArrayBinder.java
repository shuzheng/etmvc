package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class DoubleArrayBinder implements DataBinder{

	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}
		
		String[] values = (String[])ctx.getRequest().getParameterMap().get(parameterName);
		if (values == null){
			return null;
		} else if (ctx.getParameterType().equals(double[].class)){
			double[] aa = new double[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = 0;
				} else {
					aa[i] = Double.parseDouble(values[i]);
				}
			}
			return aa;
		} else {
			Double[] aa = new Double[values.length];
			for(int i=0; i<values.length; i++){
				if (DataBinders.isAllowEmpty() && values[i].equals("")) {
					aa[i] = null;
				} else {
					aa[i] = Double.parseDouble(values[i]);
				}
			}
			return aa;
			
		}
	}

}
