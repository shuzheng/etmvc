package com.et.ar.annotations;

import com.et.ar.validators.NotEmptyValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidatorClass(NotEmptyValidator.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEmpty {
    String message() default "字段内容不能为空";
}
