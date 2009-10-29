package com.et.ar.connections;

import java.sql.*;

public interface ConnectionProvider {
    public Connection getConnection() throws SQLException;
    
    public void closeConnection(Connection conn) throws SQLException;
    
    public void close() throws SQLException;
}
