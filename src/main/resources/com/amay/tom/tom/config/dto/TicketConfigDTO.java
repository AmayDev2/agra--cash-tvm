package com.amay.tom.config.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketConfigDTO {
    private List<ProductTypeDefDTO> productType = null;
    private int id = 111;
    private String configVer = "v1.2";
    private double administrationFee = 0.0;
    private double overtravelCharges = 10.0;
    private int maxStaySameStation = 20;
    private int maxStayOtherStation = 120;
    private int minGroupTicket = 10;
    private int maxGroupTicket = 30;
    private double tailgatingCharges = 50.0;
    private double overstayCharges = 20.0;
    private double ticketlessCharges = 70.0;
    private int maxRefundTime = 0;
    private int journeyTime = 120;
    private int maxTicket = 5;
    private int entryAfterSale = 0;
    private double maxOverstayCharges = 50.0;

}



