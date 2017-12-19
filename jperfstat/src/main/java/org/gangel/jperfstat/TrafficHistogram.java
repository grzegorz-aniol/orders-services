package org.gangel.jperfstat;

import org.gangel.jperfstat.Histogram.Statistics;

import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Map of histograms for each path (endpoint)
 * 
 * Class aggregates performance/latency statistics for different service paths (e.g. endpoints)/  
 * As a result it returns statistical data grouped by service path.  
 * 
 * @author Grzegorz_Aniol
 *
 */
public class TrafficHistogram {

    private Map<String, Histogram> histogramPerPath = new HashMap<String,Histogram>();
    
    private int initialCapacity;
    
    private TemporalUnit unit;
    
    private long startTime;
    
    private long endTime; 
    
    /**
     * @param initialCapacity - estimated size of measured samples per each endpoint
     * @param unit - measurement units
     */
    public TrafficHistogram(int initialCapacity, TemporalUnit unit) {
        this.initialCapacity = initialCapacity;
        this.unit = unit; 
    }
    
    /**
     * @param initialCapacity - estimated size of measured samples per each endpoint
     */
    public TrafficHistogram(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.unit = Histogram.PRECISION_UNITS; 
    }
    
    public void setStartTime() {
        this.startTime = System.nanoTime();
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setEndTime() {
        this.endTime = System.nanoTime();
    }
    
    public long getEndTime() {
        return this.endTime; 
    }
    
    /**
     * Add performance/latency result for application path (endpoint) 
     * 
     * @param path Service, application endpoint or path. 
     * @param duration Performance result in defined units 
     */
    public void put(String path, long startTime, long endTime) {
        Histogram h = histogramPerPath.getOrDefault(path, new Histogram(initialCapacity, unit));        
        h.put(endTime - startTime);
        histogramPerPath.computeIfAbsent(path, p -> { h.setStartTime(startTime); return h; });
        h.setStopTime(endTime);
    }
    
    /**
     * Merge traffic histograms gathered by different threads and group them per each application endpoint.
     * Method produce histogram statistics per each endpoint 
     * @param trafficHistograms List of histograms gathered by different threads 
     * @return
     */
    public static Map<String, Histogram.Statistics> getStats(List<TrafficHistogram> trafficHistograms) {        
        // create a map of collections of histograms for such path (endpoint)
        HashMap<String, List<Histogram>> allHistograms = new HashMap<String, List<Histogram>>();
        Iterator<TrafficHistogram> iterTH = trafficHistograms.iterator();
        while(iterTH.hasNext()) {
            TrafficHistogram th = iterTH.next();
            th.histogramPerPath.entrySet().stream()
                .forEach(e -> { 
                    List<Histogram> list = allHistograms.getOrDefault(e.getKey(), new LinkedList<Histogram>());
                    list.add(e.getValue());
                    allHistograms.putIfAbsent(e.getKey(), list);
                } );
        }
        
        // merge each list of histograms for the same path
        HashMap<String, Statistics> result = new HashMap<String, Histogram.Statistics>();
        allHistograms.entrySet().stream()
            .forEach(e -> result.put(e.getKey(), Histogram.getStats(e.getValue())));
        
        return result; 
    }
}
