package com.amay.tom.coin.service;



import lombok.ToString;

import java.util.Arrays;
import java.util.List;

public class CoinResponseDecoder {

    @ToString
    public static class CoinModuleDispenseResponse {
        public boolean success;
        public int amountDispensed;
        public int errorCode;
        public String message;
        public List<DispenseResult> dispenseResult;

        public CoinModuleDispenseResponse(boolean success, int errorCode, String message, List<DispenseResult> dispenseResult) {
            this.dispenseResult = dispenseResult;
            this.success = success;
            this.errorCode = errorCode;
            this.message = message;
        }

//        public void setTotalAmount() {
//            this.amountDispensed = dispenseResult.stream().mapToInt(r -> HoppersRegistry.INSTANCE.getAmount(String.valueOf(r.hopperId))*r.quantityDispensed).sum();
//        }
    }

    public static class DispenseResult {
        public boolean success;
        public int hopperId;
        public int quantityDispensed; //Quantity of coins dispensed
        public int errorCode;
        public String message;

        public DispenseResult(boolean success, int hopperId, int quantityDispensed, int errorCode, String message) {
            this.success = success;
            this.hopperId = hopperId;
            this.quantityDispensed = quantityDispensed;
            this.errorCode = errorCode;
            this.message = message;
        }
    }

    /**
     * Decodes the response frame from dispenseCoin operation.
     * @param dataBytes the raw response byte array from device.
     * @return a structured DispenseResult object with parsed data.
     */
    public static DispenseResult decodeDispenseResponse(byte[] dataBytes) {


        try{
            // Determine success by COM state, typically in data
            // From protocol, success is indicated by comState=0x00
            // Data structure: COM State + Hopper + Dispensed amount + Error code
            byte comState = 0x00;
            byte hopperId = -1;
            int dispensedQuantity = 0;
            int errorCode = 0;

            // Based on previous responses, typical data format:
            // [COM State][Hopper][Amount dispensed][Error code]
            if (dataBytes.length >= 4) {
                comState = dataBytes[0];
                hopperId = (byte) (dataBytes[1] & 0xFF);
                dispensedQuantity = dataBytes[2] & 0xFF;
                errorCode = dataBytes[3] & 0xFF;
            }

            boolean success = (comState == 0x00);
            String message = success ? "Dispense successful" : "Dispense failed, error code: " + errorCode;

            return new DispenseResult(success, hopperId, dispensedQuantity, errorCode, message);

        } catch (Exception e) {
            return new DispenseResult(false, -1, 0, -1, "Exception during parsing: " + e.getMessage());
        }
    }


}
