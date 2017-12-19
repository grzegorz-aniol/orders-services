package org.gangel.orders.grpc.executors;

import io.grpc.ManagedChannel;
import io.netty.util.internal.ThreadLocalRandom;
import lombok.NoArgsConstructor;
import org.gangel.jperfstat.TrafficHistogram;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.proto.Customer;
import org.gangel.orders.proto.CustomerServiceGrpc;
import org.gangel.orders.proto.CustomerServiceGrpc.CustomerServiceFutureStub;
import org.gangel.orders.proto.GetCustomerRequest;
import org.gangel.orders.proto.NewCustomerRequest;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Function;

@NoArgsConstructor
public class CustomerServiceExecutor extends AbstractGrpcServiceExecutor {
    
    private Function<CustomerServiceFutureStub, GrpcCallEndpoint> requestFunction;
    
    public CustomerServiceExecutor(Function<CustomerServiceFutureStub, GrpcCallEndpoint> requestFunction) {
        this.requestFunction = requestFunction;
    }

    public static GrpcCallEndpoint getNewCustomerEndpoint(CustomerServiceFutureStub stub) {
        return new GrpcCallEndpoint("NewCustomerRequest", stub.createNewCustomer(NewCustomerRequest.newBuilder()
                .setCustomer(Customer.newBuilder()
                        .setName(UUID.randomUUID().toString().substring(0, 10))
                        .setLastname(UUID.randomUUID().toString().substring(0,10))
                        .setEmail("test@domain.com")
                        .build())
                .build())
                );
    }
    

    public static GrpcCallEndpoint getGetCustomerEndpoint(CustomerServiceFutureStub stub) {
        return new GrpcCallEndpoint("GetCustomerRequest", stub.getCustomer(GetCustomerRequest.newBuilder()
                .setId(ThreadLocalRandom.current()
                        .nextLong(Configuration.minCustomerId, Configuration.maxCustomerId))
                .build()));
    }    
    public static CustomerServiceExecutor getStreamOfNewCustomersRequestExecutor() {
        return new CustomerServiceExecutor((stub) -> {
            return null;
        });
    }

    public static Callable<TrafficHistogram> getGetCustomerRequestExecutor() {
        return new CustomerServiceExecutor(CustomerServiceExecutor::getGetCustomerEndpoint);
    }

    public static CustomerServiceExecutor getNewCustomerRequestExecutor() {
        return new CustomerServiceExecutor(CustomerServiceExecutor::getNewCustomerEndpoint);
    }
   
    protected CustomerServiceFutureStub stub; 

    @Override
    protected void onChannel(ManagedChannel channel) {
        stub = CustomerServiceGrpc.newFutureStub(channel);        
    }

    @Override
    protected GrpcCallEndpoint createNewRequest() {
        return requestFunction.apply(stub);
    }

}
