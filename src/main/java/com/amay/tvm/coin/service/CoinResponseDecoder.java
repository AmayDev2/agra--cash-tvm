package com.amay.tvm.coin.service;



import java.util.Arrays;

public class CoinResponseDecoder {

    public static class DispenseResult {
        public boolean success;
        public int hopperId;
        public int amountDispensed;
        public int errorCode;
        public String message;

        public DispenseResult(boolean success, int hopperId, int amountDispensed, int errorCode, String message) {
            this.success = success;
            this.hopperId = hopperId;
            this.amountDispensed = amountDispensed;
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
            int dispensedAmount = 0;
            int errorCode = 0;

            // Based on previous responses, typical data format:
            // [COM State][Hopper][Amount dispensed][Error code]
            if (dataBytes.length >= 4) {
                comState = dataBytes[0];
                hopperId = (byte) (dataBytes[1] & 0xFF);
                dispensedAmount = dataBytes[2] & 0xFF;
                errorCode = dataBytes[3] & 0xFF;
            }

            boolean success = (comState == 0x00);
            String message = success ? "Dispense successful" : "Dispense failed, error code: " + errorCode;

            return new DispenseResult(success, hopperId, dispensedAmount, errorCode, message);

        } catch (Exception e) {
            return new DispenseResult(false, -1, 0, -1, "Exception during parsing: " + e.getMessage());
        }
    }


}
