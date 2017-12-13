package org.gangel.orders.rnd;

import org.gangel.orders.dto.CustomerTO;

import java.util.UUID;

public class CustomerGenerator {

    public static CustomerTO generateCustomer() {
        return CustomerTO.builder()
            .name(UUID.randomUUID().toString().substring(0, 10))
            .lastname(UUID.randomUUID().toString().substring(0,10))
            .email("test@domain.com")
            .build();
    }
    
}
