package org.gangel.orders.grpc.executors;

import io.grpc.ManagedChannel;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.proto.CustomerServiceGrpc;
import org.gangel.orders.proto.CustomerServiceGrpc.CustomerServiceFutureStub;
import org.gangel.orders.proto.OrdersServiceGrpc;
import org.gangel.orders.proto.OrdersServiceGrpc.OrdersServiceFutureStub;
import org.gangel.orders.proto.ProductServiceGrpc;
import org.gangel.orders.proto.ProductServiceGrpc.ProductServiceFutureStub;
import org.gangel.orders.rnd.Probability;

public class TrafficServiceExecutor extends AbstractGrpcServiceExecutor {

    private static final double READ_PROB = 0.9;
    private static final double WRITE_PROB = 1.0 - READ_PROB;
    private CustomerServiceFutureStub customerStub;
    private OrdersServiceFutureStub ordersStub;
    private ProductServiceFutureStub productStub; 
    
    @Override
    protected void onChannel(ManagedChannel channel) {
        ordersStub = OrdersServiceGrpc.newFutureStub(channel);
        customerStub = CustomerServiceGrpc.newFutureStub(channel);
        productStub = ProductServiceGrpc.newFutureStub(channel);
    }

    
    private double probSize(long min, long max) {
        if (max > 0) {
            return 1.0;
        }
        return 0.0;
    }
    
    @Override
    protected GrpcCallEndpoint createNewRequest() {
        
        double customersProb = probSize(Configuration.maxCustomerId, Configuration.minCustomerId);
        double productsProb = probSize(Configuration.maxProductId, Configuration.minProductId);
        double ordersProb = probSize(Configuration.maxOrdersId, Configuration.minOrdersId);
        
        return Probability.select(GrpcCallEndpoint.class)
            .with(customersProb * 0.1 * READ_PROB, () -> CustomerServiceExecutor.getGetCustomerEndpoint(customerStub))
            .with(productsProb * 0.8 * READ_PROB, () -> ProductServiceExecutor.getGetProductEndpoint(productStub))
            .with(ordersProb * 0.1 * READ_PROB, () -> OrdersServiceExecutor.getGetOrdersEndpoint(ordersStub))
            .with(0.1 * WRITE_PROB, () -> CustomerServiceExecutor.getNewCustomerEndpoint(customerStub))
            .with(0.1 * WRITE_PROB, () -> ProductServiceExecutor.getNewProductEndpoint(productStub))
            .with(0.8 * WRITE_PROB, () -> OrdersServiceExecutor.getNewOrdersEndpoint(ordersStub))
            .choose()
            .get();
    }

}
