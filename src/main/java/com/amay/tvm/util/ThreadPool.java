package com.amay.tvm.util;

import java.util.concurrent.*;

public class ThreadPool {

    private final ExecutorService fixedThreadPool;
    private final ExecutorService cachedThreadPool;
    private final ExecutorService singleThread;
    private final ScheduledExecutorService scheduler;

    // Private constructor
    private ThreadPool(int n) {
        fixedThreadPool = Executors.newFixedThreadPool(n);
        cachedThreadPool = Executors.newCachedThreadPool();
        singleThread = Executors.newSingleThreadExecutor();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    // Holder class for lazy-loaded singleton
    private static class Holder {
        // You can define the number of threads here, e.g., 10
        private static final ThreadPool INSTANCE = new ThreadPool(10);
    }

    // Public method to get singleton instance
    public static ThreadPool getInstance() {
        return Holder.INSTANCE;
    }

    // Getters for thread pools
    public ExecutorService getFixedThreadPool() {
        return fixedThreadPool;
    }

    public ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }

    public ExecutorService getSingleThread() {
        return singleThread;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    // Shutdown all pools
    public void shutdown() {
        fixedThreadPool.shutdown();
        cachedThreadPool.shutdown();
        singleThread.shutdown();
        scheduler.shutdown();
    }
}
