package com.linkedlogics.parser;

import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.flow.LogicGroup;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

public class LogicGroupYamlParser implements LogicGroupParser {
    @Override
    public LogicGroup parse(String s) {
        return null;
    }

    public static void main(String[] args) {
        Binding binding = new Binding();
        binding.setVariable("builder", new DefaultLogicGroupBuilder("root"));
        GroovyShell shell = new GroovyShell(binding);

        try {
            LogicGroup value = (LogicGroup) shell.evaluate(new File("/home/rajab/usb/mywork/workspaces/primary/socrate/linked-logics/src/main/resources/scripts/group.groovy"));
            System.out.println(value.getClass());
            value.print();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
