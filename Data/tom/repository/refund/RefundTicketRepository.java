package com.amay.tom.repository.refund;

import com.amay.tom.model.QRTicket;
import com.amay.tom.model.refund.Refund;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

public abstract class RefundTicketRepository {


    protected static final String TABLE_NAME = "refunded_ticket";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL1 =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "ticket_number VARCHAR(50) PRIMARY KEY, " +
                    "amount DOUBLE, " +
                    "shift_id VARCHAR(50), " +
                    "operator_id VARCHAR(50), " +
                    "refund_mode VARCHAR(50), " +
                    "device_id VARCHAR(50), " +
                    "creation_date_time TIMESTAMP, " +
                    "update_date_time TIMESTAMP" +
                    ")";

    // Insert statement (matches column order)
    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (ticket_number, amount, shift_id, operator_id, refund_mode, device_id, creation_date_time, update_date_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    // Select by ticket_number (primary key)
    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE ticket_number = ?";

    // Select all by shift_id
    protected static final String SELECT_EOS_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE shift_id = ?";

    // Update by ticket_number
    protected static final String UPDATE_SQL =
            "UPDATE " + TABLE_NAME + " SET amount = ?, shift_id = ?, operator_id = ?, refund_mode = ?, device_id = ?, creation_date_time = ?, update_date_time = ? " +
                    "WHERE ticket_number = ?";

    // Delete by ticket_number
    protected static final String DELETE_BY_ID_SQL =
            "DELETE FROM " + TABLE_NAME + " WHERE ticket_number = ?";

    protected static  final String SELECT_ALL_REFUNDED_TICKETS_FROM_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE creation_date_time >= ?";


    public abstract void insert(Refund refund);

    public abstract Refund findByTicketNumber(String ticketNumber);
    public abstract List<Refund> findByShiftId(String shiftId);

    public abstract List<Refund> findAllQRTicketsFrom(Timestamp from);
}
