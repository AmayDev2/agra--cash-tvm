package com.amay.tom.model;


public class PeripheralStatus {
    private int printer;
    private int scanner;
    private int CSC;
    private int PDM;

    // Constructor
    public PeripheralStatus(int printer, int scanner, int CSC, int PDM) {
        if (printer < 0 || scanner < 0 || CSC < 0 || PDM < 0) {
            throw new IllegalArgumentException("Invalid peripheral status");
        }
        this.printer = printer;
        this.scanner = scanner;
        this.CSC = CSC;
        this.PDM = PDM;
    }

    // Getters
    public int getPrinter() {
        return printer;
    }

    public int getScanner() {
        return scanner;
    }

    public int getCSC() {
        return CSC;
    }

    public int getPDM() {
        return PDM;
    }


}
