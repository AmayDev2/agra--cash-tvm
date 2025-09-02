package com.amay.tom.model.product;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
// This class represents a Data Transfer Object (DTO) for Product information.
public class ProductEntity {
    private int id;
    private String productVersion;
    private String productid;     // Product ID is a unique identifier for the product.
    private String productName;
    private String fareMediaType;
    private String productType;
    private int tripCount;
    private double administrationFee;
    private double overtravelCharges;
    private int maxStaySameStation;
    private int maxStayOtherStation;
    private double tailgatingCharges;
    private double overstayChargesPerHour;
    private double maxOverstayCharges;
    private double ticketlessCharges;
    private int adjustmentAfterFirstEntry;
    private int maxTicket;
    private int minTicket;
    private int entryAfterSale;
    private int refundAfterSale;
    private int entryCount;
    private int exitCount;
    private int maxAdjustmentLimit;
    private String createdBy;
    private String updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean active;
}
