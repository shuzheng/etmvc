package com.et.ar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.et.ar.adapters.Adapter;
import com.et.ar.annotations.GeneratorType;
import com.et.ar.exception.DataAccessException;
import com.et.ar.exception.FieldAccessException;
import com.et.ar.orm.ColumnField;

public class DaoSupport {
	private static Log log = LogFactory.getLog("ActiveRecord");
	
    private Connection conn;
    
    public DaoSupport(Connection conn){
        this.conn = conn;
    }
    
    public <E> int insert(E obj, Adapter adapter) throws FieldAccessException,DataAccessException {
        Class<?> c = obj.getClass();
        OrmInfo orm = OrmInfo.getOrmInfo(c);

        if (adapter != null && orm.idGeneratorType == GeneratorType.AUTO){
            String adapterName = adapter.getAdapterName();
            if (adapterName.equals("mysql")){
                orm.idGeneratorType = GeneratorType.AUTO;
            }
            else if (adapterName.equals("oracle")){
                orm.idGeneratorType = GeneratorType.SEQUENCE;
            }
            else if (adapterName.equals("sqlserver")){
                orm.idGeneratorType = GeneratorType.IDENTITY;
            }
            else if (adapterName.equals("db2")){
            	orm.idGeneratorType = GeneratorType.IDENTITY;
            }
        }
        
        if (orm.idGeneratorType == GeneratorType.AUTO || 
                orm.idGeneratorType == GeneratorType.IDENTITY ||
                orm.idGeneratorType == GeneratorType.SEQUENCE){
            
            String sql1 = "insert into "+orm.table+"(";
            String sql2 = "values(";
            
            if (orm.idGeneratorType == GeneratorType.SEQUENCE){
                if (adapter != null){
                    String sequenceName = orm.table+"_seq"; //序列名称
                    Object nextval = executeScalar(adapter.getSequenceNextValString(sequenceName), null);
                    nextval = ConvertUtil.castFromObject(nextval, orm.idType);
                    sql1 += orm.id + ",";
                    sql2 += nextval.toString() + ",";
                    OrmInfo.setFieldValue(c, orm.id, obj, nextval); //设置序列即主键值
                }
                else{
                    String selectMaxSql = "select max(" + orm.id + ") from " + orm.table;
                    Object maxId = executeScalar(selectMaxSql, null);
                    Long nextId = Long.parseLong(maxId.toString()) + 1;
                    Object nextval = ConvertUtil.castFromObject(nextId.toString(), orm.idType);
                    sql1 += orm.id + ",";
                    sql2 += nextval.toString() + ",";
                    OrmInfo.setFieldValue(c, orm.id, obj, nextval);
                }
            }
            
            Object[] args = new Object[orm.columnFields.length];
            for(int i=0; i<orm.columnFields.length; i++){
                ColumnField field = orm.columnFields[i];
                sql1 += field.getName() + ",";
                sql2 += "?,";
                args[i] = OrmInfo.getFieldValue(c, field.getName(), obj);
            }
            String sql = sql1.substring(0,sql1.length()-1)+") " + sql2.substring(0,sql2.length()-1)+")";
            
            int updated = execute(sql, args);
            //获得自增长值
            if (orm.idGeneratorType != GeneratorType.SEQUENCE){
                if (adapter != null){
                    Object id = executeScalar(adapter.getIdentitySelectString(), null);
                    id = ConvertUtil.castFromObject(id.toString(), orm.idType);
                    OrmInfo.setFieldValue(c, orm.id, obj, id);
                }
                else{
                    String selectMaxSql = "select max(" + orm.id + ") from " + orm.table;
                    Object maxId = executeScalar(selectMaxSql, null);
                    maxId = ConvertUtil.castFromObject(maxId, orm.idType);
                    OrmInfo.setFieldValue(c, orm.id, obj, maxId);
                }
            }
            OrmInfo.setFieldValue(ActiveRecordBase.class, "isnewrecord", obj, false);
            return updated;
        }
        else{
            String sql1 = "insert into " + orm.table + "(";
            String sql2 = "values(";
            List<Object> tmpArgs = new ArrayList<Object>();
            if (orm.id != null){
                sql1 += orm.id + ",";
                sql2 += "?,";
                Object value = OrmInfo.getFieldValue(c, orm.id, obj);
                tmpArgs.add(value);
            }
            for(ColumnField field: orm.columnFields){
                sql1 += field.getName() + ",";
                sql2 += "?,";
                Object value = OrmInfo.getFieldValue(c, field.getName(), obj);
                tmpArgs.add(value);
            }
            String sql = sql1.substring(0,sql1.length()-1)+") " + sql2.substring(0,sql2.length()-1)+")";
            Object[] args = tmpArgs.toArray();
            
            int updated = execute(sql, args);
            OrmInfo.setFieldValue(ActiveRecordBase.class, "isnewrecord", obj, false);
            return updated;
        }
    }
    
