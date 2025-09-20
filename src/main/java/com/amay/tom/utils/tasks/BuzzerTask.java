package com.amay.tom.utils.tasks;

import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.CoinModuleInterface;
import org.tinylog.Logger;

import java.time.Instant;
import java.util.concurrent.*;

public class BuzzerTask {
    private final ScheduledExecutorService scheduler;
    private final int TIME;
    private ScheduledFuture<?> future;
    private long timeStamp;

    public BuzzerTask(ScheduledExecutorService scheduler,int time){
        this.scheduler=scheduler;
        this.TIME=time;
    }
    // Start the buzzer ON and schedule to turn it OFF after 30s
    public void start() {
        Logger.tag(LoggerTag.APP).debug("Task Start");
        timeStamp= Instant.now().toEpochMilli();
        if (future == null || !Future.State.RUNNING.equals(future.state()))
            future = scheduler.schedule(CoinModuleInterface.INSTANCE::turnOnBuzzer,
                TIME, TimeUnit.SECONDS);
    }

    // Cancel any scheduled operation
    public void cancelScheduled() {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

    // Force turn ON immediately
    public void turnOnImmediately() {
        CoinModuleInterface.INSTANCE.turnOnBuzzer();
        cancelScheduled();
    }

    public void cancelAndTurnOff(){
        cancelScheduled();
        CoinModuleInterface.INSTANCE.turnOffBuzzer();
    }
    // Shutdown scheduler completely
    public void closeScheduler() {
        Logger.tag(LoggerTag.APP).debug("Task Canceled");
        cancelScheduled();
        scheduler.shutdownNow();
    }
}
