package com.et.ar.validators;

import com.et.ar.ActiveRecordBase;
import com.et.ar.exception.ValidateException;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 数据验证抽象类，所有的验证类必须继承该类
 * @author stworthy
 */
public abstract class AbstractValidator<T> {
    protected T parameters;
    protected ResourceBundle validatorResource = null;
    
    public void init(T parameters){
        this.parameters = parameters;
        validatorResource = ActiveRecordBase.getCurrentValidatorResource();
    }
    
    public String getMessage() throws ValidateException{
        Class<?> c = parameters.getClass();
        String msg = "";
        try{
            msg = (String)c.getMethod("message").invoke(parameters);
        }
        catch(Exception e){
            throw new ValidateException("cannot find message property for " + parameters.toString(), e);
        }
        if (validatorResource == null){
            return msg;
        }
        
        try{
            String resourceMsg = validatorResource.getString(msg.trim());
            msg = resourceMsg;
            for(Method method: c.getDeclaredMethods()){
                if (method.getParameterTypes().length == 0){
                    String key = "\\{"+method.getName()+"\\}";
                    String value = method.invoke(parameters).toString();
                    msg = msg.replaceAll(key, value);
                }
            }
        }
        catch(MissingResourceException e1){
            throw new ValidateException("cannot find validation resource.", e1);
        }
        catch(Exception e2){
            throw new ValidateException(e2);
        }
        
        return msg;
    }
    
    protected abstract boolean validate(Object value) throws ValidateException;
}
