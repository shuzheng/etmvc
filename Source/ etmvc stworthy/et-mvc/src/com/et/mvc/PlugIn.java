package com.et.mvc;

/**
 * 插件接口
 * @author stworthy
 */
public interface PlugIn {
	/**
	 * 插件初始化时执行的方法
	 * @param context 插件上下文对象
	 */
    public void init(PlugInContext context);
    
    /**
     * 插件销毁时执行的方法
     */
    public void destroy();
}
