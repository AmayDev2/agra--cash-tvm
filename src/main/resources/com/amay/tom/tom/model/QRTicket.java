package com.amay.tom.model;

import com.amay.tom.exceptions.DecodingBase64Exception;
import com.amay.tom.exceptions.TicketNotGenerated;
import com.amay.tom.repository.StationData;
import com.amay.tom.repository.TicketTypeData;
import com.amay.tom.utils.encription.Base64Encoding;
import com.amay.tom.utils.time.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.*;

import java.io.Serializable;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class QRTicket implements Serializable {


    private String ticketNo;
    private String initiateDateTime;
    private String expiryTime;
    private String from;
    private String to;
    private String type;
    private String fareMode;
    private String price;
    private int Qty;
    @JsonIgnore
    private String orderID;



    @JsonIgnore
    private Image qrCode;
    private String qrCodeData;

    public QRTicket(String ticketNo, String initiateDateTime, String expiryTime, String from, String to, String type, String fareMode, String price, Image qrCode) {
        this.ticketNo = ticketNo;
        this.initiateDateTime = initiateDateTime;
        this.expiryTime = expiryTime;
        this.from = from;
        this.to = to;
        this.type = type;
        this.fareMode = fareMode;
        this.price = price;
        this.qrCode = qrCode;
    }

    @Deprecated
    public QRTicket(String qrCodeData) throws TicketNotGenerated,DecodingBase64Exception {
        String encodedString=Base64Encoding.decode(qrCodeData);
        try {
            String metroNumber = encodedString.substring(0, 2); // Assuming metro number is 2 characters long
            String source = encodedString.substring(2, 6); // Assuming initiateDateTime is 10 characters long
            String equipmentId = encodedString.substring(6, 10);
            String equipmentSerial = encodedString.substring(10, 15);
            String currentStation = encodedString.substring(15, 19);
            String destination = encodedString.substring(19, 23);
            String issueTime = TimeUtil.getTime(encodedString.substring(23, 29), encodedString.substring(29, 33));
            String fare = encodedString.substring(35, 39);
            TicketType ticketType = TicketTypeData.getInstance().getTicketType(encodedString.substring(39, 40));
            String ticketTypeName = ticketType.getTicketTypeName();
            String expireTime = TimeUtil.addHours(issueTime, ticketType == TicketType.RETURN ? 24 : 1);
            String ticketQuantity = encodedString.substring(40, 42);

            String ticketNo = encodedString.substring(42);

            this.ticketNo = ticketNo;
            this.initiateDateTime = issueTime;
            this.expiryTime = expireTime;
            this.from = StationData.getInstance().getStation(source).getStationName();
            this.to = StationData.getInstance().getStation(destination).getStationName();
            this.type = ticketTypeName;
            this.fareMode = "Cash";
            this.price = "₹ " + (Integer.parseInt(fare));
            this.Qty = Integer.parseInt(ticketQuantity);
        }catch (Exception e){
            throw new TicketNotGenerated("QR is not valid");
        }

    }

    @Deprecated
    public QRTicket(String[] qrData) throws Exception {
        try {
            String metroNumber = String.valueOf(qrData[0]);
            String source = String.valueOf(qrData[1]);
            String equipmentId = String.valueOf(qrData[2]);
            String equipmentSerial = String.valueOf(qrData[3]);
            String currentStation = String.valueOf(qrData[4]);
            String destination = String.valueOf(qrData[5]);
            String issueTime = TimeUtil.getTime(String.valueOf(qrData[6]), String.valueOf(qrData[7]));
            String validTill = String.valueOf(qrData[8]);
            String fare = String.valueOf(qrData[9]);
            TicketType ticketType = TicketTypeData.getInstance().getTicketType(String.valueOf(qrData[10]));
            String ticketTypeName = ticketType.getTicketTypeName();
            String expireTime = TimeUtil.addHours(issueTime, ticketType == TicketType.RETURN ? 24 : ticketType == TicketType.FREE ? (1/4) : 1);
            String ticketQuantity = String.valueOf(qrData[11]);

            String ticketNo = String.valueOf(qrData[12]);

            this.ticketNo = ticketNo;
            this.initiateDateTime = issueTime;
            this.expiryTime = expireTime;
            this.from = StationData.getInstance().getStation(source).getStationName();
            this.to = StationData.getInstance().getStation(destination).getStationName();
            this.type = ticketTypeName;
            this.fareMode = "Cash";
            this.price = "₹ " + (Integer.parseInt(fare));
            this.Qty = Integer.parseInt(ticketQuantity);
        }catch (Exception e){
            throw new TicketNotGenerated("QR is not valid");
        }

    }

    public QRTicket(String[] qrData,int parameters) throws Exception {
        try {
            String OperatorId = String.valueOf(qrData[0]);
            String  TicketId = String.valueOf(qrData[1]);
            String  IssueTime = String.valueOf(qrData[2]);
            String  ValidityTime = String.valueOf(qrData[3]);
            String  Fare = String.valueOf(qrData[4]);
            String  Source = String.valueOf(qrData[5]).length()!=2?"0"+String.valueOf(qrData[5]):String.valueOf(qrData[5]);
            String  Destination = String.valueOf(qrData[6]).length()!=2?"0"+String.valueOf(qrData[6]):String.valueOf(qrData[6]);
            String  TicketType = String.valueOf(qrData[7]);
            String  TicketQuantity = String.valueOf(qrData[8]);

            this.ticketNo = TicketId;
            this.initiateDateTime = TimeUtil.epochMilliToFormattedSystemTime(IssueTime,null);
            this.expiryTime = TimeUtil.epochMilliToFormattedSystemTime(ValidityTime,null);
            this.from = StationData.getInstance().getStation(Source).getStationName();
            this.to = StationData.getInstance().getStation(Destination).getStationName();
            this.type = TicketTypeData.getInstance().getTicketType(TicketType).getTicketTypeName();
//            this.fareMode = "Cash";
            this.price = String.valueOf(Integer.parseInt(Fare));
            this.Qty = Integer.parseInt(TicketQuantity);

        }catch (Exception e){
            throw new TicketNotGenerated("QR is not valid");
        }

    }


    public QRTicket setQrCode(Image qrCode) {
        this.qrCode = qrCode;
        return this;
    }

    public void setOrderId(String orderId) {
        this.orderID = orderId;
    }



//    @Override
//    public String toString() {
//        return "QRTicket{" +
//                "ticketNo='" + ticketNo + '\'' +
//                ", initiateDateTime='" + initiateDateTime + '\'' +
//                ", expiryTime='" + expiryTime + '\'' +
//                ", from='" + from + '\'' +
//                ", to='" + to + '\'' +
//                ", type='" + type + '\'' +
//                ", fareMode='" + fareMode + '\'' +
//                ", price='" + price + '\'' +
//                ", qrCode=" + qrCode +
//                ", qrCode=" + qrCode +
//                '}';
//    }


}
