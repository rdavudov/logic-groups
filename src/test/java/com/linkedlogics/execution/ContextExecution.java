package com.linkedlogics.execution;

import com.linkedlogics.core.DefaultLogicContext;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class ContextExecution {
    public static void main(String[] args) {
        DefaultLogicContext ctx = new DefaultLogicContext(null) ;
        Request r = new Request() ;
        r.setAge(15) ;
        r.setName("duke");
        r.setMap(new HashMap<>());
        r.getMap().put("k1", new HashMap<>() {{
            put("k2", "v2") ;
        }}) ;

        ctx.setContextParam("request", r);
        System.out.println(ctx.evaluate("#request.map['k1']['k2']"));
    }

    @Data
    private static class Request {
        private String name ;
        private int age ;
        private Map<String, Object> map ;
    }
}
