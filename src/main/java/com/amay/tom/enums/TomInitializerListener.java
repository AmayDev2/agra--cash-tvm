package com.amay.tom.enums;

import javafx.scene.Scene;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TomInitializerListener {

    private final TomSessionListener listener;

    // Listener interface
    public interface TomSessionListener {
        void onDeviceStatusChanged(Scene newStatus);
    }

    // Register a listener
    public TomInitializerListener(TomSessionListener listener) {
        this.listener = listener;
    }

//    // Unregister a listener
//    public void removeTomSessionListener(TomSessionListener listener) {
//        listeners.remove(listener);
//    }

    // Notify all listeners of a status change
    private void notifyListeners(Scene newStatus) {
//        for (TomSessionListener listener : listeners) {
            listener.onDeviceStatusChanged(newStatus);
//        }
    }

    // Method to set the device status and notify listeners
    public void setDeviceOperationMode(Scene newScene) {
        notifyListeners(newScene);
    }



}

