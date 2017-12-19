package org.gangel.orders.executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.rnd.OrdersGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class NewOrdersExecutor extends AbstractTaskExecutor {

    public NewOrdersExecutor(){
    }
    
    @SneakyThrows
    public static HttpCallRequest getGetOrdersEndpoint() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new HttpCallRequest("GetOrders", requestGetBuilder(ENDPOINT_ORDERS + "/"
                + rnd.nextLong(Configuration.minOrdersId, Configuration.maxOrdersId)));        
    }
    
    @SneakyThrows
    public static HttpCallRequest getNewOrdersEndpoint() {
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(OrdersGenerator.generateOrders());
        return new HttpCallRequest("CreateOrders",requestPostBuilder("/api2/orders", value));        
    }
    
    @Override
    @SneakyThrows
    public HttpCallRequest requestSupplier() {
        return NewOrdersExecutor.getNewOrdersEndpoint();
    }

    @Override
    public void responseConsumer(CloseableHttpResponse response, String body) {
        if (response != null) {
            long id = Long.parseLong(response.getFirstHeader("id").getValue());
            Configuration.maxOrdersId = Math.max(id, Configuration.maxOrdersId);
        }
    }     
    
}
