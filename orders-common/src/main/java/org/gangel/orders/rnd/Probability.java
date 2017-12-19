package org.gangel.orders.rnd;

import lombok.val;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Probability {
    
    public static class Select<T> {
        private double weigth; 
        private Supplier<T> supplier;
        
        private Select(double w, Supplier<T> sup) {
            this.weigth = w;
            this.supplier = sup;
        }
    };
    
    public static class SelectBuilder<T> {
        private List<Select<T>> list = new LinkedList<Select<T>>();
        
        private SelectBuilder() {
        }
        
        public SelectBuilder<T> with(double w, Supplier<T> s) {
            Select<T> obj = new Select<T>(w,s);
            list.add(obj);
            return this; 
        }
        
        public Supplier<T> choose() {
            if (list == null || list.size() == 0) {
                return null;
            }
            final double sum = list.stream().map(e -> e.weigth).collect(Collectors.summingDouble(e->e));
            list.stream().forEach( e -> { e.weigth=e.weigth/sum; } );
            double rnd = ThreadLocalRandom.current().nextDouble(sum); 
            
            double localSum = 0;
            Iterator<Select<T>> i = list.iterator();
            
            while (i.hasNext()) {
                val item = i.next();
                localSum += item.weigth;
                if (rnd <= localSum) {
                    return item.supplier; 
                }
            }
            
            return null;
        }
    }
    
    public static <T> SelectBuilder<T> select(Class<T> elements) {
        return new SelectBuilder<T>();
    }

}
