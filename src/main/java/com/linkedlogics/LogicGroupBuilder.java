package com.linkedlogics;

import com.linkedlogics.flow.LogicFailure;
import com.linkedlogics.flow.LogicGroup;
import com.linkedlogics.flow.LogicSelection;
import com.linkedlogics.flow.LogicSeverity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

//    LogicGroupBuilder join(String... logics) ;
//
//    LogicGroupBuilder joinAll() ;
//
//    LogicGroupBuilder consume(String... events) ;
//
//    LogicGroupBuilder publish(String... events) ;
//
//    LogicGroupBuilder wait(String... event) ;
//
//    LogicGroupBuilder retry(String interval) ;
//
//    LogicGroupBuilder retry(int attempts, int delay, TimeUnit unit) ;
//
//    LogicGroupBuilder retryExceptions(Class<? extends Throwable>... classes) ;
//
//    LogicGroupBuilder loop(Class<? extends Throwable>... classes) ;
//
//    LogicGroupBuilder limits() ;
    //
//    LogicGroupBuilder timeout() ;

    LogicGroupBuilder anchor() ;

    LogicGroupBuilder hidden() ;

    LogicGroupBuilder input(String key, Object value) ;

    LogicGroupBuilder undo(String undo) ;

    default LogicGroupBuilder compensate(String compensate) {
        return undo(compensate) ;
    }

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

    LogicGroupBuilder errortags(String... tags) ;

    LogicGroupBuilder ifanytag(String... tags) ;

    LogicGroupBuilder ifnotags(String... tags) ;

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

    default LogicGroupBuilder executeall() {
        return selection(LogicSelection.executeAll) ;
    }

    default LogicGroupBuilder executeone() {
        return selection(LogicSelection.executeAny) ;
    }

    default LogicGroupBuilder executeany() {
        return selection(LogicSelection.executeOne) ;
    }
}
