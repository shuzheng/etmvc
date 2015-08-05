package com.et.ar.adapters;

public class SqlServerAdapter extends Adapter{
    public String getAdapterName(){
        return "sqlserver";
    }

    public String getLimitString(String sql, int limit, int offset){
        if (sql.toLowerCase().startsWith("select distinct")){
            return "select distinct top " + Integer.toString(limit+offset) + sql.substring("select distinct".length());
        }
        else{
            return "select top " + Integer.toString(limit+offset) + sql.substring("select".length());
        }
    }
    
    public boolean supportsLimitOffset(){
        return false;
    }
    
    public String getIdentitySelectString(){
        return "SELECT @@IDENTITY";
//        return "select scope_identity()";
    }
    
    public String getSequenceNextValString(String sequenceName){
        return null;
    }
}
