package com.amay.tom.enums;

import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tvm.backend.enums.LoggerTag;
import lombok.*;
import org.tinylog.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public class DeviceStatus implements DeviceStatusListener {

    @Getter
    private DeviceOperationMode currentStatus;

    private OperationModeDeciderService operationModeDeciderService;

    private final List<DeviceStatusListener> listeners = new ArrayList<>();

    public DeviceStatus(DeviceOperationMode currentStatus) {
        this.operationModeDeciderService=new OperationModeDeciderService();
        this.currentStatus = currentStatus;
    }

    @Override
    public void onDeviceStatusChanged(int[] deviceStatus) {
        if (deviceStatus[0] == 1) {
            Logger.tag(LoggerTag.APP).debug("Trigger to maintenance");
            notifyListeners(this.operationModeDeciderService.requestAndGetAppliedOperationMode(
                    new OperationMode(OperationModeSource.TRIGGER,DeviceOperationMode.IN_SERVICE)));
        } else {
            notifyListeners(this.operationModeDeciderService.requestAndGetAppliedOperationMode(
                    new OperationMode(OperationModeSource.TRIGGER,DeviceOperationMode.MAINTENANCE)));
        }
    }

    // Listener interface
    public interface DeviceStatusListener {
        void onDeviceStatusChanged(DeviceOperationMode newStatus);
    }

    // Register a listener
    public void addDeviceStatusListener(DeviceStatusListener listener) {
        listeners.add(listener);
    }

    // Unregister a listener
    public void removeDeviceStatusListener(DeviceStatusListener listener) {
        listeners.remove(listener);
    }

    // Notify all listeners of a status change
    private void notifyListeners(DeviceOperationMode newStatus) {
        for (DeviceStatusListener listener : listeners) {
            listener.onDeviceStatusChanged(newStatus);
        }
    }

    // Method to set the device status and notify listeners "FROM SERVER"
    public void setDeviceOperationMode(DeviceOperationMode newStatus) {
        currentStatus = newStatus;
        // NOW WE HAVE TO STORE THE "STATION MODE" IN A CONTAINER AND THAT WILL DECIDE FOR OPERATION MODE BASED ON SOURCE

        notifyListeners(this.operationModeDeciderService.requestAndGetAppliedOperationMode(new OperationMode(OperationModeSource.SERVER,newStatus)));
    }



}

@Data
@ToString
class OperationMode implements Comparable<OperationMode> {
    private OperationModeSource operationModeSource;
    private DeviceOperationMode operationMode;
    private long timestamp; // when it was set (epoch millis)
    public  OperationMode(OperationModeSource operationModeSource,DeviceOperationMode deviceOperationMode){
        this.operationMode=deviceOperationMode;
        this.operationModeSource=operationModeSource;
        this.timestamp= Instant.now().toEpochMilli();
    }

    @Override
    public int compareTo(OperationMode other) {
        // First compare by timestamp
        int cmp = Long.compare(this.timestamp, other.timestamp);
        if (cmp != 0) return cmp;

        // If timestamps are equal, break ties with source to avoid duplicates
        return this.operationModeSource.compareTo(other.operationModeSource);
    }
}



class OperationModeDeciderService{
    private TreeSet<OperationMode> operationModes;

    public OperationModeDeciderService(){
        operationModes=new TreeSet<>();
    }
    public DeviceOperationMode requestAndGetAppliedOperationMode(OperationMode operationMode){
        Logger.tag(LoggerTag.APP).debug("Requested Operation Mode is : {}",operationMode);

        operationModes.forEach(x-> Logger.tag(LoggerTag.APP).debug(x));

            if(DeviceOperationMode.IN_SERVICE.equals(operationMode.getOperationMode())){
                popOperationMode(operationMode.getOperationModeSource());
            }else {
                this.operationModes.add(operationMode);
            }

        Optional<OperationMode> currentMode=this.operationModes.stream().filter(x->
                DeviceOperationMode.MAINTENANCE.equals(operationMode.getOperationMode())).findAny();

            if(currentMode.isPresent()){
                return currentMode.get().getOperationMode();
            }

        DeviceOperationMode toApply= operationModes.isEmpty() ?DeviceOperationMode.IN_SERVICE:operationModes.last().getOperationMode();
        Logger.tag(LoggerTag.APP).debug("To Apply Mode is : {}",toApply);
        return toApply;
    }

    private void popOperationMode(OperationModeSource operationModeSource) {
        operationModes.removeIf(x->x.getOperationModeSource().equals(operationModeSource));
    }
}

