package com.et.ar.exception;

import java.sql.SQLException;

/**
 * 数据访问异常类
 * @author stworthy
 *
 */
public class DataAccessException extends ActiveRecordException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataAccessException(String s, SQLException root){
        super(s, root);
    }
    
    public DataAccessException(SQLException root){
        super(root);
    }
//    
//    public DataAccessException(Exception root){
//        super(root);
//    }
}
