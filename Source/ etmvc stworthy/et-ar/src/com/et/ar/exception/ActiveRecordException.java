package com.et.ar.exception;

/**
 * 活动记录操作异常基类
 * @author stworthy
 *
 */
public class ActiveRecordException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActiveRecordException(String s){
        super(s);
    }
    
    public ActiveRecordException(String s, Throwable root){
        super(s, root);
    }
    
    public ActiveRecordException(Throwable root){
        super(root);
    }
}
