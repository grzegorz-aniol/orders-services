package org.gangel.orders.grpc.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.gangel.common.services.Pair;
import org.gangel.orders.grpc.service.data.CustomerDataService;
import org.gangel.orders.grpc.service.data.OrdersDataService;
import org.gangel.orders.grpc.service.data.ProductDataService;
import org.gangel.orders.proto.ConfigurationResponse;
import org.gangel.orders.proto.ConfigurationServiceGrpc.ConfigurationServiceImplBase;
import org.gangel.orders.proto.IdRange;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@GRpcService
@Component
public class ConfigurationGrpcService extends ConfigurationServiceImplBase  {

    @Autowired
    private CustomerDataService customerService;
    
    @Autowired
    private ProductDataService productService;
    
    @Autowired
    private OrdersDataService ordersService;
    
    private IdRange map(Pair<Long> pair) {
        return IdRange.newBuilder()
            .setMinId(pair != null && pair.first != null ? pair.first : 0L)
            .setMaxId(pair != null && pair.second != null ? pair.second : 0L)
            .build();
    }
    
    @Override
    public void getConfiguration(Empty request, StreamObserver<ConfigurationResponse> responseObserver) {
        responseObserver.onNext(ConfigurationResponse.newBuilder()
                .setCustomerRange(map(customerService.getIdsRange()))
                .setProductRange(map(productService.getIdsRange()))
                .setOrdersRange(map(ordersService.getIdsRange()))
                .build()
        );
        responseObserver.onCompleted();
    }
    
    
}
