package com.linkedlogics.flow.configure;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.lang.reflect.Method;

public interface Configurer {
    default boolean validateProfile(Class configurationClass, Environment environment) {
        if (configurationClass.isAnnotationPresent(Profile.class)) {
            Profile profile = (Profile) configurationClass.getAnnotation(Profile.class) ;
            return environment.acceptsProfiles(Profiles.of(profile.value()));
        }

        return true ;
    }

    default boolean validateProfile(Method method, Environment environment) {
        if (method.isAnnotationPresent(Profile.class)) {
            Profile profile = method.getAnnotation(Profile.class) ;
            return environment.acceptsProfiles(Profiles.of(profile.value()));
        }

        return true ;
    }
}
