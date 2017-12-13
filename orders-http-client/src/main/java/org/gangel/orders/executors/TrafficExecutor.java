package org.gangel.orders.executors;

import lombok.SneakyThrows;
import org.apache.http.client.methods.HttpUriRequest;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.rnd.CustomerGenerator;
import org.gangel.orders.rnd.OrdersGenerator;
import org.gangel.orders.rnd.ProductGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class TrafficExecutor extends AbstractTaskExecutor {

    private static final String ENDPOINT_CUSTOMERS = "/api2/customers";
    private static final String ENDPOINT_PRODUCTS = "/api2/products";
    private static final String ENDPOINT_ORDERS = "/api2/orders";

    @Override
    @SneakyThrows
    public HttpUriRequest requestSupplier() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        
        if (rnd.nextDouble() < 0.9) {
            // read operations
            double hit = rnd.nextDouble();
            
            if (hit < 0) {
                // get customer
                return requestGetBuilder(ENDPOINT_CUSTOMERS + "/" + rnd.nextLong(Configuration.minCustomerId, Configuration.maxCustomerId));
            } else if (hit < 0.9) {
                // get product
                return requestGetBuilder(ENDPOINT_PRODUCTS + "/" + rnd.nextLong(Configuration.minProductId, Configuration.maxProductId));
            } else {
                // get orders
                return requestGetBuilder(ENDPOINT_ORDERS + "/" + rnd.nextLong(Configuration.minOrdersId, Configuration.maxOrdersId));
            }
            
        } else {
            // create/update operations
            double hit = rnd.nextDouble();
            
            if (hit < 0.1) {
                // new customer
                String value = mapper.writeValueAsString(CustomerGenerator.generateCustomer());
                return requestPostBuilder(ENDPOINT_CUSTOMERS, value);
            } else if (hit < 0.9) {
                // get product
                String value = mapper.writeValueAsString(ProductGenerator.generateProduct());
                return requestPostBuilder(ENDPOINT_PRODUCTS, value);
            } else {
                // get orders
                String value = mapper.writeValueAsString(OrdersGenerator.generateOrders());
                return requestPostBuilder(ENDPOINT_ORDERS, value);
            }
            
        }

    }

}
