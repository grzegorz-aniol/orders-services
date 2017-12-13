package org.gangel.orders.executors;

import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.gangel.orders.job.Configuration;
import org.gangel.orders.rnd.CustomerGenerator;

public class NewCustomerExecutor extends AbstractTaskExecutor {

    public NewCustomerExecutor(){
    }
    
    @Override
    @SneakyThrows
    public HttpUriRequest requestSupplier() {
        String value = mapper.writeValueAsString(CustomerGenerator.generateCustomer());
        return requestPostBuilder("/api2/customers", value);
    }

    @Override
    public void responseConsumer(CloseableHttpResponse response, String body) {
        if (response != null) {
            long id = Long.parseLong(response.getFirstHeader("id").getValue());
            Configuration.maxCustomerId = Math.max(id, Configuration.maxCustomerId);
        }
    }

}
