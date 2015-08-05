package com.et.mvc;

/**
 * 视图渲染接口
 * @author stworthy
 */
public interface ViewRenderer<T> {
	/**
	 * 渲染动作
	 * @param viewObject 视图对象
	 * @param viewContext 视图上下文
	 * @throws Exception
	 */
    public void render(T viewObject, ViewContext viewContext) throws Exception;
}
