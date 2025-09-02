package com.amay.tom.repository.Replacement;

import com.amay.tom.model.replacement.Replacement;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

public abstract class ReplacementTicketRepository {


    protected static final String TABLE_NAME = "Replacement_ticket";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL1 =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "ticket_number VARCHAR(50) , " +   // getTicketNumber() -> String
                    "amount DOUBLE, " +                           // getAmount() -> Double
                    "shift_id VARCHAR(50), " +                    // getShiftId() -> String
                    "operator_id VARCHAR(50), " +                 // getOperatorId() -> String
                    "device_id VARCHAR(50), " +                   // getDeviceId() -> String
                    "creation_date_time TIMESTAMP, " +            // getCreationDateTime() -> LocalDateTime
                    "update_date_time TIMESTAMP, " +              // getUpdateDateTime() -> LocalDateTime
                    "ticket_type VARCHAR(20)" +                   // getTicketType() -> Enum.toString()
                    ")";

    // Insert statement (matches column order)
    protected static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (" +
            "ticket_number, " +
            "amount, " +
            "shift_id, " +
            "operator_id, " +
            "device_id, " +
            "creation_date_time, " +
            "update_date_time, " +
            "ticket_type" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


    // Select by ticket_number (primary key)
    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE ticket_number = ?";

    // Select all by shift_id
    protected static final String SELECT_EOS_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE shift_id = ?";

    // Update by ticket_number
    protected static final String UPDATE_SQL =
            "UPDATE " + TABLE_NAME + " SET amount = ?, shift_id = ?, operator_id = ?, device_id = ?, creation_date_time = ?, update_date_time = ? " +
                    "WHERE ticket_number = ?";

    // Delete by ticket_number
    protected static final String DELETE_BY_ID_SQL =
            "DELETE FROM " + TABLE_NAME + " WHERE ticket_number = ?";

    protected static  final String SELECT_ALL_Replacement_TICKETS_FROM_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE creation_date_time >= ?";


    public abstract void insert(Replacement replacement) throws RuntimeException;

    public abstract Replacement findByTicketNumber(String ticketNumber);
    public abstract List<Replacement> findByShiftId(String shiftId);

    public abstract List<Replacement> findAllQRTicketsFrom(Timestamp from);
}
