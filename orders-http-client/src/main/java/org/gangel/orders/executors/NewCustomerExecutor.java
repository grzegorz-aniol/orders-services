package org.gangel.orders.executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.rnd.CustomerGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class NewCustomerExecutor extends AbstractTaskExecutor {

    public NewCustomerExecutor(){
    }
    
    @SneakyThrows
    public static HttpCallRequest getGetCustomerEndpoint() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new HttpCallRequest("GetCustomer", requestGetBuilder(ENDPOINT_CUSTOMERS + "/"
            + rnd.nextLong(Configuration.minCustomerId, Configuration.maxCustomerId)));
    }
    
    
    @SneakyThrows
    public static HttpCallRequest getNewCustomerEndpoint() {
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(CustomerGenerator.generateCustomer());
        return new HttpCallRequest("CreateCustomer", requestPostBuilder("/api2/customers", value));
    }
    
    @Override
    public HttpCallRequest requestSupplier() {
        return NewCustomerExecutor.getNewCustomerEndpoint();
    }

    @Override
    public void responseConsumer(CloseableHttpResponse response, String body) {
        if (response != null) {
            long id = Long.parseLong(response.getFirstHeader("id").getValue());
            Configuration.maxCustomerId = Math.max(id, Configuration.maxCustomerId);
        }
    }

}
