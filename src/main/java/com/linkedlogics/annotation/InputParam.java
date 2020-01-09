package com.linkedlogics.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InputParam {
    String value() default "";

    boolean required() default false;

    String defaultValue() default "" ;
}
