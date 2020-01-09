package com.linkedlogics.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryLogic {
    int attempts() ;
    long delay() ;
    TimeUnit unit() default TimeUnit.MILLISECONDS ;
    Class[] include() default {Throwable.class} ;
    Class[] exclude() default {} ;
}
