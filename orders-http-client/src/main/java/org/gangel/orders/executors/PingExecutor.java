package org.gangel.orders.executors;

public class PingExecutor extends AbstractTaskExecutor {

    @Override
    public HttpCallRequest requestSupplier() {
        return new HttpCallRequest("Ping",requestGetBuilder("/"));
    }

}
