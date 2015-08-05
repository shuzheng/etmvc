package com.et.ar.adapters;

public class SqliteAdapter extends Adapter{
    public String getAdapterName(){
        return "sqlite";
    }

    public String getLimitString(String sql, int limit, int offset){
        if (offset == 0){
            return sql + " limit " + Integer.toString(limit);
        }
        else{
            return sql + " limit " + Integer.toString(offset) + "," + Integer.toString(limit);
        }
    }
    
    public boolean supportsLimitOffset(){
        return true;
    }
    
    public String getIdentitySelectString(){
        return "select last_insert_rowid()";
    }
    
    public String getSequenceNextValString(String sequenceName){
        return null;
    }
}
