package com.amay.tvm.backend.enums;

import java.util.Arrays;

public enum Hopper{
    HOPPER1(1,5),
    HOPPER2(2,10),
    HOPPER3(3,10);

    private int hopperId;
    private int unitAmount;


    Hopper(int hopperId,int unitAmount) {
        this.unitAmount=unitAmount;
        this.hopperId = hopperId;
    }

    public int getUnitAmount() {
        return unitAmount;
    }

    public int getHopperId(){
        return hopperId;
    }

    public Hopper getHopperByHopperId(int hopperId){
        return Arrays.stream(Hopper.values()).peek(x->System.out.println("PEEK "+x.hopperId)).filter(x->x.hopperId==hopperId).findFirst().orElseThrow();
    }

    public void setHopperUnitAmount(int unitAmount){

    }
}