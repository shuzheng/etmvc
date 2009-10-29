package com.et.ar.connections;

import java.util.*;
import java.sql.*;

public class DriverManagerConnectionProvider implements ConnectionProvider{
    private Properties prop;
    private final ArrayList<Connection> pool = new ArrayList<Connection>();
    private int pool_size;
    
    public DriverManagerConnectionProvider(Properties prop) throws ClassNotFoundException{
        this.prop = prop;
        Class.forName(prop.getProperty("driver_class"));
        pool_size = Integer.parseInt(prop.getProperty("pool_size"));
    }
    
    public Connection getConnection() throws SQLException{
        synchronized(pool){
            if(!pool.isEmpty()){
                int last = pool.size()-1;
                Connection pooled = (Connection)pool.remove(last);

                boolean conn_ok = true;
                String test_table = prop.getProperty("test_table");
                if (test_table != null){
                    Statement stmt = null;
                    try{
                        stmt = pooled.createStatement();
                        stmt.executeQuery("select * from " + prop.getProperty("test_table"));
                    }
                    catch(SQLException ex){
                        conn_ok = false;    //连接不可用
                    }
                    finally{
                        if (stmt != null){
                            stmt.close();
                        }
                    }
                }
                if (conn_ok == true){
                    return pooled;
                }
                else{
                    pooled.close();
                }
            }
        }
        Connection conn = DriverManager.getConnection(prop.getProperty("url"),prop.getProperty("username"),prop.getProperty("password"));
        return conn;
    }
    
    public void closeConnection(Connection conn) throws SQLException{
        synchronized(pool){
            if(pool.size() < pool_size){
                pool.add(conn);
                return;
            }
        }
        conn.close();
    }
    
    public void close() throws SQLException{
        Iterator<Connection> it = pool.iterator();
        while(it.hasNext()){
            Connection conn = it.next();
            conn.close();
        }
        pool.clear();
    }
    
    @Override
    protected void finalize(){
        try{
            close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
