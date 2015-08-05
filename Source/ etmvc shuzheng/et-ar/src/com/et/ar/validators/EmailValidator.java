package com.et.ar.validators;

import com.et.ar.annotations.Email;

/**
 * 电子邮件验证类
 * @author stworthy
 */
public class EmailValidator extends AbstractValidator<Email>{
    public boolean validate(Object value) {
        String parten = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}" +
                        "\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\" + 
                        ".)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        String s = (String)value;
        if (s == null){
            s = "";
        }
        if (s.matches(parten) == false){
            return false;
        }
        return true;
    }
}
