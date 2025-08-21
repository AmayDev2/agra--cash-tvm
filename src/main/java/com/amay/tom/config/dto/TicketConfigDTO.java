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

    private List<ProductTypeDefDTO> productType;
    private int id;
    private String configVer;
    private double administrationFee;
    private double overtravelCharges;
    private int maxStaySameStation;
    private int maxStayOtherStation;
    private int minGroupTicket;
    private int maxGroupTicket;
    private double tailgatingCharges;
    private double overstayCharges;
    private double ticketlessCharges;
    private int maxRefundTime;
    private int journeyTime;
    private int maxTicket;
    private int entryAfterSale;
    private double maxOverstayCharges;
}



