package com.amay.tom.repository.product;

import com.amay.tom.model.product.ProductEntity;

import java.sql.Connection;
import java.util.List;

public abstract class ProductRepository {
    protected static final String TABLE_NAME = "Products";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "productVersion VARCHAR(50), " +
                    "productid VARCHAR(50) UNIQUE, " +
                    "productName VARCHAR(100), " +
                    "fareMediaType VARCHAR(50), " +
                    "productType VARCHAR(50), " +
                    "tripCount INT, " +
                    "administrationFee DOUBLE, " +
                    "overtravelCharges DOUBLE, " +
                    "maxStaySameStation INT, " +
                    "maxStayOtherStation INT, " +
                    "tailgatingCharges DOUBLE, " +
                    "overstayChargesPerHour DOUBLE, " +
                    "maxOverstayCharges DOUBLE, " +
                    "ticketlessCharges DOUBLE, " +
                    "adjustmentAfterFirstEntry INT, " +
                    "maxTicket INT, " +
                    "minTicket INT, " +
                    "entryAfterSale INT, " +
                    "refundAfterSale INT, " +
                    "entryCount INT, " +
                    "exitCount INT, " +
                    "maxAdjustmentLimit INT, " +
                    "createdBy VARCHAR(50), " +
                    "updatedBy VARCHAR(50), " +
                    "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "active BOOLEAN" +
                    ")";

    // Insert statement
    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (" +
                    "productVersion, productid, productName, fareMediaType, productType, tripCount, administrationFee, " +
                    "overtravelCharges, maxStaySameStation, maxStayOtherStation, tailgatingCharges, overstayChargesPerHour, " +
                    "maxOverstayCharges, ticketlessCharges, adjustmentAfterFirstEntry, maxTicket, minTicket, entryAfterSale, " +
                    "refundAfterSale, entryCount, exitCount, maxAdjustmentLimit, createdBy, updatedBy, active" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected static final String UPSERT_SQL =
            "MERGE INTO products (" +
                    "product_id, product_version, product_name, fare_media_type, product_type, trip_count, " +
                    "administration_fee, overtravel_charges, max_stay_same_station, max_stay_other_station, " +
                    "tailgating_charges, overstay_charges_per_hour, max_overstay_charges, ticketless_charges, " +
                    "adjustment_after_first_entry, max_ticket, min_ticket, entry_after_sale, refund_after_sale, " +
                    "entry_count, exit_count, max_adjustment_limit, created_by, updated_by, created_at, updated_at, active" +
                    ") KEY (product_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    protected static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    // Select by productid
    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE productid = ?";

    protected static final String DELETE_BY_ID_SQL =
            "DELETE FROM " + TABLE_NAME + " WHERE productid = ?";

    protected static final String DELETE_ALL_SQL =
            "DELETE FROM " + TABLE_NAME;


    public abstract void insert(ProductEntity productEntity) throws RuntimeException;

    public abstract void insertOrUpdateProduct(ProductEntity product);

    public abstract List<ProductEntity> findAll() throws RuntimeException;

    public abstract ProductEntity findByProductId(String productId);

    public abstract void deleteByProductId(String productId) throws RuntimeException;

    public abstract void deleteAll();

}
