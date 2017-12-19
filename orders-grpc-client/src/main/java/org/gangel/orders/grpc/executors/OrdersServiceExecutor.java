package org.gangel.orders.grpc.executors;

import io.grpc.ManagedChannel;
import io.netty.util.internal.ThreadLocalRandom;
import org.gangel.jperfstat.TrafficHistogram;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.proto.GetOrderRequest;
import org.gangel.orders.proto.NewOrderRequest;
import org.gangel.orders.proto.OrderItem;
import org.gangel.orders.proto.Orders;
import org.gangel.orders.proto.OrdersServiceGrpc;
import org.gangel.orders.proto.OrdersServiceGrpc.OrdersServiceFutureStub;
import org.gangel.orders.proto.PingRequest;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class OrdersServiceExecutor extends AbstractGrpcServiceExecutor {

    private Function<OrdersServiceFutureStub, GrpcCallEndpoint> requestFunction;

    protected OrdersServiceFutureStub stub;

    public OrdersServiceExecutor(
            Function<OrdersServiceFutureStub, GrpcCallEndpoint> requestFunction) {
        this.requestFunction = requestFunction;
    }

    @Override
    protected void onChannel(ManagedChannel channel) {
        stub = OrdersServiceGrpc.newFutureStub(channel);
    }

    @Override
    protected GrpcCallEndpoint createNewRequest() {
        return requestFunction.apply(stub);
    }

    public static OrdersServiceExecutor getPingExecutor() {
        return new OrdersServiceExecutor((stub) -> {
            return new GrpcCallEndpoint(PingRequest.class.getSimpleName(),
                    stub.ping(PingRequest.newBuilder().build()));
        });
    }

    public static GrpcCallEndpoint getNewOrdersEndpoint(OrdersServiceFutureStub stub) {

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        Orders.Builder ob = Orders.newBuilder().setCustomerId(
                rnd.nextLong(Configuration.minCustomerId, Configuration.maxCustomerId));

        for (int i = 0; i < 4; ++i) {
            ob.addOrderItem(OrderItem.newBuilder().setLineNumber(i + 1)
                    .setQuantity(rnd.nextInt(1, 17)).setAmount(rnd.nextInt(1, 1500))
                    .setProductId(
                            rnd.nextLong(Configuration.minProductId, Configuration.maxProductId))
                    .build());
        }

        return new GrpcCallEndpoint(NewOrderRequest.class.getSimpleName(),
                stub.addNewOrder(NewOrderRequest.newBuilder().setOrders(ob.build()).build()));
    }


    public static GrpcCallEndpoint getGetOrdersEndpoint(OrdersServiceFutureStub stub) {
        return new GrpcCallEndpoint(GetOrderRequest.class.getSimpleName(),
                stub.getOrder(GetOrderRequest
                        .newBuilder().setId(ThreadLocalRandom.current()
                                .nextLong(Configuration.minOrdersId, Configuration.maxOrdersId))
                        .build()));
    }

    public static OrdersServiceExecutor getNewOrdersRequestExecutor() {
        return new OrdersServiceExecutor(OrdersServiceExecutor::getNewOrdersEndpoint);
    }

    public static Callable<TrafficHistogram> getGetOrdersRequestExecutor() {
        return new OrdersServiceExecutor(OrdersServiceExecutor::getGetOrdersEndpoint);
    }

}
