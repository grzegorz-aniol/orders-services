package org.gangel.jperfstat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.gangel.jperfstat.Histogram.Statistics;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistogramTrafficTest {

    @Test
    public void testEmptyHistograms() {
        List<TrafficHistogram> allTraffic = new ArrayList<TrafficHistogram>(5);
        for (int n=0; n < 5; n++) {
            TrafficHistogram th = new TrafficHistogram(100, ChronoUnit.MILLIS);
            allTraffic.add(th);
        }
        Map<String, Statistics> stats = TrafficHistogram.getStats(allTraffic);
        MatcherAssert.assertThat(stats, notNullValue());
        MatcherAssert.assertThat(stats.size(), equalTo(0));
    }
    
    @Test
    public void testAllHistogramPaths() {
        final String PATH_1 = "/customers";
        final String PATH_2 = "/users";
        final String PATH_3 = "/products";
        
        List<TrafficHistogram> allTraffic = new ArrayList<TrafficHistogram>(5);
        for (int n=0; n < 5; n++) {
            TrafficHistogram th = new TrafficHistogram(100, ChronoUnit.MILLIS);
            for (int i=0; i<100;++i) {
                long t0 = System.nanoTime();
                th.put(PATH_1, t0, t0 + n*100 + 10+i);            
                th.put(PATH_2, t0, t0 + n*100 + 20+10*i);            
                th.put(PATH_3, t0, t0 + n*100 + 30+100*i);            
            }
            allTraffic.add(th);
        }
        
        Map<String, Statistics> stats = TrafficHistogram.getStats(allTraffic);
        
        assertThat(stats.size(), equalTo(3));
        assertThat(stats.containsKey(PATH_1), equalTo(true));
        assertThat(stats.containsKey(PATH_2), equalTo(true));
        assertThat(stats.containsKey(PATH_3), equalTo(true));
        
        assertThat(stats.get(PATH_1).p0th.toMillis(), equalTo(10L));
        assertThat(stats.get(PATH_2).p0th.toMillis(), equalTo(20L));
        assertThat(stats.get(PATH_3).p0th.toMillis(), equalTo(30L));
        
        assertThat(stats.get(PATH_1).p100th.toMillis(), equalTo(4*100L + 10 + 1*99));
        assertThat(stats.get(PATH_2).p100th.toMillis(), equalTo(4*100L + 20 + 10*99));
        assertThat(stats.get(PATH_3).p100th.toMillis(), equalTo(4*100L + 30 + 100*99));
        
    }
    
}