    public int updateAll(Class<?> c, String updates, Object[] update_args, String conditions, Object[] condition_args) throws DataAccessException{
        OrmInfo orm = OrmInfo.getOrmInfo(c);
        List<Object> tmpArgs = new ArrayList<Object>();
        String sql = "update " + orm.table + " set " + updates;
        if (update_args != null){
            for(Object arg: update_args){
                tmpArgs.add(arg);
            }
        }
        if (conditions != null && !conditions.equals("")){
            sql += " where " + conditions;
            if (condition_args != null){
                for(Object arg: condition_args){
                    tmpArgs.add(arg);
                }
            }
        }
        Object[] args = tmpArgs.toArray();
        
        return execute(sql, args);
    }
    
    public <E> int update(E obj) throws FieldAccessException,DataAccessException{
        Class<?> clasz = obj.getClass();
        if (((ActiveRecordBase)obj).isProxy()){
            clasz = clasz.getSuperclass();
        }
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        
        Object[] args = new Object[orm.columnFields.length+1];
        String sql = "update " + orm.table + " set ";
        for (int i=0; i<orm.columnFields.length; i++){
            ColumnField field = orm.columnFields[i];
            sql += field.getName() + "=?,";
            args[i] = OrmInfo.getFieldValue(clasz, field.getName(), obj);
        }
        sql = sql.substring(0, sql.length()-1) + " where " + orm.id + "=?";
        args[orm.columnFields.length] = OrmInfo.getFieldValue(clasz, orm.id, obj);
        
        int updated = execute(sql, args);
        OrmInfo.setFieldValue(ActiveRecordBase.class, "isnewrecord", obj, false);
        return updated;
    }
    
    public int deleteAll(Class<?> clasz, String conditions, Object[] args) throws DataAccessException{
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        String sql = "delete from " + orm.table;
        if (conditions != null && !conditions.equals("")){
            sql += " where " + conditions;
        }
        
        return execute(sql, args);
    }
    
    public <E> int delete(E obj) throws FieldAccessException,DataAccessException{
        Class<?> clasz = obj.getClass();
        if (((ActiveRecordBase)obj).isProxy()){
            clasz = clasz.getSuperclass();
        }
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        Object id = OrmInfo.getFieldValue(clasz, orm.id, obj);
        String sql = "delete from " + orm.table + " where " + orm.id + "=?";
        Object[] args = new Object[]{id};
        
        return execute(sql, args);
    }
    
    public List<Map<String,Object>> select(String sql, Object[] args, int limit, int offset) throws DataAccessException{
        List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Object[] sqlParts = buildSql(sql, args);
        sql = sqlParts[0].toString();
        args = (Object[])sqlParts[1];
        String sqlInfo = sqlParts[2].toString();
        try{
        	long t1 = System.currentTimeMillis();
        	
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for(int i=0; i<args.length; i++){
                    pstmt.setObject(i+1, args[i]);
                }
            }
            
            if (limit > 0){
                //设定最大记录数，如果驱动程序不支持则跳过
                try{
                    pstmt.setMaxRows(limit+offset);
                }
                catch(Exception e){}
            }
            rs = pstmt.executeQuery();
            
            int count = 0;
            while(count < offset && rs.next()){
                count ++;
            }
            
            ResultSetMetaData meta = rs.getMetaData();
            count = 0;
            while(rs.next() && (limit == 0 || count++ < limit)){
                Map<String,Object> item = new HashMap<String,Object>();
                for(int i=1; i<=meta.getColumnCount(); i++){
                    String name = meta.getColumnName(i).toLowerCase();
                    Object value = rs.getObject(i);
                    item.put(name, value);
                }
                data.add(item);
            }
            
            long t2 = System.currentTimeMillis();
            if (log.isDebugEnabled()){
            	log.debug((t2-t1)/1000.0 + "s " + sqlInfo);
            }
        }
        catch(SQLException e1){
    		throw new DataAccessException(sqlInfo, e1);
        }
        finally{
            try{
                if (rs != null){
                    rs.close();
                }
                if (pstmt != null){
                    pstmt.close();
                }
            }
            catch(SQLException e){
                throw new DataAccessException(e);
            }
        }
        
