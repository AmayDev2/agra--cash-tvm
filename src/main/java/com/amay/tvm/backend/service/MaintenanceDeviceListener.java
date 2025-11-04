package com.amay.tvm.backend.service;

import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tvm.backend.model.MaintenanceLog;
import com.amay.tvm.backend.repository.MaintenanceRepository;

import java.time.LocalDateTime;

public class MaintenanceDeviceListener implements DeviceStatusListener {
    private int[] lastDeviceStatus=null;
    private final MaintenanceRepository maintenanceRepository;
//    private final String username;

    public MaintenanceDeviceListener(MaintenanceRepository maintenanceRepository){
        this.maintenanceRepository=maintenanceRepository;
//        this.username = username;
    }
    private final String[]deviceNames={
           "Printer", "SCU", "CCU", "BNR", "PDU", "Cash Drawer", "UPS","OHD","UPOS","Reader"
    };
    @Override
    public void onDeviceStatusChanged(int[] deviceStatus) {
        for(int i=0;i<deviceStatus.length;i++){
            boolean changed=lastDeviceStatus==null || lastDeviceStatus[i]!=deviceStatus[i];
            if(changed){
                String statusStr=deviceStatus[i]==1?"Connected":"Disconnected";
                MaintenanceLog log=new MaintenanceLog(
                        deviceNames[i],
                        statusStr,
                        LocalDateTime.now()
                );
                maintenanceRepository.insert(log);
            }
        }
        lastDeviceStatus=deviceStatus.clone();
    }
}
