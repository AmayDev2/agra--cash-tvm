package com.amay.tom.utils.helper;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.database.RedisConnectionPool;
import com.amay.tom.model.PeripheralStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tinylog.Logger;

import java.util.Map;
import java.util.UUID;

public class Helper {
    public static String ObjectToJson(Object peripheralStatus) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(peripheralStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateOrderId() {
        String orderId= "ORD" + UUID.randomUUID().toString();
        SystemConfig.setLastOrderId(orderId);
        Logger.debug("Generated Order id {}",orderId);
        return orderId;
    }


    public int countRunningThreads() {
        int runningThreadCount = 0;
        Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();
        for (Thread thread : allThreads.keySet()) {
            if (thread.getState() == Thread.State.RUNNABLE) {
                runningThreadCount++;
            }
            Logger.info("Thread: " + thread.getName() + " State: " + thread.getState());
        }
        RedisConnectionPool.totalConnections();
        return runningThreadCount;
    }


    public static Object JSONtoObject(String json, Class<?> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T JSONtoObjectAR(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
