package com.amay.tom.service.qrDataGenerator.impl;

import com.amay.tom.exceptions.TicketNotGenerated;
import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.repository.StationData;
import com.amay.tom.repository.TicketTypeData;
import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
import com.amay.tom.utils.encription.Base64Encoding;
import com.amay.tom.utils.time.TimeUtil;
import org.tinylog.Logger;

public class ImplQRDataGenerator implements QRDataGenerator {

    private final String delimiter =":";


/*    @Override
    public String getQRDataByTicket(MetroTicket metroTicket) {
        if(metroTicket.getTicketId()==null || metroTicket.getTicketId().isEmpty()){
            throw new TicketNotGenerated("Ticket ID is not generated for the ticket "+(metroTicket.toString()));
        }

        String sum=metroTicket.getIssueDay();
        String HM=metroTicket.getIssueHM();
        String source=metroTicket.getSource();
        String destination=metroTicket.getDestination();
        String FARE=String.format("%04d",metroTicket.getFare());
        String EXP_TIME=String.valueOf("01");



        String qrData=
                metroTicket.getMetroNumber() + delimiter +
                metroTicket.getStationId() + delimiter +
                metroTicket.getEquipmentId() + delimiter +
                metroTicket.getEquipmentSerial() + delimiter +
                source + delimiter +
                destination + delimiter +
                sum + delimiter +
                HM + delimiter +
                EXP_TIME + delimiter +
                FARE + delimiter +
                metroTicket.getTicketType().getTicketTypeId() + delimiter +
                *//*no of passenger*//*String.format("%02d",metroTicket.getTicketQuantity()) + delimiter +
                metroTicket.getTicketId();

        Logger.info("QR Data: {}",qrData);
        return Base64Encoding.encode(qrData);

    }*/


    public String getQRDataByTicketV1(MetroTicket metroTicket) {
        if(metroTicket.getTicketId()==null || metroTicket.getTicketId().isEmpty()){
            throw new TicketNotGenerated("Ticket ID is not generated for the ticket "+(metroTicket.toString()));
        }

        String sum=metroTicket.getIssueDay();
        String HM=metroTicket.getIssueHM();
        String source=metroTicket.getSource();
        String destination=metroTicket.getDestination();
        String FARE=String.format("%04d",metroTicket.getFare());
        String EXP_TIME=String.valueOf("01");

        //9  Parameters are used to generate the QR code

        String qrData=
                metroTicket.getLineNumber()+metroTicket.getStationId().replace("st","")+metroTicket.getEquipmentSerial()+delimiter+  //6 digit
//                "060101" +delimiter+ //6 digit
                        metroTicket.getTicketId()+delimiter+ //6 digitri
                        metroTicket.getIssuanceTime()+delimiter+ //10 digit
                        metroTicket.getValidityTime() + delimiter +//10 digit
                        FARE + delimiter +//4 digit
                        source.replace("st","") + delimiter +//2 digit
                        destination.replace("st","") + delimiter +//2 digit
                        metroTicket.getTicketType().getTicketTypeId() + delimiter +//2 digit
                        String.format("%02d",metroTicket.getTicketQuantity()); //2 digit

        Logger.info("QR Data: {}",qrData);
        return Base64Encoding.encode(qrData);
    }


/*    @Override
    public QRTicket getTicketByQRData(String encodedQRData) throws Exception {
        String decodedData= Base64Encoding.decode(encodedQRData);
        Logger.debug("Decoded Data: {}",decodedData);
        String[] parts =  decodedData.split(delimiter);
        QRTicket qrTicket=new QRTicket(parts,10);
        qrTicket.setPrice("₹ "+qrTicket.getPrice());
        return qrTicket;
    }*/

    //Analysis
    @Override
    public QRTicketV2 getTicketV2ByQRData(String encodedQRData) throws Exception {
        String decodedData= Base64Encoding.decode(encodedQRData);
        Logger.debug("Decoded Data: {}",decodedData);
        String[] parts =  decodedData.split(delimiter);
        return this.getqRTicketV2(parts,10);
    }

