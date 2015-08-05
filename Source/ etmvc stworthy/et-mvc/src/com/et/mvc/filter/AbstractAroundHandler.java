package com.et.mvc.filter;

import com.et.mvc.Controller;

/**
 * 环绕过滤器处理抽象类
 * @author stworthy
 */
public class AbstractAroundHandler implements AroundHandler{
    public boolean before(Controller controller) throws Exception{
        return true;
    }
    
    public boolean after(Controller controller) throws Exception{
        return true;
    }
}
