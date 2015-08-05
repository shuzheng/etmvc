package com.et.mvc.binding.binders;

import com.et.mvc.binding.BindingContext;
import com.et.mvc.binding.DataBinder;

public class MapBinder implements DataBinder{

	public Object bind(BindingContext ctx) throws Exception {
		return ctx.getRequest().getParameterMap();
	}

}
