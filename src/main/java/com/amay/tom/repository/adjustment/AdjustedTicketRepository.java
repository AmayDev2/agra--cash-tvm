package com.amay.tom.repository.adjustment;

import com.amay.tom.model.QRTicket;
import com.amay.tom.model.adjust.AdjustedTicketDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public abstract class AdjustedTicketRepository {

    protected static final String TABLE_NAME = "adjusted_ticket";
    protected Connection connection = null;




    protected static final String CREATE_TABLE_SQL1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "orderId VARCHAR(255)," +
            "adjustId SERIAL PRIMARY KEY," +
            "adjustmentType VARCHAR(255)," +
            "issueTime VARCHAR(255)," +
            "entryTime VARCHAR(255)," +
            "exitTime VARCHAR(255)," +
            "destination VARCHAR(255)," +
            "ticketNumber VARCHAR(20)," +
            "deviceId VARCHAR(25)," +
            "operatorId VARCHAR(25)," +
            "shiftId VARCHAR(255)," +
            "reason VARCHAR(255)," +
            "area VARCHAR(255)," +
            "paymentMode VARCHAR(255)," +
            "penaltyAmount VARCHAR(255)," +
            "transactionId VARCHAR(255)," +
            "createdAt TIMESTAMP," +
            "updatedAt TIMESTAMP," +
            "transactionTime TIMESTAMP," +
            "encryptedQR VARCHAR(255)" +
            ");";

    protected static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (orderId, adjustId, adjustmentType, issueTime, entryTime, exitTime, destination, ticketNumber, deviceId, operatorId, reason, area, paymentMode, transactionId, createdAt, updatedAt, transactionTime, encryptedQR, penaltyAmount, shiftId) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

    protected static final String SELECT_BY_ID_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE adjustId = ?";
    protected static final String SELECT_EOS_SQL = "SELECT * FROM " + TABLE_NAME +" WHERE shiftId = ?";
    protected static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + " SET orderId=?, adjustmentType=?, issueTime=?, entryTime=?, exitTime=?, destination=?, ticketNumber=?, deviceId=?, operatorId=?, reason=?, area=?, paymentMode=?, transactionId=?, createdAt=?, updatedAt=?, transactionTime=? encryptedQR=? penaltyAmount=? shiftId=? WHERE adjustId=?";
    protected static final String DELETE_BY_ID_SQL = "DELETE FROM " + TABLE_NAME + " WHERE adjustId = ?";
    protected static final String SELECT_ALL_QR_TICKETS_FROM_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE createdAt >= ? ORDER BY createdAt DESC";


    abstract void  createTableIfNotExists() throws SQLException;
    public abstract String save(AdjustedTicketDto adjustedTicket) ;
    public abstract AdjustedTicketDto findById(String adjustId) ;
    abstract void update(AdjustedTicketDto adjustedTicket);
    abstract void deleteById(String adjustId) ;
    public abstract List<AdjustedTicketDto> findForEOS(String shiftId) ;
    public abstract List<AdjustedTicketDto> findAllQRTicketsFrom(Timestamp from);
}
