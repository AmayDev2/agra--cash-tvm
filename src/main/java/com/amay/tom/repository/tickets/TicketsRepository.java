package com.amay.tom.repository.tickets;


import com.amay.tom.model.QRTicket;
import com.amay.tom.model.tickets.TicketsDto;

import java.sql.*;
import java.util.List;

public abstract class TicketsRepository {

    protected static final String TABLE_NAME = "tickets";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "orderId VARCHAR(255)," +
            "ticketId VARCHAR(255) PRIMARY KEY," +
            "issueAt BIGINT," +
            "entryTime BIGINT," +
            "validUntil BIGINT," +
            "inStation VARCHAR(255)," +
            "outStation VARCHAR(255)," +
            "language VARCHAR(255)," +
            "ticketType VARCHAR(255)," +
            "qrData VARCHAR(255)," +
            "operatorId VARCHAR(255)," +
            "shiftId VARCHAR(255)," +
            "deviceId VARCHAR(255)," +
            "deviceType VARCHAR(255)," +
            "deviceSerial VARCHAR(255)," +
            "lineId VARCHAR(255)," +
            "stationId VARCHAR(255)," +
            "amount DOUBLE," +
            "discount DOUBLE," +
            "paymentMode VARCHAR(255)," +
            "isCanceled BOOLEAN," +
            "isRefund BOOLEAN," +
            "isReplaced BOOLEAN," +
            "isAdjusted BOOLEAN," +
            "isActive BOOLEAN," +
            "softwareVer VARCHAR(255)," +
            "ticketVer VARCHAR(255)," +
            "faretableVer VARCHAR(255)," +
            "quantity INT," +
            "status VARCHAR(255)," +
            "transactionId VARCHAR(255)," +
            "createdAt TIMESTAMP," +
            "updatedAt TIMESTAMP" +
            ");";

    protected static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (orderId, ticketId, issueAt, entryTime, validUntil, inStation, outStation, language, ticketType, qrData, operatorId, shiftId, deviceId, deviceType, deviceSerial, lineId, stationId, amount, discount, paymentMode, isCanceled, isRefund, isReplaced, isAdjusted, isActive, softwareVer, ticketVer, faretableVer, quantity, status, transactionId, createdAt, updatedAt) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected static final String SELECT_BY_ID_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE ticketId = ?";
    protected static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + " SET orderId=?, issueAt=?, entryTime=?, validUntil=?, inStation=?, outStation=?, language=?, ticketType=?, qrData=?, operatorId=?, shiftId=?, deviceId=?, deviceType=?, deviceSerial=?, lineId=?, stationId=?, amount=?, discount=?, paymentMode=?, isCanceled=?, isRefund=?, isReplaced=?, isAdjusted=?, isActive=?, softwareVer=?, ticketVer=?, faretableVer=?, quantity=?, status=?, transactionId=?, createdAt=?, updatedAt=? WHERE ticketId=?";
    protected static final String DELETE_BY_ID_SQL = "DELETE FROM " + TABLE_NAME + " WHERE ticketId = ?";
    protected static final String CANCEL_TICKET_SQL = "UPDATE " + TABLE_NAME +" SET isCanceled = true , isActive = false WHERE orderId = ?";
    protected static final String ADJUSTED_TICKET_SQL = "UPDATE " + TABLE_NAME +" SET isAdjusted = true WHERE ticketId = ?";
    protected static final String REFUNDED_TICKET_SQL = "UPDATE " + TABLE_NAME +" SET isRefund = true , isActive = false  WHERE ticketId = ?";
    protected static final String SELECT_LAST_TICKET_SQL = "SELECT ticketId FROM "+ TABLE_NAME +" ORDER BY ticketId DESC LIMIT 1";
    protected static final String SELECT_TICKET_BY_ORDER_ID_SQL = "SELECT * FROM "+ TABLE_NAME +" WHERE orderId = ?";
    protected static final String SELECT_EOS_SQL = "SELECT * FROM "+ TABLE_NAME+ " WHERE shiftId = ?";
    protected static final String SELECT_QR_TICKETS_FROM_SQL = "SELECT * FROM "+ TABLE_NAME +" WHERE createdAt >= ? ORDER BY createdAt DESC";

    abstract void createTableIfNotExists() throws SQLException;
    public abstract String save(TicketsDto tickets);
    abstract public TicketsDto findById(String ticketId);
    abstract void update(TicketsDto tickets);
    abstract void deleteById(String ticketId);
    abstract public String getLastTicketNumber();
    abstract public void markTicketCancelByOrderId(String orderId);

    public abstract void markTicketRefundedByTicketId(String ticketId);

    abstract public void markTicketAdjustedByTicketId(String ticketId);
    abstract public List<TicketsDto> getTicketByOrderId(String orderId);
    abstract public List<TicketsDto> getTicketByShiftId(String shiftId);
    abstract public List<TicketsDto> findAllQRTicketsFrom(Timestamp timestamp);
}

