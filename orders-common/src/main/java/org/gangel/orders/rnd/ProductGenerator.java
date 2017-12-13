package org.gangel.orders.rnd;

import org.gangel.orders.dto.ProductTO;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProductGenerator {

    public static  ProductTO generateProduct() {
        return ProductTO.builder()
        .title(UUID.randomUUID().toString().substring(0, 10))
        .description(UUID.randomUUID().toString().substring(0,10))
        .price(ThreadLocalRandom.current().nextDouble(0.01, 1000.0))
        .build();
    }
}
