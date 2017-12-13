package org.gangel.orders.job;

public class Configuration {
    public static String appName = "";
    public static int numOfIterations = 50_000;
    public static int numOfWarmIterations = 0;    
    public static int numOfThreads = 1;
    public static String certFilePath;
    public static String host = "localhost";
    public static int port = 6565;
    public static String jobName = "ping";
    
    public static JobType jobType = JobType.UNKNOWN;
    
    public static long minCustomerId = 1;
    public static long maxCustomerId = 10_000;
    
    public static long minProductId = 1;
    public static long maxProductId = 10_000;
    
    public static long minOrdersId = 1;
    public static long maxOrdersId = 10_000;
    
    public static boolean isSSL = false; 
    public static String sslCertFile;
    
    public static boolean isCSVFormat = true;
}
