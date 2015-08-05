package com.et.mvc.filter;

import com.et.mvc.Controller;

/**
 * 环绕过滤器接口
 * @author stworthy
 */
public interface AroundHandler {
	/**
	 * 控制器执行前
	 * @param controller 控制器
	 * @return 返回true继续执行，返回false中断执行
	 * @throws Exception
	 */
    public boolean before(Controller controller) throws Exception;
    
    /**
     * 控制器执行后
     * @param controller 控制器
     * @return 返回true继续执行，返回false中断执行
     * @throws Exception
     */
    public boolean after(Controller controller) throws Exception;
}
