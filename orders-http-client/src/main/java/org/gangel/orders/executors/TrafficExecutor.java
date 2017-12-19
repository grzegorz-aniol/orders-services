package org.gangel.orders.executors;

import lombok.SneakyThrows;
import org.gangel.orders.rnd.Probability;

/**
 * Requests executor that simulates user's traffic
 * 
 * @author Grzegorz_Aniol
 *
 */
public class TrafficExecutor extends AbstractTaskExecutor {

    private static final double PROB_READ_DATA = 0.9;
    private static final double PROB_WRITE_DATA = 1 - PROB_READ_DATA;

    @Override
    @SneakyThrows
    public HttpCallRequest requestSupplier() {
        return Probability.select(HttpCallRequest.class)
             // read operations 
            .with(PROB_READ_DATA * 0.1, NewCustomerExecutor::getGetCustomerEndpoint)
            .with(PROB_READ_DATA * 0.8, NewProductExecutor::getGetProductEndpoint)
            .with(PROB_READ_DATA * 0.1, NewOrdersExecutor::getGetOrdersEndpoint)
            // write operations
            .with(PROB_WRITE_DATA * 0.1, NewCustomerExecutor::getNewCustomerEndpoint)
            .with(PROB_WRITE_DATA * 0.1, NewProductExecutor::getNewProductEndpoint)
            .with(PROB_WRITE_DATA * 0.8, NewOrdersExecutor::getNewOrdersEndpoint)
            .choose()
            .get();
    }

}
