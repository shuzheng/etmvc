package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class DoubleBinder implements DataBinder {
	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}

		String value = ctx.getRequest().getParameter(parameterName);
		if (value == null) {
			if (ctx.getParameterType().equals(double.class)) {
				return 0;
			} else {
				return null;
			}
		} else {
        	if (DataBinders.isAllowEmpty() && value.equals("")) {
        		return null;
        	}
			return Double.parseDouble(value);
		}
	}

}