    private QRTicketV2 getqRTicketV2(String[] qrData,int parameters) throws Exception {
        QRTicketV2 qrTicketV2=new QRTicketV2();
        try {
            String OperatorId = String.valueOf(qrData[0]);
            String  TicketId = qrData[1];
            String  IssueTime = qrData[2];
            String  ValidityTime = qrData[3];
            String  Fare = qrData[4];
            String  Source = String.valueOf(qrData[5]).length()!=2?"0"+String.valueOf(qrData[5]):String.valueOf(qrData[5]);
            String  Destination = String.valueOf(qrData[6]).length()!=2?"0"+String.valueOf(qrData[6]):String.valueOf(qrData[6]);
            String  TicketType = qrData[7];
            String  TicketQuantity = qrData[8];

            qrTicketV2.setTicketId(TicketId);
            qrTicketV2.setOperatorId(OperatorId);
            qrTicketV2.setIssueAt(Long.parseLong(IssueTime));
            qrTicketV2.setValidUntil(Long.parseLong(ValidityTime));
            qrTicketV2.setInStation(StationData.getInstance().getStation(Source));
            qrTicketV2.setOutStation(StationData.getInstance().getStation(Destination));
            qrTicketV2.setTicketType(TicketTypeData.getInstance().getTicketType(TicketType));
            qrTicketV2.setAmount(Integer.parseInt(Fare));
            qrTicketV2.setQuantity(Integer.parseInt(TicketQuantity));

        }catch (Exception e){
            throw new TicketNotGenerated("QR is not valid for QRTicketV2");
        }
        return qrTicketV2;

    }

    @Override
    public QRTicket getTgTicketByQRData(String encodedTgQRData) throws Exception {
        String decodedData= Base64Encoding.decode(encodedTgQRData);
        Logger.debug("Decoded Data: {}",decodedData);
        String[] parts =  decodedData.split(delimiter);
        return new QRTicket(parts,10);
    }

/*
    @Override
    public String getQRDataByTicketV1(
            String ticketId, String issueDay, String issueHM, String source, String destination,
            int fare, String lineNumber, String stationId, String equipmentSerial,
            String issuanceTime, String validityTime, int ticketTypeId, int ticketQuantity) {

        if(ticketId == null || ticketId.isEmpty()) {
            throw new TicketNotGenerated("Ticket ID is not generated for the ticket.");
        }

        String fareFormatted = String.format("%04d", fare);
        String sourceFormatted = source.replace("st", "");
        String destinationFormatted = destination.replace("st", "");

        // 9 Parameters are used to generate the QR code
        String qrData = lineNumber + stationId.replace("st", "") + equipmentSerial + delimiter + // 6 digit
                ticketId + delimiter + // 6 digit
                issuanceTime + delimiter + // 10 digit
                validityTime + delimiter + // 10 digit
                fareFormatted + delimiter + // 4 digit
                sourceFormatted + delimiter + // 2 digit
                destinationFormatted + delimiter + // 2 digit
                String.format("%02d", ticketTypeId) + delimiter + // 2 digit
                String.format("%02d", ticketQuantity); // 2 digit

        Logger.info("QR Data: {}", qrData);
        return Base64Encoding.encode(qrData);
    }

    public MetroTicket decodeQRDataToTicket(String encodedQRData) {
        // Decode the Base64 string
        String decodedQRData = Base64Encoding.decode(encodedQRData);
        Logger.info("Decoded QR Data: {}", decodedQRData);

        // Split the decoded string by the delimiter
        String[] qrData = decodedQRData.split(this.delimiter);

        // Ensure that the number of fields matches the expected number of fields
//        if (data.length != 9) {
//            throw new IllegalArgumentException("Invalid QR data format.");
//        }
        String OperatorId = String.valueOf(qrData[0]);
        String  TicketId = String.valueOf(qrData[1]);
        long  IssueTime = Long.parseLong(qrData[2]);
        long  ValidityTime = Long.parseLong(qrData[3]);
        String  Fare = String.valueOf(qrData[4]);
        String  Source = String.valueOf(qrData[5]).length()!=2?"0"+String.valueOf(qrData[5]):String.valueOf(qrData[5]);
        String  Destination = String.valueOf(qrData[6]).length()!=2?"0"+String.valueOf(qrData[6]):String.valueOf(qrData[6]);
        String  TicketType = String.valueOf(qrData[7]);
        String  TicketQuantity = String.valueOf(qrData[8]);

        // Create a new MetroTicket object with the decoded data
        MetroTicket metroTicket = new MetroTicket(""+qrData[0].charAt(0)+qrData[0].charAt(1),StationData.getInstance().getStation("st"+Source),IssueTime,ValidityTime,StationData.getInstance().getStation("st"+Source),StationData.getInstance().getStation( "st"+Destination),TicketId, Integer.parseInt(Fare), TicketTypeData.getInstance().getTicketType(TicketType), Integer.parseInt(TicketQuantity));
        return metroTicket;
    }
*/

}
