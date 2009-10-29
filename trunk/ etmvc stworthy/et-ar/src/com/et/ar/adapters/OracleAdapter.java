package com.et.ar.adapters;

public class OracleAdapter extends Adapter{
    public String getAdapterName(){
        return "oracle";
    }

    public String getLimitString(String sql, int limit, int offset){
        StringBuffer pagingSelect = new StringBuffer(sql.length()+100);
        if (offset == 0){
            pagingSelect.append("select * from ( ");
            pagingSelect.append(sql);
            pagingSelect.append(" ) where rownum <= " + Integer.toString(limit));
        }
        else{
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
            pagingSelect.append(sql);
            pagingSelect.append(" ) row_ ) where rownum_ <= "+Integer.toString(limit+offset)+" and rownum_ > "+Integer.toString(offset));
        }
        return pagingSelect.toString();
    }
    
    public boolean supportsLimitOffset(){
        return true;
    }

    public String getIdentitySelectString(){
        return null;
    }
    
    public String getSequenceNextValString(String sequenceName){
        return "select " + sequenceName + ".nextval from dual";
    }
}
