package com.linkedlogics.core;

import com.linkedlogics.LogicGroupBuilder;
import com.linkedlogics.flow.*;
import com.linkedlogics.flow.order.RelativeOrder;
import com.linkedlogics.flow.order.SortedOrder;
import com.linkedlogics.parser.LogicGroupYamlParser;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class DefaultLogicGroupBuilder implements LogicGroupBuilder {
    private Stack<LogicGroup> stack = new Stack<LogicGroup>();
    private LogicItem item ;
    private List<String> resources = new ArrayList<String>() ;
    private ApplicationContext context ;

    public DefaultLogicGroupBuilder(String name, ApplicationContext context) {
        this.context = context ;
        item = new LogicGroup(name) ;
        stack.push((LogicGroup) item) ;
    }

    public DefaultLogicGroupBuilder group(String name) {
        item = new LogicGroup(name) ;
        stack.peek().add(item);
        stack.push((LogicGroup) item);
        return this ;
    }

    public DefaultLogicGroupBuilder logic(String name, String execute) {
        item = new LogicItem(name, execute) ;
        stack.peek().add(item);
        return this ;
    }

    public DefaultLogicGroupBuilder selection(LogicSelection selection) {
        stack.peek().setSelection(selection);
        return this ;
    }

    public DefaultLogicGroupBuilder description(String description) {
        item.setDescription(description);
        return this ;
    }

    public DefaultLogicGroupBuilder label(String label) {
        item.setLabel(label);
        return this ;
    }

    public DefaultLogicGroupBuilder order(int order) {
        item.setOrder(new SortedOrder(order));
        return this ;
    }

    public RelativeOrder orderRelative() {
        if (item.getOrder() == null && !(item.getOrder() instanceof RelativeOrder)) {
            item.setOrder(new RelativeOrder());
        }
        return (RelativeOrder) item.getOrder() ;
    }

    public DefaultLogicGroupBuilder first() {
        orderRelative().setFirst(true);
        return this ;
    }

    public DefaultLogicGroupBuilder last() {
        orderRelative().setLast(true);
        return this ;
    }

    public DefaultLogicGroupBuilder before(String... before) {
        orderRelative().setBeforeItems(Arrays.stream(before).collect(Collectors.toSet()));
        return this ;
    }

    public DefaultLogicGroupBuilder after(String... after) {
        orderRelative().setAfterItems(Arrays.stream(after).collect(Collectors.toSet()));
        return this ;
    }

    public DefaultLogicGroupBuilder profiles(String profiles) {
        item.setProfiles(profiles);
        return this ;
    }

    public DefaultLogicGroupBuilder condition(String condition) {
        item.setCondition(new LogicExpression(condition));
        return this ;
    }

    public DefaultLogicGroupBuilder flag(String flag) {
        item.setFlag(flag, true);
        return this ;
    }

    public DefaultLogicGroupBuilder unflag(String flag) {
        item.setFlag(flag, false);
        return this ;
    }

    public DefaultLogicGroupBuilder disable() {
        item.setFlag(LogicFlags.IS_DISABLED, true);
        return this ;
    }

    public DefaultLogicGroupBuilder enable() {
        item.setFlag(LogicFlags.IS_DISABLED, false);
        return this ;
    }

    public DefaultLogicGroupBuilder fork() {
        item.setFlag(LogicFlags.IS_FORKED, true);
        return this ;
    }

    public DefaultLogicGroupBuilder anchor() {
        item.setFlag(LogicFlags.IS_ANCHORED, true);
        return this ;
    }

    public DefaultLogicGroupBuilder hidden() {
        item.setFlag(LogicFlags.IS_HIDDEN, true);
        return this ;
    }

    public DefaultLogicGroupBuilder severity(LogicSeverity severity) {
        item.setSeverity(severity);
        return this ;
    }

    public DefaultLogicGroupBuilder input(String key, Object value) {
        if (LogicExpression.isExpression(value)) {
            item.setInput(key, new LogicExpression(value.toString()));
        } else {
            item.setInput(key, value);
        }
        return this ;
    }

    public DefaultLogicGroupBuilder output(String key, Object value) {
        if (LogicExpression.isExpression(value)) {
            item.setOutput(key, new LogicExpression(value.toString()));
        } else {
            item.setOutput(key, value);
        }

        return this ;
    }

    public DefaultLogicGroupBuilder output(String key) {
        item.setOutput(key, null);
        return this ;
    }

    public DefaultLogicGroupBuilder finish() {
        if (!stack.isEmpty()) {
            item = stack.pop();
        }
        return this ;
    }

    public DefaultLogicGroupBuilder undo(String undo) {
        item.setUndo(undo);
        return this ;
    }

    public LogicGroup build() {
        while (!stack.isEmpty()) {
            item = stack.pop() ;
        }
        return (LogicGroup) item ;
    }

    public DefaultLogicGroupBuilder resource(String resourcePath) throws IOException {
        Resource resource = context.getResource(resourcePath) ;
        InputStream in = resource.getInputStream() ;
        try (Reader reader = new InputStreamReader(resource.getInputStream(), "utf8")) {
            LogicGroup group = new LogicGroupYamlParser().parse(FileCopyUtils.copyToString(reader)) ;
            stack.peek().add(group);
        }
        return this ;
    }

    public DefaultLogicGroupBuilder file(String resourcePath) throws IOException {
        Resource resource = new FileUrlResource(resourcePath) ;
        if (resource.getFile().isDirectory()) {
            throw new IOException(resourcePath + " is not a file") ;
        }
        InputStream in = resource.getInputStream() ;
        try (Reader reader = new InputStreamReader(resource.getInputStream(), "utf8")) {
            LogicGroup group = new LogicGroupYamlParser().parse(FileCopyUtils.copyToString(reader)) ;
            stack.peek().add(group);
        }
        return this ;
    }

    public DefaultLogicGroupBuilder folder(String resourcePath) throws IOException {
        Resource resource = new FileUrlResource(resourcePath) ;
        if (!resource.getFile().isDirectory()) {
            throw new IOException(resourcePath + " is not a directory") ;
        }
        stack.peek().add(read(resource.getFile()));
        return this ;
    }

    private LogicGroup read(File directory) {
        final LogicGroup group = new LogicGroup(directory.getName()) ;

        Arrays.stream(directory.listFiles()).forEach(f -> {
            if (f.isDirectory()) {
                group.add(read(f));
            } else {
                try (Reader reader = new FileReader(f)) {
                    group.add(new LogicGroupYamlParser().parse(FileCopyUtils.copyToString(reader)));
                } catch (Exception e) {
                    throw new RuntimeException(e) ;
                }
            }
        });

        return group ;
    }

    public DefaultLogicGroupBuilder url(String resourcePath) throws IOException {
        Resource resource = new UrlResource(resourcePath) ;
        InputStream in = resource.getInputStream() ;
        try (Reader reader = new InputStreamReader(resource.getInputStream(), "utf8")) {
            LogicGroup group = new LogicGroupYamlParser().parse(FileCopyUtils.copyToString(reader)) ;
            stack.peek().add(group);
        }
        return this ;
    }

    public DefaultLogicGroupBuilder namespace(String namespace) {
        item.setNamespace(namespace);
        return this ;
    }

    @Override
    public DefaultLogicGroupBuilder export(String... keys) {
        item.setExportedKeys(Arrays.stream(keys).collect(Collectors.toSet()));
        return this ;
    }

    @Override
    public DefaultLogicGroupBuilder export(Class... classes) {
        item.setExportedClasses(Arrays.stream(classes).collect(Collectors.toSet()));
        return this ;
    }
    @Override
    public DefaultLogicGroupBuilder tag(String... tags) {
        item.setTags(Arrays.stream(tags).collect(Collectors.toSet()));
        return this ;
    }
    @Override
    public DefaultLogicGroupBuilder untag(String... tags) {
        item.setUntags(Arrays.stream(tags).collect(Collectors.toSet()));
        return this ;
    }
    @Override
    public DefaultLogicGroupBuilder iftags(String... tags) {
        item.setIftags(Arrays.stream(tags).collect(Collectors.toSet()));
        return this ;
    }
}
