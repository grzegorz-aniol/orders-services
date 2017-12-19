package org.gangel.orders.executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.gangel.jperfstat.TrafficHistogram;
import org.gangel.orders.job.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

public abstract class AbstractTaskExecutor implements Callable<TrafficHistogram> {

    public static final String ENDPOINT_CUSTOMERS = "/api2/customers";
    public static final String ENDPOINT_PRODUCTS = "/api2/products";
    public static final String ENDPOINT_ORDERS = "/api2/orders";
    
    @Getter @NoArgsConstructor @AllArgsConstructor 
    protected static class HttpCallRequest {
        private String name; 
        private HttpUriRequest request;
    }
    
    @Getter
    private TrafficHistogram histogram; 
    
    protected ObjectMapper mapper = new ObjectMapper();
    
    public abstract HttpCallRequest requestSupplier();
    
    public void responseConsumer(CloseableHttpResponse response, String body) {        
    }
    
    public static String getProtocol() {
        return Configuration.isSSL ? "https://" : "http://";
    }
    
    public static HttpUriRequest requestGetBuilder(String path) {
        HttpGet getRequest = new HttpGet(getProtocol() + Configuration.host + ":" + Configuration.port + path);
        getRequest.addHeader("Accept", "application/json");
        return getRequest;
    }

    @SneakyThrows
    public static HttpUriRequest requestPostBuilder(String path, String body) {
        HttpPost postRequest = new HttpPost(
                    getProtocol() 
                    + Configuration.host 
                    + ":" 
                    + Configuration.port 
                    + path);
        postRequest.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        return postRequest;
    }

    private void sendRequest(CloseableHttpClient httpClient, boolean warmingPhase) throws ClientProtocolException, IOException {
        HttpCallRequest call = requestSupplier();

        long t0 = System.nanoTime();
        CloseableHttpResponse response = httpClient.execute(call.getRequest());
        int code = response.getStatusLine().getStatusCode();
        if (code/100 != 2) {
            throw new RuntimeException("Unexpected response code: " + code);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response.getEntity().writeTo(output);
        
        long endTime = System.nanoTime();

        if (!warmingPhase) {
            histogram.put(call.getName(), t0, endTime);            
            responseConsumer(response, output.toString("UTF-8"));
        }
        response.close();
        
    }
    
    @Override
    public TrafficHistogram call() throws Exception {
        
        histogram = new TrafficHistogram(Configuration.numOfIterations, ChronoUnit.NANOS);
        
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(Configuration.numOfIterations);
        cm.setDefaultMaxPerRoute(Configuration.numOfIterations);
        HttpHost localhost = new HttpHost(Configuration.host, Configuration.port);
        cm.setMaxPerRoute(new HttpRoute(localhost), Configuration.numOfIterations);

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        if (Configuration.numOfWarmIterations > 0) {
            for (long i = 0; i < Configuration.numOfWarmIterations; ++i) {
                sendRequest(httpClient, true);
            }
        }        
        
        histogram.setStartTime();
        for (long i = 0; i < Configuration.numOfIterations; ++i) {
            sendRequest(httpClient, false);
        }
        histogram.setEndTime();

        httpClient.close();
        
        return histogram;

    }

}
