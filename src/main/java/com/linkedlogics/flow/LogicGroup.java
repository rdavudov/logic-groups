package com.linkedlogics.flow;

import lombok.Data;

import java.util.*;

@Data
public class LogicGroup extends LogicItem {
    protected LogicSelection selection = LogicSelection.executeAll;
    protected List<LogicItem> items = new ArrayList<>() ;
    protected Set<String> exportedKeys ;
    protected Set<Class> exportedClasses ;
    protected String parent ;
    
    public LogicGroup(String name) {
        super(name, null) ;
    }

    public LogicGroup(String name, String parent) {
        super(name, null) ;
        this.parent = parent ;
    }

    public ListIterator<LogicItem> iterator() {
        return items.listIterator() ;
    }

    public void add(LogicItem item) {
        Optional<LogicItem> existing = items.stream().filter(i -> i.getName().equals(item.getName())).findFirst() ;
        if (existing.isPresent() && existing.get().getNamespace() == item.getNamespace()) {
            existing.get().merge(item);
        } else {
            items.add(item);
        }
    }

    public void merge(LogicItem item) {
        super.merge(item);

        if (item instanceof LogicGroup) {
            LogicGroup group = (LogicGroup) item ;

            if (group.getSelection() != null) {
                setSelection(group.getSelection());
            }

            if (group.getExportedClasses() != null) {
                setExportedClasses(group.getExportedClasses());
            }

            if (group.getExportedKeys() != null) {
                setExportedKeys(group.getExportedKeys());
            }

            for (LogicItem i : group.getItems()) {
                add(i);
            }
        }
    }

    private LogicGroup find(String name) {
        if (name == null) {
            return this ;
        } else {
            return find(name.split("\\."), 0) ;
        }
    }

    public LogicGroup find(String name, String namespace) {
        String[] splitted = name.split("\\.") ;
        if (namespace == null) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getName().equals(splitted[0]) && items.get(i).getNamespace() == null) {
                    return ((LogicGroup) items.get(i)).find(splitted, 1) ;
                }
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getName().equals(splitted[0]) && (items.get(i).getNamespace() != null && items.get(i).getNamespace().equals(namespace))) {
                    return ((LogicGroup) items.get(i)).find(splitted, 1);
                }
            }
        }

        return null ;
    }

    private LogicGroup find(String[] names, int nameIndex) {
        if (nameIndex == names.length) {
            return this ;
        }

        String name = names[nameIndex] ;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(name)) {
                return ((LogicGroup) items.get(i)).find(names, nameIndex + 1) ;
            }
        }

        return null ;
    }

    public void print(String tab) {
        System.out.println(tab + " GROUP " + name);
        for (LogicItem item : items) {
            item.print(tab + "\t");
        }
    }

    public String toString() {
        return "GROUP " + name ;
    }
}
