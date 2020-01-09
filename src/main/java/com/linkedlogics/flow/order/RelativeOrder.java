package com.linkedlogics.flow.order;

import java.util.Set;

public class RelativeOrder implements LogicOrder {

    private Boolean isFirst ;
    private Boolean isLast ;
    private Set<String> beforeItems;
    private Set<String> afterItems;

    public Boolean getFirst() {
        return isFirst;
    }

    public void setFirst(Boolean first) {
        isFirst = first;
    }

    public Boolean getLast() {
        return isLast;
    }

    public void setLast(Boolean last) {
        isLast = last;
    }

    public Set<String> getBeforeItems() {
        return beforeItems;
    }

    public void setBeforeItems(Set<String> beforeItems) {
        this.beforeItems = beforeItems;
    }

    public Set<String> getAfterItems() {
        return afterItems;
    }

    public void setAfterItems(Set<String> afterItems) {
        this.afterItems = afterItems;
    }
}
