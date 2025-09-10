package com.amay.tom;

public class ThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        int numThreads = 100000; // same number as goroutines
        Thread[] threads = new Thread[numThreads];
        //System.out.println("Starting time in HH:MM:SS "+ java.time.LocalTime.now());

        for (int i = 0; i < numThreads; i++) {
            int id = i;
            threads[i] = new Thread(() -> {
                if (id < 5) {
                    //System.out.println("Thread " + id + " running");
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }

        //System.out.println("Completion time in HH:MM:SS "+ java.time.LocalTime.now());

        //System.out.println("All threads finished.");
    }
}
