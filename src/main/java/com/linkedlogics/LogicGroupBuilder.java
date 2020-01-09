package com.linkedlogics;

import com.linkedlogics.flow.LogicFailure;
import com.linkedlogics.flow.LogicGroup;
import com.linkedlogics.flow.LogicSelection;
import com.linkedlogics.flow.LogicSeverity;

import java.io.IOException;

public interface LogicGroupBuilder {
    LogicGroupBuilder group(String name) ;

    LogicGroupBuilder logic(String name, String execute) ;

    LogicGroupBuilder selection(LogicSelection selection) ;

    LogicGroupBuilder description(String description) ;

    LogicGroupBuilder label(String label) ;

    LogicGroupBuilder order(int order) ;

    LogicGroupBuilder export(String... keys) ;

    LogicGroupBuilder export(Class... classes) ;

    LogicGroupBuilder first() ;

    LogicGroupBuilder last() ;

    LogicGroupBuilder before(String... before) ;

    LogicGroupBuilder after(String... after) ;

    LogicGroupBuilder profiles(String profiles) ;

    LogicGroupBuilder condition(String condition) ;

    LogicGroupBuilder flag(String flag) ;

    LogicGroupBuilder unflag(String flag) ;

    LogicGroupBuilder disable() ;

    LogicGroupBuilder enable() ;

    LogicGroupBuilder fork() ;

    LogicGroupBuilder anchor() ;

    LogicGroupBuilder hidden() ;

    LogicGroupBuilder input(String key, Object value) ;

    LogicGroupBuilder output(String key, Object value) ;

    LogicGroupBuilder output(String key) ;

    LogicGroupBuilder undo(String undo) ;

    LogicGroupBuilder finish() ;

    LogicGroup build() ;

    LogicGroupBuilder resource(String resourcePath) throws IOException;

    LogicGroupBuilder file(String resourcePath) throws IOException ;

    LogicGroupBuilder folder(String resourcePath) throws IOException ;

    LogicGroupBuilder url(String resourcePath) throws IOException ;

    LogicGroupBuilder namespace(String namespace) ;

    LogicGroupBuilder tag(String... tags) ;

    LogicGroupBuilder untag(String... tags) ;

    LogicGroupBuilder iftags(String... tags) ;

    LogicGroupBuilder severity(LogicSeverity severity) ;

    default LogicGroupBuilder fatal() {
        return severity(LogicSeverity.fatal) ;
    }

    default LogicGroupBuilder high() {
        return severity(LogicSeverity.high) ;
    }

    default LogicGroupBuilder medium() {
        return severity(LogicSeverity.medium) ;
    }

    default LogicGroupBuilder low() {
        return severity(LogicSeverity.low) ;
    }


    default LogicGroupBuilder ns(String namespace) {
        return namespace(namespace) ;
    }
}
