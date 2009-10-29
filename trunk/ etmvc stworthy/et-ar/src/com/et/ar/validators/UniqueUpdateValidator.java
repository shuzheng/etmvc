package com.et.ar.validators;

import com.et.ar.ActiveRecordBase;
import com.et.ar.exception.ValidateException;
import com.et.ar.annotations.Unique;

/**
 * 数据更新时唯一性验证类
 * @author stworthy
 */
public class UniqueUpdateValidator extends AbstractValidator<Unique>{
    private Class<?> c;
    private String tableName;
    private String fieldName;
    private String fieldId;
    private Object idValue;
    
    public UniqueUpdateValidator(Class<?> c, String tableName, String fieldName, String fieldId, Object idValue, Unique parameters){
        super.init(parameters);
        this.c = c;
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.fieldId = fieldId;
        this.idValue = idValue;
    }
    
    public boolean validate(Object value) throws ValidateException{
        try{
            String sql = "select count(*) from "+tableName+" where "+fieldName+"=? and "+fieldId+"<>?";
            Object[] args = new Object[]{value,idValue};
            Object count = ActiveRecordBase.executeScalar(c, sql, args);
            if (Integer.parseInt(count.toString()) > 0){
                return false;
            }
            else{
                return true;
            }
        }
        catch(Exception e){
            throw new ValidateException(e);
        }
    }
}
