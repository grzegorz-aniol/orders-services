package org.gangel.orders.grpc.executors;

import io.grpc.ManagedChannel;
import io.netty.util.internal.ThreadLocalRandom;
import org.gangel.jperfstat.TrafficHistogram;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.proto.GetProductRequest;
import org.gangel.orders.proto.NewProductRequest;
import org.gangel.orders.proto.Product;
import org.gangel.orders.proto.ProductServiceGrpc;
import org.gangel.orders.proto.ProductServiceGrpc.ProductServiceFutureStub;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ProductServiceExecutor extends AbstractGrpcServiceExecutor {

    private Function<ProductServiceFutureStub, GrpcCallEndpoint> requestFunction;
    
    private ProductServiceFutureStub stub;    
    
    public ProductServiceExecutor(Function<ProductServiceFutureStub, GrpcCallEndpoint> requestFunction) {
        this.requestFunction = requestFunction;
    }

    @Override
    protected void onChannel(ManagedChannel channel) {
        stub = ProductServiceGrpc.newFutureStub(channel);
    }
    
    @Override
    protected GrpcCallEndpoint createNewRequest() {
        return requestFunction.apply(stub);
    }
    
    public static GrpcCallEndpoint getNewProductEndpoint(ProductServiceFutureStub stub) {
        return new GrpcCallEndpoint(NewProductRequest.class.getSimpleName(), stub.createNewProduct(NewProductRequest.newBuilder()
                .setProduct(Product.newBuilder()
                        .setTitle(UUID.randomUUID().toString().substring(0, 10))
                        .setDescription(UUID.randomUUID().toString().substring(0,10))
                        .setPrice(ThreadLocalRandom.current().nextDouble(0.01, 1000.0))
                        .build())
                .build()));
    }
    

    public static GrpcCallEndpoint getGetProductEndpoint(ProductServiceFutureStub stub) {
        return new GrpcCallEndpoint(GetProductRequest.class.getSimpleName(), stub.getProduct(GetProductRequest.newBuilder()
                .setId(ThreadLocalRandom.current()
                        .nextLong(Configuration.minProductId, Configuration.maxProductId))
                .build()));
    }    
    
    public static ProductServiceExecutor getNewProductRequestExecutor() {
        return new ProductServiceExecutor(ProductServiceExecutor::getNewProductEndpoint);
    }

    public static Callable<TrafficHistogram> getGetProductRequestExecutor() {
        return new ProductServiceExecutor(ProductServiceExecutor::getGetProductEndpoint);
    }

}
