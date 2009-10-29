package com.et.ar;

import com.et.ar.exception.ValidateException;
import com.et.ar.exception.FieldAccessException;
import com.et.ar.adapters.Adapter;
import com.et.ar.annotations.DependentType;
import com.et.ar.annotations.HasMany;
import com.et.ar.annotations.HasOne;
import com.et.ar.connections.ConnectionProvider;
import com.et.ar.annotations.ValidatorClass;
import com.et.ar.annotations.Unique;
import com.et.ar.exception.ActiveRecordException;
import com.et.ar.exception.TransactionException;
import com.et.ar.orm.HasManyField;
import com.et.ar.orm.HasOneField;
import com.et.ar.validators.UniqueCreateValidator;
import com.et.ar.validators.UniqueUpdateValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 活动记录基类
 * @author stworthy
 */
public class ActiveRecordBase {
    //当前线程事务（多数据库）
//    private static Map<Thread,Map<String,Transaction>> currentTransactions = new HashMap<Thread,Map<String,Transaction>>();
    private static ThreadLocal<Map<String,Transaction>> currentTransactions = new ThreadLocal<Map<String,Transaction>>();
    //连接提供者
    private static Map<String,ConnectionProvider> connections = new HashMap<String,ConnectionProvider>();
    //数据库适配器（方言）
    private static Map<String,Adapter> adapters = new HashMap<String,Adapter>();
    //当前线程的数据验证资源（国际化）
//    private static Map<Thread,ResourceBundle> validatorResources = new HashMap<Thread,ResourceBundle>();
    private static ThreadLocal<ResourceBundle> validatorResources = new ThreadLocal<ResourceBundle>();
    //对象的错误信息集合
    private List<String> errors = new ArrayList<String>();
    
    private boolean isproxy = false;
    private boolean isnewrecord = true;
    
