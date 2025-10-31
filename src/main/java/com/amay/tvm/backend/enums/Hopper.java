package com.amay.tvm.backend.enums;

import java.util.Arrays;

public enum Hopper{
    HOPPER1(1,5),
    HOPPER2(2,10),
    HOPPER3(3,10);

    private int unitAmount;
    private int hopperId;

    Hopper(int unitAmount,int hopperId) {
        this.unitAmount=unitAmount;
    }

    public int getUnitAmount() {
        return unitAmount;
    }

    public int getHopperId(){
        return hopperId;
    }

    public Hopper getHopperByHopperId(int hopperId){
        return Arrays.stream(Hopper.values()).filter(x->x.getHopperId()==hopperId).findFirst().orElseThrow();
    }

    public void setHopperUnitAmount(int unitAmount){

    }
}