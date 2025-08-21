package com.amay.tom.model.siftdata;




public class ShiftHeader {

    private static ShiftHeader Instance = new ShiftHeader();

    private ShiftHeader(){

    }

    public static ShiftHeader getInstance(){
        return Instance;
    }
    private String stationName;
    private String equipmentId;
    private String operatorId;
    private int shiftId;
    private String shiftStart;
    private String shiftEnd;

    public void setInstance(String stationName, String equipmentId, String operatorId, int shiftId, String shiftStart) {
        this.stationName = stationName;
        this.equipmentId = equipmentId;
        this.operatorId = operatorId;
        this.shiftId = shiftId;
        this.shiftStart = shiftStart;
//        this.shiftEnd = shiftEnd;
    }

    // Getters and Setters
    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(String shiftStart) {
        this.shiftStart = shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(String shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    @Override
    public String toString(){
        return "Station Name: "+stationName+", Equipment ID: "+equipmentId+", Operator ID: "+operatorId+", Shift ID: "+shiftId+", Shift Start: "+shiftStart+", Shift End: "+shiftEnd;
    }
}