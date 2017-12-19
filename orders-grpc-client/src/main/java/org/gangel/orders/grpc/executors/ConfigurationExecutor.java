package org.gangel.orders.grpc.executors;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import org.gangel.orders.grpc.common.GlobalExceptionHandler;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.proto.ConfigurationResponse;
import org.gangel.orders.proto.ConfigurationServiceGrpc;
import org.gangel.orders.proto.IdRange;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ConfigurationExecutor implements Callable<Void> {

    @Override
    public Void call() throws Exception {
        GlobalExceptionHandler.register();

        final ManagedChannel channel = NettyChannelBuilder
                .forAddress(Configuration.host, Configuration.port).sslContext(GrpcSslContexts
                        .forClient().trustManager(new File(Configuration.certFilePath)).build())
                .build();
        
        ConfigurationResponse config = ConfigurationServiceGrpc.newBlockingStub(channel).getConfiguration(Empty.newBuilder().build());
        
        IdRange customerRange = config.getCustomerRange();
        IdRange productRange = config.getProductRange();
        IdRange ordersRange = config.getOrdersRange();
        
        Configuration.minCustomerId = customerRange.getMinId();
        Configuration.maxCustomerId = customerRange.getMaxId();
        Configuration.minProductId = productRange.getMinId();
        Configuration.maxProductId = productRange.getMaxId();
        Configuration.minOrdersId = ordersRange.getMinId();
        Configuration.maxOrdersId = ordersRange.getMaxId();
        
        System.out.println("Customers ids range : " + customerRange.toString());
        System.out.println("Product ids range : " + productRange.toString());
        System.out.println("Orders ids range : " + ordersRange.toString());
        
        channel.shutdown();
        channel.awaitTermination(5, TimeUnit.SECONDS);
        
        return null;
    }

}
