package org.gangel.orders.grpc.executors;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.GeneratedMessageV3;

public class GrpcCallEndpoint {

    private ListenableFuture<? extends GeneratedMessageV3> result;
    
    private final String path; 
    
    public GrpcCallEndpoint(final String p, ListenableFuture<? extends GeneratedMessageV3> result) {
        this.path = p;
        this.result = result; 
    }
    
    public String getPath() {
        return this.path; 
    }
    
    public ListenableFuture<? extends GeneratedMessageV3> getResult() {
        return this.result; 
    }
}
