package org.gangel.orders.executors;

import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.rnd.OrdersGenerator;

public class NewOrdersExecutor extends AbstractTaskExecutor {

    public NewOrdersExecutor(){
    }
    
    @Override
    @SneakyThrows
    public HttpUriRequest requestSupplier() {
        String value = mapper.writeValueAsString(OrdersGenerator.generateOrders());
        return requestPostBuilder("/api2/orders", value);
    }

    @Override
    public void responseConsumer(CloseableHttpResponse response, String body) {
        if (response != null) {
            long id = Long.parseLong(response.getFirstHeader("id").getValue());
            Configuration.maxOrdersId = Math.max(id, Configuration.maxOrdersId);
        }
    }     
    
}
