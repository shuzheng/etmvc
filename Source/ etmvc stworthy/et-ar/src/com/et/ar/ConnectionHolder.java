package com.et.ar;

import com.et.ar.connections.ConnectionProvider;
import com.et.ar.exception.DataAccessException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 管理当前操作的数据库连接，使用连接的优先顺序如下：
 * 1、如果处于事务中，则使用事务的连接；
 * 2、如果连接有缓存，则使用缓存中的连接；
 * 3、以上都没有，则创建从连接池中取得新的连接。
 * @author stworthy
 */
public class ConnectionHolder {
    private Transaction transaction = null;
    private ConnectionProvider connectionProvider = null;
    private Connection con = null;
    
    public ConnectionHolder(Class<?> c) throws DataAccessException {
        transaction = ActiveRecordBase.getCurrentTransaction(c);
        
        try{
            if (transaction == null){
                connectionProvider = ActiveRecordBase.getConnectionProvider(c);
            	con = connectionProvider.getConnection();
            }
            else{
                con = transaction.getConnection();
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e);
        }
    }
    
    public Connection getConnection() {
        return con;
    }
    
    public void close() throws DataAccessException{
        try{
            if (transaction == null){
                connectionProvider.closeConnection(con);
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e);
        }
    }
    
}
