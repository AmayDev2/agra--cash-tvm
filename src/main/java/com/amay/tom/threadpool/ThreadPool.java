package com.amay.tom.threadpool;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class ThreadPool {

    private final ExecutorService fixedThreadPool;
    private final ExecutorService cachedThreadPool;

    private final ExecutorService singleThread;
    private final ScheduledExecutorService scheduler;
    public ThreadPool(int n){
        fixedThreadPool = Executors.newFixedThreadPool(n);
        cachedThreadPool = Executors.newCachedThreadPool();
        singleThread = Executors.newSingleThreadExecutor();
        scheduler = Executors.newScheduledThreadPool(1);
            }
}
