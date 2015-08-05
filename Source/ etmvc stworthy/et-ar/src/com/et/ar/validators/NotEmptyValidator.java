package com.et.ar.validators;

import com.et.ar.annotations.NotEmpty;

/**
 * 数据非空验证类
 * @author stworthy
 */
public class NotEmptyValidator extends AbstractValidator<NotEmpty>{
    public boolean validate(Object value) {
        if (value == null){
            return false;
        }
        if (value instanceof String){
            String s = (String)value;
            if (s.length() == 0){
                return false;
            }
        }
        return true;
    }
}
