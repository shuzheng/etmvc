package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;
import com.et.mvc.binding.DataBinders;

public class BooleanBinder implements DataBinder {
	public Object bind(BindingContext ctx) throws Exception {
		String parameterName = ctx.getParameterName();
		if (!ctx.getPrefix().equals("")) {
			parameterName = ctx.getPrefix() + "." + parameterName;
		}

		String value = ctx.getRequest().getParameter(parameterName);
		if (value == null) {
			if (ctx.getParameterType().equals(boolean.class)) {
				return false;
			} else {
				return null;
			}
		} else if (value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("on")) {
			return true;
		} else if (value.equalsIgnoreCase("false")
				|| value.equalsIgnoreCase("off")) {
			return false;
		} else {
			if (DataBinders.isAllowEmpty() && value.equals("")) {
				return null;
			}
			return Boolean.parseBoolean(value);
		}
	}

}
