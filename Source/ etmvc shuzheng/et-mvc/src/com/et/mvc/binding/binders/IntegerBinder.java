package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class IntegerBinder implements DataBinder {
	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}
		String value = ctx.getRequest().getParameter(parameterName);
		if (value == null) {
			if (ctx.getParameterType().equals(int.class)) {
				return 0;
			} else {
				return null;
			}
		} else if (value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("on")) {
			return 1;
		} else if (value.equalsIgnoreCase("false")
				|| value.equalsIgnoreCase("off")) {
			return 0;
		} else {
        	if (DataBinders.isAllowEmpty() && value.equals("")) {
        		return null;
        	}
			return Integer.parseInt(value);
		}
	}
}
