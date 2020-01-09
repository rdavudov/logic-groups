package com.linkedlogics.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Group {
    String value() default "";
    String parent() default "" ;
    String[] resource() default {} ;
    int precedence() default 0 ;
}
