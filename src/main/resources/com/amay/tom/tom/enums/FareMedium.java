package com.amay.tom.enums;

import com.amay.tom.controller.Controller;

public enum FareMedium {
  QR(1, "QR", 0, 0),
    NCMC(2, "NCMC", 0, 0),
  IMPREST_MONEY(3,"IMPREST_MONEY",0,0);

    private final int fareMediumId;
    private final String fareMediumName;
    private int fareMediumTotal;
    private int fareMediumSale;

    FareMedium(int fareMediumId, String fareMediumName, int fareMediumTotal, int fareMediumSale) {
        this.fareMediumId = fareMediumId;
        this.fareMediumName = fareMediumName;
        this.fareMediumTotal = fareMediumTotal;
        this.fareMediumSale = fareMediumSale;
    }

    public int getFareMediumId() {
        return fareMediumId;
    }

    public String getFareMediumName() {
        return fareMediumName;
    }

    public int getFareMediumTotal() {  //total
        return fareMediumTotal;
    }

    public int getFareMediumSale() {
        return fareMediumSale;
    }


    public void setFareMediumTotal(int i) {
        this.fareMediumTotal = i;
    }

    public void addFareMediumTotal(int i) {
        if(this.fareMediumTotal + i < 10000) this.fareMediumTotal += i;
    }

    public void setFareMediumSale(int i) {
        this.fareMediumTotal = i;
    }


    public void incrementQRSaleByOne() {

//        if(getAvailableStock()<=0){
//            throw new RuntimeException("No stock available");
//        }
        this.fareMediumSale++;
        Controller.getController().updateStock();
    }

    public void incrementNCMCSaleByOne() {
        if(getAvailableStock()<=0){
            throw new RuntimeException("No stock available");
        }

        this.fareMediumSale++;
        Controller.getController().updateStock();
    }

    public int getAvailableStock() {
        return this.fareMediumTotal - this.fareMediumSale;
    }


    public void decrementQRSaleByOne() {
        if(this.fareMediumSale<=0){
            throw new RuntimeException("Error in generating QR Code");
        }

        this.fareMediumSale--;
        Controller.getController().updateStock();
    }
}
