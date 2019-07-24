package com.amap.map3d.demo.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
    private ExecutorService executorService = null;


    private volatile static ThreadUtil instance;

    public static ThreadUtil getInstance() {
        if (instance == null) {
            synchronized(ThreadUtil.class) {
                if(instance == null) {
                    instance = new ThreadUtil();
                }
            }
        }
        return instance;
    }


    public static void destroy() {
        if(instance != null) {
            instance.executorService.shutdownNow();
            instance.executorService = null;
            instance = null;
        }
    }

    private  ThreadUtil() {

        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        int KEEP_ALIVE_TIME = 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;


        executorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES*2,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                taskQueue ,
                new ThreadPoolExecutor.AbortPolicy());
    }


    public void execute(Runnable command) {
        if(executorService != null) {
            executorService.execute(command);
        }
    }



}