    static {
        try{
            ConfReader reader = new ConfReader();
            reader.init();
            connections = reader.getConnections();
            adapters = reader.getAdapters();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 是否是一个代理对象
     * @return 是代理对象时返回true，否则返回false。
     */
    public boolean isProxy(){
        return this.isproxy;
    }
    
    /**
     * 是否被持久化过
     * @return 已经持久化过返回true，否则返回false。
     */
    public boolean isNewRecord(){
        return this.isnewrecord;
    }
    
    /**
     * 设置数据验证资源
     * @param resource
     */
    public static void setValidatorResource(ResourceBundle resource){
        validatorResources.set(resource);
    }
    
    /**
     * 获取数据验证资源
     * @return 验证资源
     */
    public static ResourceBundle getCurrentValidatorResource(){
        return validatorResources.get();
    }
    
    /**
     * 开始一个事务，以ActiveRecordBase.class所指数据库连接操作事务
     * @throws TransactionException
     */
    public static void beginTransaction() throws TransactionException{
        beginTransaction(ActiveRecordBase.class);
    }
    
    /**
     * 开始一个事务
     * @param c 绑定数据库连接的类class
     * @throws TransactionException
     */
    public static void beginTransaction(Class<?> c) throws TransactionException{
        String className = getBaseClassName(c);
        
        Map<String,Transaction> map = currentTransactions.get();
        try{
            if (map == null){
                Connection con = getConnectionProvider(c).getConnection();
                Transaction transaction = new Transaction(con);
                transaction.beginTransaction();

                map = new HashMap<String,Transaction>();
                map.put(className, transaction);    //将当前事务保存起来

                currentTransactions.set(map);
            }
            else{
                Transaction transaction = map.get(getBaseClassName(c));
                if (transaction == null){
                    Connection con = getConnectionProvider(c).getConnection();
                    transaction = new Transaction(con);
                    transaction.beginTransaction();

                    map.put(className, transaction);    //将当前事务保存起来
                }
                else{
                    transaction.beginTransaction();     //增加事务级别
                }
            }
        }
        catch(SQLException e){
            throw new TransactionException(e);
        }
    }
    
    /**
     * 提交事务
     * @throws TransactionException
     */
    public static void commit() throws TransactionException{
        commit(ActiveRecordBase.class);
    }
    
    /**
     * 提交事务
     * @param c 指定的数据库连接所绑定的类class
     * @throws TransactionException
     */
    public static void commit(Class<?> c) throws TransactionException{
        String className = getBaseClassName(c);
        Map<String,Transaction> map = currentTransactions.get();
        Transaction transaction = map.get(className);
        transaction.commit();
        if (transaction.isFinished()){  //事务最终完成
            map.remove(className);
        }
        
        if (map.size() == 0){
            currentTransactions.remove();
        }
    }
    
    /**
     * 回滚事务
     * @throws TransactionException
     */
    public static void rollback() throws TransactionException{
        rollback(ActiveRecordBase.class);
    }
    
    /**
     * 回滚事务
     * @param c 指定的数据库连接所绑定的类class
     * @throws TransactionException
     */
    public static void rollback(Class<?> c) throws TransactionException{
        String className = getBaseClassName(c);
        Map<String,Transaction> map = currentTransactions.get();
        Transaction transaction = map.get(className);
        transaction.rollback();
        if (transaction.isFinished()){
            map.remove(className);
        }
        
        if (map.size() == 0){
            currentTransactions.remove();
        }
    }
    
    /**
     * 获取当前线程事务
     * @param c 指定的数据库连接所绑定的类class
     * @return 当前事务
     */
    public static Transaction getCurrentTransaction(Class<?> c){
        Map<String,Transaction> map = currentTransactions.get();
        if (map == null){
            return null;
        }
        else{
            return map.get(getBaseClassName(c));
        }
    }
    
    /**
     * 取得数据库连接提供者
     * @param c 指定的数据库连接所绑定的类class
     * @return 连接提供者
     */
    public static ConnectionProvider getConnectionProvider(Class<?> c) {
        return connections.get(getBaseClassName(c));
    }
    
    /**
     * 设置数据库连接提供者
     * @param domainClassName 域基类，由它登记数据库连接信息
     * @param cp 连接提供者
     */
    public static void putConnectionProvider(String domainClassName, ConnectionProvider cp){
        connections.put(domainClassName, cp);
    }
    
    /**
     * 设置连接适配器
     * @param domainClassName 域基类，由它登记数据库连接信息
     * @param adapter 适配器
     */
    public static void putConnectionAdapter(String domainClassName, Adapter adapter){
        adapters.put(domainClassName, adapter);
    }
    
    private static String getBaseClassName(Class<?> c){
        String className = c.getCanonicalName();
        ConnectionProvider cp = connections.get(className);
        while (cp == null){
            c = c.getSuperclass();
            if (c == null) {
            	return null;
            }
            className = c.getCanonicalName();
            cp = connections.get(className);
        }
        return className;
    }
    
    private void uniqueCreateValidate() throws ValidateException{
        Class<?> clasz = this.getClass();
        if (this.isProxy()){
            clasz = clasz.getSuperclass();
        }
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        for(Field f: clasz.getDeclaredFields()){
            f.setAccessible(true);
            Unique v_unique = f.getAnnotation(Unique.class);
            if (v_unique != null){
                UniqueCreateValidator ucv = new UniqueCreateValidator(clasz,orm.table,f.getName(),v_unique);
                try{
                    Object value = f.get(this);
                    if (ucv.validate(value) == false){
                        errors.add(ucv.getMessage());
                    }
                }
                catch(IllegalAccessException e){
                    throw new ValidateException(e);
                }
            }
        }
    }
    
    private void uniqueUpdateValidate() throws ValidateException{
        Class<?> clasz = this.getClass();
        if (this.isProxy()){
            clasz = clasz.getSuperclass();
        }
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        for (Field f: clasz.getDeclaredFields()){
            f.setAccessible(true);
            Unique v_unique = f.getAnnotation(Unique.class);
            if (v_unique != null){
                try{
                    Object idValue = OrmInfo.getFieldValue(clasz,orm.id,this);
                    UniqueUpdateValidator uuv = new UniqueUpdateValidator(clasz,orm.table,f.getName(),orm.id,idValue,v_unique);
                    if (uuv.validate(f.get(this)) == false){
                        errors.add(uuv.getMessage());
                    }
                }
                catch(Exception e){
                    throw new ValidateException(e);
                }
            }
        }
    }
    
    /**
     * 进行数据有效性验证，验证的错误信息可以调用getErrors()获取。
     * @throws com.et.ar.exception.ValidateException
     */
    public void validate() throws ValidateException{
        Class<?> clasz = this.getClass();
        if (this.isProxy()){
            clasz = clasz.getSuperclass();
        }
        try{
            for(Field f: clasz.getDeclaredFields()){
                f.setAccessible(true);
                for(Annotation aa: f.getDeclaredAnnotations()){
                    ValidatorClass validatorClass = aa.annotationType().getAnnotation(ValidatorClass.class);
                    if (validatorClass != null){
                        Class<?> vc = validatorClass.value();
                        Object instance = vc.newInstance(); //创建验证类实例
                        vc.getMethod("init", Object.class).invoke(instance, aa);
                        boolean result = (Boolean)vc.getMethod("validate", Object.class).invoke(instance, f.get(this));
                        if (result == false){
                            String err = (String)vc.getMethod("getMessage").invoke(instance);
                            errors.add(err);
                        }
                    }
                }
            }
        }
        catch(Exception e){
            throw new ValidateException(e);
        }
    }
    
    /**
     * 新建对象时进行验证
     * @throws com.et.ar.exception.ValidateException
     */
    public void validateOnCreate() throws ValidateException{
        this.errors.clear();
        uniqueCreateValidate();
        validate();
    }
    
    /**
     * 更新对象时进行验证
     * @throws com.et.ar.exception.ValidateException
     */
    public void validateOnUpdate() throws ValidateException{
        this.errors.clear();
        uniqueUpdateValidate();
        validate();
    }
    
    /**
     * 查找对象，找到时返回对象，找不到返回null。
     * @param <E>
     * @param clasz 对象类型
     * @param id 对象ID
     * @return 对应指定的对象
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> E find(Class<E> clasz, Object id) throws ActiveRecordException{
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        List<E> results = findAll(clasz, orm.id+"=?", new Object[]{id});
        if (results.size() == 0){
            return null;
        }
        else{
            return results.get(0);
        }
    }
    
    /**
     * 查找第一个对象，找到时返回对象，找不到返回null。
     * @param <E>
     * @param clasz 对象类型
     * @return 第一个对象
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> E findFirst(Class<E> clasz) throws ActiveRecordException {
        return findFirst(clasz, null, null, null, 0, 0);
    }
    
    /**
     * 查找第一个对象，找到时返回对象，找不到返回null。
     * @param <E>
     * @param clasz 
     * @param conditions 查询条件
     * @param args 查询参数
     * @return 第一个对象
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> E findFirst(Class<E> clasz, String conditions, Object[] args) throws ActiveRecordException {
        return findFirst(clasz, conditions, args, null, 0, 0);
    }
    
    /**
     * 查找第一个对象，找到时返回对象，找不到返回null。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @param args 查询参数
     * @param order 排序
     * @return 第一个对象
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> E findFirst(Class<E> clasz, String conditions, Object[] args, String order) throws ActiveRecordException {
        return findFirst(clasz, conditions, args, order, 0, 0);
    }
    
    /**
     * 查找第一个对象，找到时返回对象，找不到返回null。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @param args 查询参数
     * @param order 排序
     * @param limit 返回最大数量
     * @return 第一个对象
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> E findFirst(Class<E> clasz, String conditions, Object[] args, String order, int limit) throws ActiveRecordException {
        return findFirst(clasz, conditions, args, order, limit, 0);
    }
    
    /**
     * 查找第一个对象，找到时返回对象，找不到返回null。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @param args 查询参数
     * @param order 排序
     * @param limit 返回最大数量
     * @param offset 查找的偏移位置
     * @return 第一个对象
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> E findFirst(Class<E> clasz, String conditions, Object[] args, String order, int limit, int offset) throws ActiveRecordException {
        List<E> results = findAll(clasz, conditions, args, order, limit, offset);
        if (results.size() == 0){
            return null;
        }
        else{
            return results.get(0);
        }
    }
    
    /**
     * 查找对象集合。
     * @param <E>
     * @param clasz
     * @return 对象集合
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> List<E> findAll(Class<E> clasz) throws ActiveRecordException {
        return findAll(clasz, null, null, null, 0, 0);
    }
    
    /**
     * 查找对象集合。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @return 对象集合
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> List<E> findAll(Class<E> clasz, String conditions) throws ActiveRecordException {
        return findAll(clasz, conditions, null, null, 0, 0);
    }
    
    /**
     * 查找对象集合。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @param args 查询参数
     * @return 对象集合
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> List<E> findAll(Class<E> clasz, String conditions, Object[] args) throws ActiveRecordException {
    	return findAll(clasz, conditions, args, null, 0, 0);
    }
    
    /**
     * 查找对象集合。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @param args 查询参数
     * @param order 排序
     * @return 对象集合
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> List<E> findAll(Class<E> clasz, String conditions, Object[] args, String order) throws ActiveRecordException {
        return findAll(clasz, conditions, args, order, 0, 0);
    }
    
    /**
     * 查找对象集合。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @param args 查询参数
     * @param order 排序
     * @param limit 返回最大数量
     * @return 对象集合
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> List<E> findAll(Class<E> clasz, String conditions, Object[] args, String order, int limit) throws ActiveRecordException {
        return findAll(clasz, conditions, args, order, limit, 0);
    }
    
    /**
     * 查找对象集合。
     * @param <E>
     * @param clasz
     * @param conditions 查询条件
     * @param args 查询参数
     * @param order 排序
     * @param limit 返回最大数量
     * @param offset 查找的偏移位置
     * @return 对象集合
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static <E> List<E> findAll(Class<E> clasz, String conditions, Object[] args, String order, int limit, int offset) throws ActiveRecordException {
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);
        String sql = "select * from " + orm.table;
        if (conditions != null && !conditions.equals("")){
            sql += " where " + conditions;
        }
        return findBySql(clasz, sql, args, order, limit, offset);
    }
    
    /**
     * 查找对象集合
     * @param <E>
     * @param clasz
     * @param sql SQL语句
     * @return 对象集合
     * @throws ActiveRecordException
     */
    public static <E> List<E> findBySql(Class<E> clasz, String sql) throws ActiveRecordException {
    	return findBySql(clasz, sql, null, null, 0, 0);
    }
    
    /**
     * 查找对象集合
     * @param <E>
     * @param clasz
     * @param sql SQL语句
     * @param args 查询参数
     * @return 对象集合
     * @throws ActiveRecordException
     */
    public static <E> List<E> findBySql(Class<E> clasz, String sql, Object[] args) throws ActiveRecordException {
    	return findBySql(clasz, sql, args, null, 0, 0);
    }
    
    /**
     * 查找对象集合
     * @param <E>
     * @param clasz
     * @param sql SQL语句
     * @param args 查询参数
     * @param order 排序
     * @return 对象集合
     * @throws ActiveRecordException
     */
    public static <E> List<E> findBySql(Class<E> clasz, String sql, Object[] args, String order) throws ActiveRecordException {
    	return findBySql(clasz, sql, args, order, 0, 0);
    }
    
    /**
     * 查找对象集合
     * @param <E>
     * @param clasz
     * @param sql SQL语句
     * @param args 查询参数
     * @param order 排序
     * @param limit 返回最大数量
     * @return 对象集合
     * @throws ActiveRecordException
     */
    public static <E> List<E> findBySql(Class<E> clasz, String sql, Object[] args, String order, int limit) throws ActiveRecordException {
    	return findBySql(clasz, sql, args, order, 0);
    }
    
    /**
     * 通过SQL查找对象集合，按照clasz指定的连接操作，如果找不到连接就使用ActiveRecordBase所指定的连接
     * @param <E>
     * @param clasz
     * @param sql SQL语句
     * @param args 查询参数
     * @param order 排序
     * @param limit 返回最大数量
     * @param offset 查找偏移位置
     * @return 对象集合
     * @throws ActiveRecordException
     */
    public static <E> List<E> findBySql(Class<E> clasz, String sql, Object[] args, String order, int limit, int offset) throws ActiveRecordException {
        if (order != null && !order.equals("")){
            sql += " order by " + order;
        }
        
        ConnectionHolder connectionHolder = null;
        Adapter adapter = null;
        String baseClassName = getBaseClassName(clasz);
        if (baseClassName != null) {
        	connectionHolder = new ConnectionHolder(clasz);
        	adapter = adapters.get(baseClassName);
        } else {
        	connectionHolder = new ConnectionHolder(ActiveRecordBase.class);
        	adapter = adapters.get(ActiveRecordBase.class.getCanonicalName());
        }
        
        try{
            Connection conn = connectionHolder.getConnection();
            DaoSupport dao = new DaoSupport(conn);
            if (adapter != null){
                if (limit > 0){
                    sql = adapter.getLimitString(sql, limit, offset);
                }
                if (adapter.supportsLimitOffset() == false){
                    return dao.select(clasz, sql, args, 0, offset);
                }
                else{
                    return dao.select(clasz, sql, args, 0, 0);
                }
            }
            else{
                return dao.select(clasz, sql, args, limit, offset);
            }
        }
        finally{
            connectionHolder.close();
        }
    }
    
    /**
     * 将查询结果ResultSet映射成Map集合，多用于统计。
     * @param connClass 连接的基类
     * @param sql 查询统计语句
     * @param args 查询参数
     * @param order 排序
     * @param limit 返回最大数量
     * @param offset 返回偏移位置
     * @return Map集合
     * @throws com.et.ar.exception.ActiveRecordException
     */
    public static List<Map<String,Object>> getResultMap(Class<?> connClass, String sql, Object[] args, String order, int limit, int offset) throws ActiveRecordException {
        if (order != null && !order.equals("")){
            sql += " order by " + order;
        }
        
        Adapter adapter = adapters.get(getBaseClassName(connClass));
        
        ConnectionHolder connectionHolder = new ConnectionHolder(connClass);
        try{
            Connection conn = connectionHolder.getConnection();
            DaoSupport dao = new DaoSupport(conn);
            if (adapter != null){
                if (limit > 0){
                    sql = adapter.getLimitString(sql, limit, offset);
                }
                if (adapter.supportsLimitOffset() == false){
                    return dao.select(sql, args, 0, offset);
                }
                else{
                    return dao.select(sql, args, 0, 0);
                }
            }
            else{
                return dao.select(sql, args, limit, offset);
            }
        }
        finally{
            connectionHolder.close();
        }
    }
    
    /**
     * 更新操作
     * @param clasz
     * @param updates 更新语句
     * @param update_args 更新参数
     * @param conditions 更新条件
     * @param condition_args 条件参数
     * @return 影响行数
     * @throws ActiveRecordException
     */
    public static int updateAll(Class<?> clasz, String updates, Object[] update_args, String conditions, Object[] condition_args) throws ActiveRecordException{
        ConnectionHolder connectionHolder = new ConnectionHolder(clasz);
        try{
            DaoSupport dao = new DaoSupport(connectionHolder.getConnection());
            return dao.updateAll(clasz, updates, update_args, conditions, condition_args);
        }
        finally{
            connectionHolder.close();
        }
    }
            
    /**
     * 删除操作，执行回调验证
     * @param clasz
     * @param conditions 条件表达式
     * @param args 条件参数
     * @return 影响行数
     * @throws ActiveRecordException
     */
    public static int destroyAll(Class<?> clasz, String conditions, Object[] args) throws ActiveRecordException{
        int updated = 0;
        for(Object obj: ActiveRecordBase.findAll(clasz, conditions, args)){
            ActiveRecordBase ar = (ActiveRecordBase)obj;
            updated += ar.destroy();
        }
        return updated;
    }
    
    /**
     * 删除操作，不执行回调验证
     * @param clasz
     * @param conditions 条件表达式
     * @param args 条件参数
     * @return 影响行数
     * @throws ActiveRecordException
     */
    public static int deleteAll(Class<?> clasz, String conditions, Object[] args) throws ActiveRecordException{
        ConnectionHolder connectionHolder = new ConnectionHolder(clasz);
        try{
            DaoSupport dao = new DaoSupport(connectionHolder.getConnection());
            return dao.deleteAll(clasz, conditions, args);
        }
        finally{
            connectionHolder.close();
        }
    }
    
    /**
     * 使用默认连接执行通用操作
     * @param sql SQL语句
     * @param args 参数
     * @return 影响行数
     * @throws ActiveRecordException
     */
    public static int execute(String sql, Object[] args) throws ActiveRecordException{
        return execute(ActiveRecordBase.class, sql, args);
    }
    
    /**
     * 使用指定连接执行通用操作
     * @param c
     * @param sql SQL语句
     * @param args 参数
     * @return 影响行数
     * @throws ActiveRecordException
     */
    public static int execute(Class<?> c, String sql, Object[] args) throws ActiveRecordException{
        ConnectionHolder connectionHolder = new ConnectionHolder(c);
        try{
            DaoSupport dao = new DaoSupport(connectionHolder.getConnection());
            return dao.execute(sql, args);
        }
        finally{
            connectionHolder.close();
        }
    }
    
    /**
     * 统计数量
     * @param c 被统计类
     * @param conditions 条件表达式
     * @param args 条件参数
     * @return 统计结果数量
     * @throws ActiveRecordException
     */
    public static long count(Class<?> c, String conditions, Object[] args) throws ActiveRecordException{
        OrmInfo orm = OrmInfo.getOrmInfo(c);
        String sql = "select count(*) from "+orm.table;
        if (conditions != null){
            sql += " where "+conditions;
        }
        Object result = executeScalar(c, sql, args);
        
        return Long.parseLong(result.toString());
    }
    
    /**
     * 获取最大值
     * @param c 被统计对象
     * @param field 查找字段
     * @param conditions 条件
     * @param args 参数
     * @return 所查找字段的最大值
     * @throws ActiveRecordException
     */
    public static Object maximum(Class<?> c, String field, String conditions, Object[] args) throws ActiveRecordException{
        OrmInfo orm = OrmInfo.getOrmInfo(c);
        String sql = "select max(" + field + ") from " + orm.table;
        if (conditions != null && !conditions.equals("")){
            sql += " where " + conditions;
        }
        
        return executeScalar(c, sql, args);
    }
    
    /**
     * 获取最小值
     * @param c 被统计对象
     * @param field 查找字段
     * @param conditions 条件表达式
     * @param args 参数
     * @return 所查找字段的最小值
     * @throws ActiveRecordException
     */
    public static Object minimum(Class<?> c, String field, String conditions, Object[] args) throws ActiveRecordException{
        OrmInfo orm = OrmInfo.getOrmInfo(c);
        String sql = "select min(" + field + ") from " + orm.table;
        if (conditions != null && !conditions.equals("")){
            sql += " where " + conditions;
        }
        
        return executeScalar(c, sql, args);
    }
    
    public static Object executeScalar(String sql, Object[] args) throws ActiveRecordException{
        return executeScalar(ActiveRecordBase.class, sql, args);
    }
    
    /**
     * 执行查询，返回结果第一行第一列
     * @param c 连接所绑定的类class
     * @param sql SQL语句
     * @param args 参数
     * @return 结果集第一行第一列
     * @throws ActiveRecordException
     */
    public static Object executeScalar(Class<?> c, String sql, Object[] args) throws ActiveRecordException{
        ConnectionHolder connectionHolder = new ConnectionHolder(c);
        try{
            DaoSupport dao = new DaoSupport(connectionHolder.getConnection());
            return dao.executeScalar(sql, args);
        }
        finally{
            connectionHolder.close();
        }
    }
    
    /**
     * 建立模型对象
     * @param <E>
     * @param c 模型类
     * @param prefix 参数前缀
     * @param map 参数
     * @return 模型对象
     * @throws Exception
     */
    public static <E> E createModel(Class<E> c, String prefix, Map<?,?> map) throws Exception{
        E obj = c.newInstance();
        return updateModel(obj,prefix,map);
    }
    
    /**
     * 更新模型对象
     * @param <E>
     * @param obj 模型对象
     * @param prefix 参数前缀
     * @param map 参数
     * @return 更新后的模型对象
     * @throws Exception
     */
    public static <E> E updateModel(E obj, String prefix, Map<?,?> map) throws Exception{
        Class<?> clasz = obj.getClass();
        if (((ActiveRecordBase)obj).isProxy()){
            clasz = clasz.getSuperclass();
        }
        for(Field f: clasz.getDeclaredFields()){
        	String key = prefix + "[" + f.getName() + "]";
        	String[] value = null;
        	if (map.containsKey(key)) {
        		value = (String[])map.get(key);
        	} else {
        		key = prefix + "." + f.getName();
        		value = (String[])map.get(key);
        	}
//            String[] value = (String[])map.get(prefix+"["+f.getName()+"]");
            if (value == null){
                continue;
            }
            f.setAccessible(true);
            f.set(obj, ConvertUtil.castFromObject(value[0], f.getType()));
        }
        return obj;
    }
    
    /**
     * 建立模型对象
     * @param <E>
     * @param c 模型类
     * @param map 参数
     * @return 模型对象
     * @throws Exception
     */
    public static <E> E createModel(Class<E> c, Map<?,?> map) throws Exception{
        E obj = c.newInstance();
        return updateModel(obj, map);
    }
    
    /**
     * 更新模型对象
     * @param <E>
     * @param obj 模型对象
     * @param map 参数
     * @return 更新后的模型对象
     * @throws Exception
     */
    public static <E> E updateModel(E obj, Map<?,?> map) throws Exception{
        Class<?> clasz = obj.getClass();
        if (((ActiveRecordBase)obj).isProxy()){
            clasz = clasz.getSuperclass();
        }
        for(Field f: clasz.getDeclaredFields()){
            String[] value = (String[])map.get(f.getName());
            if (value == null){
                continue;
            }
            f.setAccessible(true);
            f.set(obj, ConvertUtil.castFromObject(value[0], f.getType()));
        }
        return obj;
    }
    
    /**
     * 获取AR对象的错误信息
     * @return 错误信息列表
     */
    public List<String> getErrors(){
        return errors;
    }
    
    /**
     * 创建对象
     * @return 成功返回true，失败返回false
     * @throws ActiveRecordException
     */
    public boolean create() throws ActiveRecordException {
        validateOnCreate();
        if (errors.size() > 0){
            return false;
        }
        else{
            Class<?> clasz = this.getClass();
            if (this.isProxy()){
                clasz = clasz.getSuperclass();
            }
            
            try{
                beginTransaction(clasz);
                beforeCreate();
                
                Adapter adapter = adapters.get(getBaseClassName(clasz));
                ConnectionHolder connectionHolder = new ConnectionHolder(clasz);
                try{
                    DaoSupport dao = new DaoSupport(connectionHolder.getConnection());
                    dao.insert(this, adapter);
                }
                finally{
                    connectionHolder.close();
                }
                
                afterCreate();
                commit(clasz);
                return true;
            }
            catch(Exception ex){
                rollback(clasz);
                throw new ActiveRecordException(ex);
            }
        }
    }
    
    /**
     * 更新对象
     * @return 成功返回true，失败返回false
     * @throws ActiveRecordException
     */
    public boolean update() throws ActiveRecordException{
        validateOnUpdate();
        if (errors.size() > 0){
            return false;
        }
        else{
            Class<?> clasz = this.getClass();
            if (this.isProxy()){
                clasz = clasz.getSuperclass();
            }
            
            try{
                beginTransaction(clasz);
                beforeUpdate();
                
                ConnectionHolder connectionHolder = new ConnectionHolder(clasz);
                try{
                    DaoSupport dao = new DaoSupport(connectionHolder.getConnection());
                    dao.update(this);
                }
                finally{
                    connectionHolder.close();
                }
                
                afterUpdate();
                commit(clasz);
                return true;
            }
            catch(Exception ex){
                rollback(clasz);
                throw new ActiveRecordException(ex);
            }
        }
    }
    
    /**
     * 保存对象，自动检测是进行create还是update操作
     * @return 成功返回true，失败返回false
     * @throws ActiveRecordException
     */
    public boolean save() throws ActiveRecordException{
        Class<?> clasz = this.getClass();
        if (this.isProxy()){
            clasz = clasz.getSuperclass();
        }
        OrmInfo orm = OrmInfo.getOrmInfo(clasz);

        try{
            beginTransaction(clasz);
            beforeSave();
            if (this.isNewRecord()){
                if (this.create() == false){
                    rollback(clasz);
                    return false;
                }
            }
            else{
                if (this.update() == false){
                    rollback(clasz);
                    return false;
                }
            }

            for(HasManyField field: orm.hasManyFields){
                String fieldName = field.getName();
                HasMany annotation = field.getAnnotation();
                Object idValue = OrmInfo.getFieldValue(clasz, orm.id, this);
                List<?> values = (List<?>)OrmInfo.getFieldValue(clasz, fieldName, this);
                if (values != null){
                    Class<?> childClass = field.getTargetType();
                    for(Object obj: values){
                        OrmInfo.setFieldValue(childClass, annotation.foreignKey(), obj, idValue);
                        ActiveRecordBase ar = (ActiveRecordBase)obj;
                        if (ar.save() == false){
                            this.getErrors().addAll(ar.getErrors());
                            rollback(clasz);
                            return false;
                        }
                    }
                }
            }
            for(HasOneField field: orm.hasOneFields){
                String fieldName = field.getName();
                HasOne annotation = field.getAnnotation();
                Object idValue = OrmInfo.getFieldValue(clasz, orm.id, this);
                Object obj = OrmInfo.getFieldValue(clasz, fieldName, this);
                if (obj != null){
                    Class<?> childClass = field.getTargetType();
                    OrmInfo.setFieldValue(childClass, annotation.foreignKey(), obj, idValue);
                    ActiveRecordBase ar = (ActiveRecordBase)obj;
                    if (ar.save() == false){
                        this.getErrors().addAll(ar.getErrors());
                        rollback(clasz);
                        return false;
                    }
                }
            }
            afterSave();
            commit(clasz);
            return true;
        }
        catch(Exception ex){
            rollback(clasz);
            throw new ActiveRecordException(ex);
        }
    }
    
    /**
     * 删除对象
     * @return 影响行数
     * @throws ActiveRecordException
     */
    public int destroy() throws ActiveRecordException{
        Class<?> clasz = this.getClass();
        if (this.isProxy()){
            clasz = clasz.getSuperclass();
        }
        try{
            OrmInfo orm = OrmInfo.getOrmInfo(clasz);
            
            beginTransaction(clasz);
            beforeDestroy();
            
            int updated = 0;
            Object idValue = OrmInfo.getFieldValue(clasz, orm.id, this);
            
            for(HasManyField field: orm.hasManyFields){
                if (field.getAnnotation().dependent() == DependentType.DELETE){
                    String cond = field.getForeignKey() + "=?";
                    Object[] args = new Object[]{idValue};
                    updated += ActiveRecordBase.deleteAll(field.getTargetType(), cond, args);
                }
                else if (field.getAnnotation().dependent() == DependentType.DESTROY){
                    String fieldName = field.getName();
                    Method method = clasz.getDeclaredMethod(OrmInfo.getMethodName(fieldName));
                    List<?> childObjects = (List<?>)method.invoke(this);
                    if (childObjects != null){
                        for(Object childObject: childObjects){
                            ActiveRecordBase ar = (ActiveRecordBase)childObject;
                            updated += ar.destroy();
                        }
                    }
                }
                else{
                    ActiveRecordBase.updateAll(field.getTargetType(), field.getForeignKey()+"=null", null, field.getForeignKey()+"=?", new Object[]{idValue});
                }
            }
            for(HasOneField field: orm.hasOneFields){
                if (field.getAnnotation().dependent() == DependentType.DELETE){
                    String cond = field.getForeignKey() + "=?";
                    Object[] args = new Object[]{idValue};
                    updated += ActiveRecordBase.deleteAll(field.getTargetType(), cond, args);
                }
                else if (field.getAnnotation().dependent() == DependentType.DESTROY){
                    String fieldName = field.getName();
                    Method method = clasz.getDeclaredMethod(OrmInfo.getMethodName(fieldName));
                    Object childObject = method.invoke(this);
                    if (childObject != null){
                        ActiveRecordBase ar = (ActiveRecordBase)childObject;
                        updated += ar.destroy();
                    }
                }
                else{
                    ActiveRecordBase.updateAll(field.getTargetType(), field.getForeignKey()+"=null", null, field.getForeignKey()+"=?", new Object[]{idValue});
                }
            }
            
            ConnectionHolder connectionHolder = new ConnectionHolder(clasz);
            try{
                DaoSupport dao = new DaoSupport(connectionHolder.getConnection());
                updated += dao.delete(this);
            }
            finally{
                connectionHolder.close();
            }
            
            afterDestroy();
            commit(clasz);
            return updated;
        }
        catch(Exception e){
            rollback(clasz);
            throw new ActiveRecordException(e);
        }
    }
    
    /**
     * 获取对象属性
     * @return 对象属性
     */
    public Map<String,Object> getAttributes(){
        Class<?> clasz = this.getClass();
        if (this.isProxy()){
            clasz = clasz.getSuperclass();
        }
        
        Map<String,Object> attr = new HashMap<String,Object>();
        for(Field f: clasz.getFields()){
            try{
                Object value = OrmInfo.getFieldValue(clasz, f.getName(), this);
                attr.put(f.getName(), value);
            }
            catch(FieldAccessException e){
            }
        }
        for(Method m: clasz.getDeclaredMethods()){
            String mname = m.getName();
            if (mname.startsWith("get")){
                String pname = mname.substring(3);
                if (pname.length()==1){
                    pname = pname.toLowerCase();
                }
                else{
                    pname = pname.substring(0,1).toLowerCase()+pname.substring(1);
                }
                
                try{
                    Object value = m.invoke(this);
                    attr.put(pname, value);
                }
                catch(Exception e){
                }
            }
        }
        return attr;
    }
    
    public void beforeCreate() throws ActiveRecordException{
    }
    
    public void afterCreate() throws ActiveRecordException{
    }
    
    public void beforeUpdate() throws ActiveRecordException{
    }
    
    public void afterUpdate() throws ActiveRecordException{
    }
    
    public void beforeSave() throws ActiveRecordException{
    }
    
    public void afterSave() throws ActiveRecordException{
    }
    
    public void beforeDestroy() throws ActiveRecordException{
    }
    
    public void afterDestroy() throws ActiveRecordException{
    }
}
