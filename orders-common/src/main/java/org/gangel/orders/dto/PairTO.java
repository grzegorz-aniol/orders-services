package org.gangel.orders.dto;

import java.io.Serializable;

public class PairTO<T> implements Serializable {

    private static final long serialVersionUID = 4095282984892139978L;

    public T first;
    
    public T second;

    public PairTO(T t1, T t2) {
        this.first = t1;
        this.second = t2;
    }
    
    public PairTO() {
    }
}
