//package com.amay.tom;
//
//import com.amay.tom.threadpool.ThreadPool;
//import org.h2.util.Task;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class ThreadDemo {
////    public static void main(String[] args) throws InterruptedException {
////        int numThreads = 100000; // same number as goroutines
////        Thread[] threads = new Thread[numThreads];
//
////
////
////        for (int i = 0; i < numThreads; i++) {
////            int id = i;
////            threads[i] = new Thread(() -> {
////                if (id < 5 || id > numThreads - 5) { // Print only for first and last 5 threads
////                    System.out.println("Thread " + id + " running");
////                }
////            });
////            threads[i].start();
////        }
////
////        for (int i = 0; i < numThreads; i++) {
////            threads[i].join();
////        }
////
////        System.out.println("Completion time in HH:MM:SS "+ java.time.LocalTime.now());
////
////        System.out.println("All threads finished.");
////    }
//
//    public static void main(String[] args) throws InterruptedException {
//        int numThreads = 100000; // same number as goroutines
//        Thread[] threads = new Thread[numThreads];
//        System.out.println("Starting time in HH:MM:SS " + java.time.LocalTime.now());
//
//        List<Task> task = new ArrayList<>();
//        for (int i = 0; i < numThreads; i++) {
//            int finalI = i;
//            task.add(new Task() {
//                @Override
//                public void call() throws Exception {
//                    if( finalI < 5 || finalI > 100000 - 5) { // Print only for first and last 5 threads
//                        System.out.println("Task " + finalI + " running "+java.time.LocalTime.now());
//                    }
//                }
//            });
//        }
//
//        ExecutorService  fixedThreadPool = Executors.newFixedThreadPool(10);
//        for (Task t : task){
//            fixedThreadPool.submit(t);
//        }
//
//        // Wait for all tasks to complete
//        fixedThreadPool.shutdown();
//        if (!fixedThreadPool.awaitTermination(1, TimeUnit.HOURS)) {
//            System.out.println("Some tasks did not finish within the timeout.");
//        }
//
//        System.out.println("Completion time in HH:MM:SS "+ java.time.LocalTime.now());
//
//        System.out.println("All threads finished.");
//    }
//}
