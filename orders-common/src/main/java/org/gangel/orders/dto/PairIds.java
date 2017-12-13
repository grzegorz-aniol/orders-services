package org.gangel.orders.dto;

import lombok.val;

public class PairIds extends PairTO<Long> {

    private static final long serialVersionUID = 4415712381114475421L;

    public PairIds(long first, long second) {
        super(first, second);
    }
    
    public PairIds() {
    }
    
    @Override
    public String toString() {
        val sb = new StringBuilder();
        sb.append("Min id=");
        sb.append(first != null ? first.toString() : "N/A");
        sb.append(", max id=");
        sb.append(second != null ? second.toString() : "N/A");
        return sb.toString();
    }
}
