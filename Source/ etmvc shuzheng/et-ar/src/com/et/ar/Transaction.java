package com.et.ar;

import com.et.ar.exception.TransactionException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务描述类
 * @author stworthy
 */
public class Transaction {
    private int level = 0;
    private Connection conn = null;
    
    public Transaction(Connection conn) throws TransactionException {
        this.conn = conn;
        try{
            this.conn.setAutoCommit(false);
        }
        catch(SQLException e){
            throw new TransactionException(e);
        }
    }
    
    public Connection getConnection(){
        return conn;
    }
    
    public void beginTransaction() throws TransactionException{
        level += 1;
    }
    
    public void commit() throws TransactionException{
        try{
            level -= 1;
            if (level == 0){
                conn.commit();
                conn.close();
            }
        }
        catch(SQLException e){
            throw new TransactionException(e);
        }
    }
    
    public void rollback() throws TransactionException{
        try{
            level -= 1;
            if (level == 0){
                conn.rollback();
                conn.close();
            }
        }
        catch(SQLException e){
            throw new TransactionException(e);
        }
    }
    
    public boolean isFinished(){
        return level == 0;
    }
}
