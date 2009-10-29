package com.et.ar.exception;

/**
 * 事务操作异常类
 * @author stworthy
 */
public class TransactionException extends ActiveRecordException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionException(String s){
        super(s);
    }
    
    public TransactionException(String s, Throwable root){
        super(s, root);
    }
    
    public TransactionException(Throwable root){
        super(root);
    }
}
