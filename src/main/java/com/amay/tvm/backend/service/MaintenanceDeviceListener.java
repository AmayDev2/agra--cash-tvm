package com.amay.tvm.backend.service;

import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tvm.backend.enums.ConnectionStatus;
import com.amay.tvm.backend.enums.Peripherals;
import com.amay.tvm.backend.model.MaintenanceLog;
import com.amay.tvm.backend.repository.MaintenanceRepository;

import java.time.LocalDateTime;

public class MaintenanceDeviceListener implements DeviceStatusListener {
    private int[] lastDeviceStatus = null;
    private final MaintenanceRepository maintenanceRepository;
//    private final String username;

    public MaintenanceDeviceListener(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
//        this.username = username;
    }

    @Override
    public void onDeviceStatusChanged(int[] deviceStatus) {
        for (int i = 0; i < deviceStatus.length; i++) {
            boolean changed = lastDeviceStatus == null || lastDeviceStatus[i] != deviceStatus[i];
            if (changed) {
                for (Peripherals peripherals : Peripherals.values()) {
                    String statusStr = deviceStatus[peripherals.getIndex()] > 0 ?
                            ConnectionStatus.CONNECTED.name() :
                            ConnectionStatus.DISCONNECTED.name();
                    MaintenanceLog log = new MaintenanceLog(
                            peripherals.name(),
                            statusStr,
                            LocalDateTime.now()
                    );
                    maintenanceRepository.insert(log);
                }
            }
            lastDeviceStatus = deviceStatus.clone();
        }
    }
}
