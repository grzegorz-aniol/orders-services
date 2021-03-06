package org.gangel.orders.job;

import org.gangel.jperfstat.Histogram;
import org.gangel.jperfstat.Histogram.Statistics;
import org.gangel.jperfstat.HistogramStatsFormatter;
import org.gangel.jperfstat.ResultsTable;
import org.gangel.jperfstat.ResultsTable.RowBuilder;
import org.gangel.jperfstat.TrafficHistogram;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobManager implements Runnable {

    private Supplier<? extends Callable<TrafficHistogram>> requestTaskSupplier;
    private JobType jobType;

    public JobManager(JobType jobType, Supplier<? extends Callable<TrafficHistogram>> requestTaskSupplier) {
        this.jobType = jobType;
        this.requestTaskSupplier = requestTaskSupplier;
    }

    @Override
    public void run() {
        System.out.println(String.format("Starting job '%s' with %d threads", jobType.getName(), Configuration.numOfThreads));        
        System.out.println("Waiting for termination...");
        
        ExecutorService executor = Executors.newFixedThreadPool(Configuration.numOfThreads);
        List<Future<TrafficHistogram>> futures = null;
        long t0 = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(
                    Stream.generate(requestTaskSupplier)
                        .limit(Configuration.numOfThreads)
                        .collect(Collectors.toList()), 
                    300, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            System.err.println(e1.getMessage());
            e1.printStackTrace();
            return; 
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
        }
        
        List<TrafficHistogram> trafficHistograms;
        trafficHistograms = futures.stream().map(v -> {
            try {
                return v.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        
        long executionTime = System.currentTimeMillis() - t0;
        
        Map<String, Histogram.Statistics> statsMap = TrafficHistogram.getStats(trafficHistograms);
        long totalRequestsCount = (Configuration.numOfIterations * Configuration.numOfThreads);
        
        ResultsTable resultsTable = new ResultsTable();
        
        for (Iterator<Entry<String, Histogram.Statistics>>iter = statsMap.entrySet().iterator(); iter.hasNext(); ) {
            Entry<String, Statistics> item = iter.next();
            Statistics stats = item.getValue();
            
            RowBuilder rowBuilder = resultsTable.withRow(Configuration.appName);
            rowBuilder.set("Job", jobType.toString()); 
            rowBuilder.set("Path", item.getKey()); 
            HistogramStatsFormatter.addStatsRow(rowBuilder, stats);
            rowBuilder.set("IOPS", stats.requestCnt / (1e-3 * stats.getExecutionTime().toMillis()));
            rowBuilder.set("Total Job Time[s]", 1e-3 * executionTime);
            rowBuilder.set("Threads", Configuration.numOfThreads);
            rowBuilder.set("Requests", stats.requestCnt);
            rowBuilder.build();            
        }
        
        resultsTable.outputAsCsv();
    }

}
