package org.gangel.orders.executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.rnd.ProductGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class NewProductExecutor extends AbstractTaskExecutor {

    public NewProductExecutor(){
    }

    @SneakyThrows
    public static HttpCallRequest getGetProductEndpoint() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new HttpCallRequest("GetProduct", requestGetBuilder(ENDPOINT_PRODUCTS + "/"
                + rnd.nextLong(Configuration.minProductId, Configuration.maxProductId)));        
    }
    
    @SneakyThrows
    public static HttpCallRequest getNewProductEndpoint() {
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(ProductGenerator.generateProduct());
        return new HttpCallRequest("CreateProduct",requestPostBuilder("/api2/products", value));        
    }
    
    @Override
    @SneakyThrows
    public HttpCallRequest requestSupplier() {
        return NewProductExecutor.getNewProductEndpoint();
    }

    @Override
    public void responseConsumer(CloseableHttpResponse response, String body) {
        if (response != null) {
            long id = Long.parseLong(response.getFirstHeader("id").getValue());
            Configuration.maxProductId = Math.max(id, Configuration.maxProductId);
        }
    }    
}
