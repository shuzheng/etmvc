package com.et.ar.exception;

/**
 * 对象字段访问异常类
 * @author stworthy
 */
public class FieldAccessException extends ActiveRecordException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FieldAccessException(String s){
        super(s);
    }
    
    public FieldAccessException(String s, Throwable root){
        super(s, root);
    }
    
    public FieldAccessException(Throwable root){
        super(root);
    }
}
