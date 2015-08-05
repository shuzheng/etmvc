package com.et.mvc.binding;

/**
 * 数据绑定接口
 * @author Administrator
 */
public interface DataBinder {
	/**
	 * 绑定操作
	 * @param ctx 绑定上下文
	 * @return 转换后的对象
	 * @throws Exception
	 */
    public Object bind(BindingContext ctx) throws Exception;
}
