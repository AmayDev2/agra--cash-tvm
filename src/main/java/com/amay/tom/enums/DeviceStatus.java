package com.amay.tom.enums;

import com.amay.tom.enums.DeviceOperationMode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DeviceStatus {

    @Getter
    private DeviceOperationMode currentStatus;

    private final List<DeviceStatusListener> listeners = new ArrayList<>();

    public DeviceStatus(DeviceOperationMode currentStatus) {
        this.currentStatus = currentStatus;
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

    // Method to set the device status and notify listeners
    public void setDeviceOperationMode(DeviceOperationMode newStatus) {
        currentStatus = newStatus;
        notifyListeners(newStatus);
    }



}

