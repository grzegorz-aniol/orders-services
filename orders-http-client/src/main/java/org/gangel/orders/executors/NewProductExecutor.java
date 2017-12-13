package org.gangel.orders.executors;

import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.rnd.ProductGenerator;

public class NewProductExecutor extends AbstractTaskExecutor {

    public NewProductExecutor(){
    }
    
    @Override
    @SneakyThrows
    public HttpUriRequest requestSupplier() {
        String value = mapper.writeValueAsString(ProductGenerator.generateProduct());
        return requestPostBuilder("/api2/products", value);
    }

    @Override
    public void responseConsumer(CloseableHttpResponse response, String body) {
        if (response != null) {
            long id = Long.parseLong(response.getFirstHeader("id").getValue());
            Configuration.maxProductId = Math.max(id, Configuration.maxProductId);
        }
    }    
}
