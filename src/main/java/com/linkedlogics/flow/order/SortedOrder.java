package com.linkedlogics.flow.order;

public class SortedOrder implements LogicOrder {
    private Integer order ;

    public SortedOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }
}
