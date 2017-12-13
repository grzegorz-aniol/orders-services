package org.gangel.orders.rnd;

import org.gangel.orders.dto.OrderItemTO;
import org.gangel.orders.dto.OrdersTO;
import org.gangel.orders.dto.OrdersTO.OrdersTOBuilder;
import org.gangel.orders.job.Configuration;

import java.util.concurrent.ThreadLocalRandom;

public class OrdersGenerator {

    public static OrdersTO generateOrders() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        
        OrdersTOBuilder ob = OrdersTO.builder()
                .customerId(rnd.nextLong(Configuration.minCustomerId, Configuration.maxCustomerId));
        
        for (int i=0; i < 4; ++i) {
            ob.orderItem(OrderItemTO.builder()
                    .position(i+1)
                    .quantity(rnd.nextInt(1,17))
                    .amount((double)rnd.nextInt(1,1500))
                    .productId(rnd.nextLong(Configuration.minProductId,Configuration.maxProductId))
                    .build()
            );
        }
        
        return ob.build();
    }
}
