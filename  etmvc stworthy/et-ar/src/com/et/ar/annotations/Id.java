package com.et.ar.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    GeneratorType generate() default GeneratorType.AUTO;
}
