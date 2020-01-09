package com.linkedlogics.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncLogic {
    long timeout() ;
    TimeUnit unit() default TimeUnit.MILLISECONDS ;
}