        return data;
    }
    
    public <E> List<E> select(Class<E> clasz, String sql, Object[] args, int limit, int offset) throws FieldAccessException,DataAccessException{
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        boolean useProxy = true;
        if (orm.hasManyFields.length==0 && orm.belongsToFields.length==0 && orm.hasOneFields.length==0){
            useProxy = false;   //无需使用延迟加载
        }
        
        List<E> data = new ArrayList<E>();
        List<Map<String,Object>> items = select(sql, args, limit, offset);
        for(Map<String,Object> item: items){
            E obj;
            if (useProxy == false){
                try{
                    obj = clasz.newInstance();
                }
                catch(Exception ex){
                    throw new FieldAccessException(ex);
                }
            }
            else{
                ActiveRecordProxy proxy = new ActiveRecordProxy();
                obj = proxy.getProxyObject(clasz);
                if (obj instanceof ActiveRecordBase) {
                	OrmInfo.setFieldValue(ActiveRecordBase.class, "isproxy", obj, true);
                }
            }
            if (obj instanceof ActiveRecordBase) {
            	OrmInfo.setFieldValue(ActiveRecordBase.class, "isnewrecord", obj, false);
            }

            if (orm.id != null){
                Object value = item.get(orm.id.toLowerCase());
                value = ConvertUtil.castFromObject(value, orm.idType);
                OrmInfo.setFieldValue(clasz, orm.id, obj, value);
            }
            for(ColumnField field: orm.columnFields){
                Object value = item.get(field.getName().toLowerCase());
                value = ConvertUtil.castFromObject(value, field.getType());
                OrmInfo.setFieldValue(clasz, field.getName(), obj, value);
            }
            data.add(obj);
        }
        return data;
    }
    
    public int execute(String sql, Object[] args) throws DataAccessException{
        int updated = 0;
        Object[] sqlParts = buildSql(sql, args);
        sql = sqlParts[0].toString();
        args = (Object[])sqlParts[1];
        String sqlInfo = sqlParts[2].toString();
        PreparedStatement pstmt = null;
        try{
        	long t1 = System.currentTimeMillis();
        	
            pstmt = this.conn.prepareStatement(sql);
            if (args != null){
                for(int i=0; i<args.length; i++){
                    pstmt.setObject(i+1, args[i]);
                }
            }
            updated = pstmt.executeUpdate();
            
            long t2 = System.currentTimeMillis();
            if (log.isDebugEnabled()){
            	log.debug((t2-t1)/1000.0 + "s " + sqlInfo);
            }
        }
        catch(SQLException e){
            throw new DataAccessException(sqlInfo, e);
        }
        finally{
            try{
                if (pstmt != null){
                    pstmt.close();
                }
            }
            catch(SQLException e){
                throw new DataAccessException(e);
            }
        }
        return updated;
    }
    
    public Object executeScalar(String sql, Object[] args) throws DataAccessException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Object scalar = null;
        
        Object[] sqlParts = buildSql(sql, args);
        sql = sqlParts[0].toString();
        args = (Object[])sqlParts[1];
        String sqlInfo = sqlParts[2].toString();
        try{
        	long t1 = System.currentTimeMillis();
        	
            pstmt = this.conn.prepareStatement(sql);
            if (args != null){
                for(int i=0; i<args.length; i++){
                    pstmt.setObject(i+1, args[i]);
                }
            }
            rs = pstmt.executeQuery();
            if (rs.next()){
                scalar = rs.getObject(1);
            }
            
            long t2 = System.currentTimeMillis();
            if (log.isDebugEnabled()){
            	log.debug((t2-t1)/1000.0 + "s " + sqlInfo);
            }
        }
        catch(SQLException e){
            throw new DataAccessException(sqlInfo, e);
        }
        finally{
            try{
                if (rs != null){
                    rs.close();
                }
                if (pstmt != null){
                    pstmt.close();
                }
            }
            catch(SQLException e){
                throw new DataAccessException(e);
            }
        }
        return scalar;
    }
    
    /**
     * 构建SQL语句，处理掉NULL值参数
     * @param sql 原始SQL语句
     * @param args 原始参数
     * @return 三个元素数组，分别是SQL是语句、参数、显示SQL语句
     */
    private Object[] buildSql(String sql, Object[] args){
    	Object[] result = new Object[3];
    	if (args == null){
    		result[0] = sql;
    		result[1] = args;
    		result[2] = sql;
    		return result;
    	}
    	
    	String newSql = "";
    	String showSql = "";
    	List<Object> tmpArgs = new ArrayList<Object>();
    	
    	String[] ss = (sql + " ").split("\\?");
    	for(int i=0; i<ss.length-1; i++){
    		Object arg = args[i];
    		if (arg == null) {
    			newSql += ss[i] + "null";
    			showSql += ss[i] + "null";
    		} else {
    			newSql += ss[i] + "?";
    			tmpArgs.add(arg);
    			if (arg instanceof String) {
    				showSql += ss[i] + "'" + arg + "'";
    			} else {
    				showSql += ss[i] + arg.toString();
    			}
    		}
    	}
    	newSql += ss[ss.length - 1];
    	showSql += ss[ss.length - 1];
    	
    	result[0] = newSql;
    	result[1] = tmpArgs.toArray();
    	result[2] = showSql;
    	
    	return result;
    }
    /*
    private String buildSql1(String sql, Object[] args){
    	if (args == null){
    		return sql;
    	}
    	String[] ss = (sql + " ").split("\\?");
    	sql = "";
    	for(int i=0; i<ss.length-1; i++){
    		Object arg = args[i];
    		String val = "";
    		if (arg == null){
    			val = "null";
    		} else if (arg instanceof String){
    			val = "'" + arg + "'";
    		} else {
    			val = arg.toString();
    		}
    		
    		sql += ss[i] + val;
    	}
    	sql += ss[ss.length-1];
    	
    	return sql;
    }
    */
}
