package com.amay.tom.enums;


import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.pdu.controller.command.PassAgentCommand;
import lombok.Getter;

// Enum to represent the different device operation modes
@Getter
public enum DeviceOperationMode {
    IN_SERVICE(1, "In Service", "green") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {
            // check login performed in last 30 sec
            // peripheral status

        }
    },
    PAUSE(2, "Pause", "red") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    FAULTY(3, "Faulty", "red") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    TEST(4, "Test", "blue") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    EMERGENCY(5, "Emergency", "red") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    OUT_OF_SERVICE(6, "Out Of Service", "red") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    NORMAL(7, "Normal", "green") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    STATION_CLOSE(8, "Station Closed", "red") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    MAINTENANCE(9, "Maintenance", "yellow") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    SHIFT_NOT_ACTIVE(10, "Shift Not Active", "yellow") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    },
    POWER_CUT(10, "On Inverter", "yellow") {
        @Override
        public void performAction(EquipmentPrivilege equipmentPrivilege) {

        }
        @Override
        public void performAction() {

        }
    };

    private final int deviceStatusId;
    private final String deviceStatusName;
    private final String color;
    public abstract void performAction(EquipmentPrivilege equipmentPrivilege);
    public abstract void performAction();


    DeviceOperationMode(int deviceStatusId, String deviceStatusName, String color) {
        this.deviceStatusId = deviceStatusId;
        this.deviceStatusName = deviceStatusName;
        this.color = color;
    }
    public DeviceOperationMode getDeviceStatus(int deviceStatusId) {
        for (DeviceOperationMode deviceStatus : DeviceOperationMode.values()) {
            if (deviceStatus.getDeviceStatusId() == deviceStatusId) {
//                setDeviceStatus(deviceStatus);  // Notify listeners
                return deviceStatus;
            }
        }
        return null;
    }

    public DeviceOperationMode getDeviceStatus(String deviceStatusName) {
        for (DeviceOperationMode deviceStatus : DeviceOperationMode.values()) {
            if (deviceStatus.getDeviceStatusName().equalsIgnoreCase(deviceStatusName)) {
//                setDeviceStatus(deviceStatus);  // Notify listeners
                return deviceStatus;
            }
        }
        return null;
    }
}