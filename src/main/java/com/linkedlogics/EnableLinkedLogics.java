package com.linkedlogics;

import com.linkedlogics.config.LinkedLogicsConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LinkedLogicsConfiguration.class)
public @interface EnableLinkedLogics {

}
