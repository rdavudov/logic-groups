package com.linkedlogics.context.validator;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicItem;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.HashMap;

public class ProfileValidator implements LogicValidator {
    private HashMap<String, Profiles> profilesMap = new HashMap<String, Profiles>() ;
    private Environment environment ;

    public ProfileValidator(Environment environment) {
        this.environment = environment ;
    }

    @Override
    public boolean execute(LogicItem item, AbstractLogicContext context) {
        if (item.getProfiles() == null) {
            return true ;
        }

        Profiles profiles = null ;
        if (profilesMap.containsKey(item.getProfiles())) {
            profiles = profilesMap.get(item.getProfiles()) ;
        } else {
            profiles = Profiles.of(item.getProfiles()) ;
            profilesMap.put(item.getProfiles(), profiles) ;
        }
        return environment.acceptsProfiles(profiles) ;
    }

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
