package org.gangel.orders.grpc.executors;

import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import lombok.SneakyThrows;
import org.gangel.jperfstat.TrafficHistogram;
import org.gangel.orders.grpc.common.GlobalExceptionHandler;
import org.gangel.orders.job.Configuration;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractGrpcServiceExecutor implements Callable<TrafficHistogram> {
    
    private TrafficHistogram histogram;

    protected AbstractGrpcServiceExecutor() {
    }
    
    protected abstract void onChannel(final ManagedChannel channel);
    
    protected abstract GrpcCallEndpoint createNewRequest(); 
    
    public TrafficHistogram getHistogram() {
        return this.histogram;
    }

    @SneakyThrows
    private void sendRequest(boolean isWarmPhase) {
        long t0 = System.nanoTime();
        
        GrpcCallEndpoint endpoint = createNewRequest();
        Message response;
        try {
            response = endpoint.getResult().get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }                
        
        long endTime = System.nanoTime();
        
        if (!isWarmPhase) {
            histogram.put(endpoint.getPath(), t0, endTime);
        }

        if (response == null) {
            throw new RuntimeException("Wrong response: null object");
        }
        
    }
    
    public TrafficHistogram call() throws Exception {
        
        GlobalExceptionHandler.register();
        histogram = new TrafficHistogram(Configuration.numOfIterations);

        final ManagedChannel channel = NettyChannelBuilder
                .forAddress(Configuration.host, Configuration.port).sslContext(GrpcSslContexts
                        .forClient().trustManager(new File(Configuration.certFilePath)).build())
                .build();
        onChannel(channel);
        
        if (Configuration.numOfWarmIterations > 0) {
            for (long i = 0; i < Configuration.numOfWarmIterations; ++i) {
                sendRequest(true);
            }
        }

        histogram.setStartTime();
        for (long i = 0; i < Configuration.numOfIterations; ++i) {
            sendRequest(false);
        }
        histogram.setEndTime();

        channel.shutdown();
        channel.awaitTermination(5, TimeUnit.SECONDS);

        return histogram;
    }

}
