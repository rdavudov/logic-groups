package com.linkedlogics.context;

import com.linkedlogics.exception.InvalidLogicOrderException;
import com.linkedlogics.flow.LogicGroup;
import com.linkedlogics.flow.LogicItem;
import com.linkedlogics.flow.order.RelativeOrder;
import com.linkedlogics.flow.order.SortedOrder;

import java.util.*;
import java.util.stream.Collectors;

public class LogicSorter {
    private static final int MAX_ORDER = 100000 ;

    public LogicGroup sort(LogicGroup group) {
        List<Ordered> orderedList = sortOrderedList(getOrderedList(group)) ;
        List<Ordered> unorderedList = getUnOrderedList(group) ;

        Set<String> orderedSet = new HashSet<>(orderedList.stream().map(o -> {
            return o.getItem().getName();
        }).collect(Collectors.toList())) ;

        int lastSize = unorderedList.size() ;
        while (unorderedList.size() > 0) {
            Iterator<Ordered> iterator = unorderedList.iterator() ;
            while (iterator.hasNext()) {
                Ordered unordered = iterator.next() ;
                if (!unordered.isBeforeOrderable(orderedSet) || !unordered.isAfterOrderable(orderedSet)) {
                    continue;
                }

                ListIterator<Ordered> orderedIterator = orderedList.listIterator() ;
                int end = getEnd(unordered, orderedIterator) ;
                int start = getStart(unordered, orderedIterator) ;

                setOrder(unordered, start, end);
                iterator.remove();

                orderedList.add(unordered) ;
                orderedSet.add(unordered.getItem().getName()) ;
                orderedList.sort(new Comparator<Ordered>() {
                    @Override
                    public int compare(Ordered o1, Ordered o2) {
                        return o1.getOrder() - o2.getOrder();
                    }
                });
            }

            if (unorderedList.size() > 0 && lastSize == orderedList.size()) {
                return missingDependency(unorderedList, orderedSet);
            }

            lastSize = orderedList.size() ;
        }

        group.setItems(orderedList.stream().map(o -> {
            return o.getItem() ;
        }).collect(Collectors.toList()));

        for (LogicItem item : group.getItems()) {
            if (item instanceof LogicGroup) {
                sort((LogicGroup) item) ;
            }
        }

        return group ;
    }

    private LogicGroup missingDependency(List<Ordered> unorderedList, Set<String> orderedSet) {
        HashSet<String> dependency = new HashSet<>() ;
        if (((RelativeOrder) unorderedList.get(0).getItem().getOrder()).getBeforeItems() != null) {
            dependency.addAll(((RelativeOrder) unorderedList.get(0).getItem().getOrder()).getBeforeItems()) ;
        }
        if (((RelativeOrder) unorderedList.get(0).getItem().getOrder()).getAfterItems() != null) {
            dependency.addAll(((RelativeOrder) unorderedList.get(0).getItem().getOrder()).getAfterItems()) ;
        }
        dependency.removeAll(orderedSet) ;
        throw new InvalidLogicOrderException("missing relative order items " + dependency.toString()) ;
    }

    private int getEnd(Ordered unordered, ListIterator<Ordered> orderedIterator) {
        while (orderedIterator.hasNext()) {
            Ordered ordered = orderedIterator.next() ;
            if (((RelativeOrder) unordered.getItem().getOrder()).getBeforeItems() != null && ((RelativeOrder) unordered.getItem().getOrder()).getBeforeItems().contains(ordered.getItem().getName())) {
                return ordered.getOrder() - 1 ;
            }
        }
        return MAX_ORDER ;
    }

    private int getStart(Ordered unordered, ListIterator<Ordered> orderedIterator) {
        while (orderedIterator.hasPrevious()) {
            Ordered ordered = orderedIterator.previous() ;
            if (((RelativeOrder) unordered.getItem().getOrder()).getAfterItems() != null && ((RelativeOrder) unordered.getItem().getOrder()).getAfterItems().contains(ordered.getItem().getName())) {
                return ordered.getOrder() + 1 ;
            }
        }
        return 0 ;
    }

    private List<Ordered> getOrderedList(LogicGroup group) {
        return group.getItems().stream().filter(i -> {
            return i.getOrder() instanceof SortedOrder || i.getOrder() == null ;
        }).sorted(new Comparator<LogicItem>() {
            @Override
            public int compare(LogicItem o1, LogicItem o2) {
                if (o1.getOrder() != null && o2.getOrder() != null) {
                    return ((SortedOrder) o1.getOrder()).getOrder() - ((SortedOrder) o2.getOrder()).getOrder();
                }
                return 0 ;
            }
        }).map(i -> {
            return new Ordered(i) ;
        }).collect(Collectors.toList());
    }

    private List<Ordered> getUnOrderedList(LogicGroup group) {
        return group.getItems().stream().filter(i -> {
            return i.getOrder() instanceof RelativeOrder ;
        }).map(i -> {
            return new Ordered(i) ;
        }).collect(Collectors.toList());
    }

    private void setOrder(Ordered unordered, int start, int end) {
        if (((RelativeOrder) unordered.getItem().getOrder()).getFirst() != null && ((RelativeOrder) unordered.getItem().getOrder()).getFirst() == true) {
            unordered.setOrder(start);
        } else if (((RelativeOrder) unordered.getItem().getOrder()).getLast() != null && ((RelativeOrder) unordered.getItem().getOrder()).getLast() == true) {
            unordered.setOrder(end);
        } else {
            unordered.setOrder((start + end) / 2);
        }
    }

    private List<Ordered> sortOrderedList(List<Ordered> orderedList) {
        int orderStep = MAX_ORDER / (orderedList.size() + 2) ;
        int index = orderStep ;
        for (Ordered o : orderedList) {
            o.setOrder(index);
            index += orderStep ;
        }

        orderedList.sort(new Comparator<Ordered>() {
            @Override
            public int compare(Ordered o1, Ordered o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

        return orderedList ;
    }

    private class Ordered {
        private LogicItem item ;
        private int order ;

        public Ordered(LogicItem item) {
            this.item = item ;
        }

        public LogicItem getItem() {
            return item ;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public boolean isBeforeOrderable(Set<String> ordered) {
            return isOrderable(((RelativeOrder) item.getOrder()).getBeforeItems(), ordered) ;
        }

        public boolean isAfterOrderable(Set<String> ordered) {
            return isOrderable(((RelativeOrder) item.getOrder()).getAfterItems(), ordered) ;
        }

        public boolean isOrderable(Set<String> source, Set<String> ordered) {
            if (source != null && source.size() > 0 && !ordered.containsAll(source)) {
                return false ;
            }
            return true ;
        }

        public String toString() {
            return item.toString() ;
        }
    }
}
