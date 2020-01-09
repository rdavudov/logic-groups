package com.linkedlogics.execution;

import com.linkedlogics.annotation.ContextParam;
import com.linkedlogics.annotation.InputParam;
import com.linkedlogics.annotation.Logic;
import com.linkedlogics.annotation.LogicConfiguration;
import com.linkedlogics.exception.LogicException;
import lombok.extern.java.Log;

import java.util.List;

@LogicConfiguration
public class BasicActions {

    @Logic("add")
    public void add(@ContextParam(value = "list") List<String> list, @InputParam(value = "item") String item) {
        list.add(item) ;
    }

    @Logic(value = "remove", returns = "removed")
    public boolean remove(@ContextParam("list") List<String> list, @InputParam("item") String item) {
       return list.remove(item) ;
    }

    @Logic("error")
    public void error(@InputParam("error_code") Long errorCode, @InputParam("error_message") String errorMessage) {
        throw new LogicException(errorCode, errorMessage) ;
    }
}
