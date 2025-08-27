package com.amay.tom.repository.refund;

import com.amay.tom.model.refund.Refund;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

public abstract class RefundTicketRepository {


    protected static final String TABLE_NAME = "refunded_ticket";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL1 =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "ticket_number VARCHAR(50), " +
                    "amount DOUBLE, " +
                    "shift_id VARCHAR(50), " +
                    "operator_id VARCHAR(50), " +
                    "refund_mode VARCHAR(50), " +
                    "device_id VARCHAR(50), " +
                    "ccu BOOLEAN DEFAULT FALSE, " +     // added
                    "scu BOOLEAN DEFAULT FALSE, " +     // added
                    "creation_date_time TIMESTAMP, " +
                    "update_date_time TIMESTAMP, " +
                    "ticket_type VARCHAR(50), " +
                    "refund_id VARCHAR(50) PRIMARY KEY, " +
                    "status VARCHAR(10) DEFAULT 'PENDING'" +
                    ");";

    protected static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME +
                    " (ticket_number, amount, shift_id, operator_id, refund_mode, device_id, creation_date_time, update_date_time, ticket_type, refund_id,status,ccu,scu) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // Select by ticket_number (primary key)
    protected static final String SELECT_BY_ID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE ticket_number = ?";

    // Select all by shift_id
    protected static final String SELECT_EOS_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE shift_id = ?";

    protected static final String SELECT_EOS_VALID_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE shift_id = ? AND STATUS = 'SUCCESS'";

    // Update by ticket_number
    protected static final String UPDATE_SQL =
            "UPDATE " + TABLE_NAME + " SET amount = ?, shift_id = ?, operator_id = ?, refund_mode = ?, device_id = ?, creation_date_time = ?, update_date_time = ?, refund_id= ? status = ? " +
                    "WHERE ticket_number = ?";

    protected static final String UPDATE_STATUS_SQL =
            "UPDATE "+TABLE_NAME+" SET status = ? WHERE refund_id = ?";

    // Delete by ticket_number
    protected static final String DELETE_BY_ID_SQL =
            "DELETE FROM " + TABLE_NAME + " WHERE ticket_number = ?";

    protected static  final String SELECT_ALL_REFUNDED_TICKETS_FROM_SQL =
            "SELECT * FROM " + TABLE_NAME + " WHERE creation_date_time >= ?";


    public abstract void insert(Refund refund) throws RuntimeException;

    public abstract Refund findByTicketNumber(String ticketNumber);

    public abstract void updateRefundStatus(String refundId, String status);

    public abstract List<Refund> findByShiftId(String shiftId);

    public abstract List<Refund> findAllQRTicketsFrom(Timestamp from);

    public abstract void pushRefunds(List<String> refundIds, String column);

    public abstract List<Refund> findNotPushedTicket(String chanal);
}
