package org.gangel.orders.executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.gangel.orders.dto.PairIds;
import org.gangel.orders.job.Configuration;

import java.io.IOException;

public class ConfigurationExecutor {
    
    private ObjectMapper mapper = new ObjectMapper();

    public void call() throws IOException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(Configuration.numOfIterations);
        cm.setDefaultMaxPerRoute(Configuration.numOfIterations);
        HttpHost localhost = new HttpHost(Configuration.host, Configuration.port);
        cm.setMaxPerRoute(new HttpRoute(localhost), Configuration.numOfIterations);

        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build()) {
            PairIds range = getRange(httpClient, "/api2/customers/range");
            Configuration.minCustomerId = (range != null ? (range.first!=null ? range.first : 0) :  0); 
            Configuration.maxCustomerId = (range != null ? (range.second!=null ? range.second : 0) :  0);
            System.out.println("Customers ids range : " + range.toString());
            
            range = getRange(httpClient, "/api2/products/range");
            Configuration.minProductId = (range != null ? (range.first!=null ? range.first : 0) :  0); 
            Configuration.maxProductId = (range != null ? (range.second!=null ? range.second : 0) :  0);  
            System.out.println("Products ids range : " + range.toString());
            
            range = getRange(httpClient, "/api2/orders/range");
            Configuration.minOrdersId = (range != null ? (range.first!=null ? range.first : 0) :  0); 
            Configuration.maxOrdersId = (range != null ? (range.second!=null ? range.second : 0) :  0); 
            System.out.println("Orders ids range : " + range.toString());
        };
    }

    private PairIds getRange(CloseableHttpClient httpClient, String endpoint) {
        HttpGet getRequest = new HttpGet(AbstractTaskExecutor.getProtocol() + Configuration.host + ":" + Configuration.port + endpoint);
        getRequest.addHeader("Accept", "application/json");        
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Wrong result code!"); 
            }
            return mapper.readValue(response.getEntity().getContent(), PairIds.class);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
