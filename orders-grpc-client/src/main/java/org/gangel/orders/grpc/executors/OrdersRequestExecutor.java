package org.gangel.orders.grpc.executors;

import io.grpc.stub.StreamObserver;
import org.gangel.jperfstat.TrafficHistogram;
import org.gangel.orders.proto.OrdersServiceGrpc;
import org.gangel.orders.proto.OrdersServiceGrpc.OrdersServiceStub;
import org.gangel.orders.proto.PingRequest;
import org.gangel.orders.proto.PingResponse;

import java.util.concurrent.Callable;

public abstract class OrdersRequestExecutor<
    R extends com.google.protobuf.GeneratedMessageV3, 
    O extends com.google.protobuf.GeneratedMessageV3> 
        extends AbstractGrpcAsyncExecutor<OrdersServiceStub, R, O>  {

    public OrdersRequestExecutor() {
        super(channel -> OrdersServiceGrpc.newStub(channel));
    }
    
    public static Callable<TrafficHistogram> newPingsExecutor() {
        return new OrdersRequestExecutor<PingRequest, PingResponse>() {

            @Override
            protected StreamObserver<PingRequest> getRpcCall(OrdersServiceStub stub,
                    StreamObserver<PingResponse> response) {
                return stub.pings(response);
            }

            @Override
            protected PingRequest produceRequest() {
                return PingRequest.newBuilder().build();
            }
            
        };
    }
}
