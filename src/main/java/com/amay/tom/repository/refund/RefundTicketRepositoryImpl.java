package com.amay.tom.repository.refund;

import com.amay.tom.model.refund.Refund;
import com.amay.tom.repository.tickets.TicketsRepository;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RefundTicketRepositoryImpl extends RefundTicketRepository {

    private final Connection connection;
    private TicketsRepository ticketsRepository;

    public RefundTicketRepositoryImpl(Connection connection, TicketsRepository ticketsRepository)  {
        this.connection = connection;
        this.ticketsRepository=ticketsRepository;
        this.createTableIfNotExists();
    }


    private void createTableIfNotExists() {


        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL1);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("Error creating refunds table: {}", e.getMessage());
        }
    }

    @Override
    public void insert(Refund refund) {

        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, refund.getTicketNumber());
            pstmt.setDouble(2, refund.getAmount());
            pstmt.setString(3, refund.getShiftId());
            pstmt.setString(4, refund.getOperatorId());
            pstmt.setString(5, refund.getRefundMode());
            pstmt.setString(6, refund.getDeviceId());
            pstmt.setTimestamp(7, Timestamp.valueOf(refund.getCreationDateTime()));
            pstmt.setTimestamp(8, Timestamp.valueOf(refund.getUpdateDateTime()));
            pstmt.executeUpdate();
            ticketsRepository.markTicketRefundedByTicketId( refund.getTicketNumber());
        } catch (SQLException e) {
            Logger.error("Error inserting refund: {}", e.getMessage());
        }
    }

    @Override
    public Refund findByTicketNumber(String ticketNumber) {
        
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, ticketNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRefund(rs);
            }
        } catch (SQLException e) {
            Logger.error("Error finding refund by ticket number: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<Refund> findByShiftId(String shiftId) {
        List<Refund> refunds = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_EOS_SQL)) {
            pstmt.setString(1, shiftId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                refunds.add(mapResultSetToRefund(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding refunds by shift ID: {}", e.getMessage());
        }
        return refunds;
    }

    @Override
    public List<Refund> findAllQRTicketsFrom(Timestamp from) {
        List<Refund> refunds = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_REFUNDED_TICKETS_FROM_SQL)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                refunds.add(mapResultSetToRefund(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding all QR tickets from: {}", e.getMessage());
        }
        return refunds;
    }

    private Refund mapResultSetToRefund(ResultSet rs) throws SQLException {
        return new Refund(
            rs.getString("ticket_number"),
            rs.getDouble("amount"),
            rs.getString("shift_id"),
            rs.getString("operator_id"),
            rs.getString("refund_mode"),
            rs.getString("device_id"),
            rs.getTimestamp("creation_date_time").toLocalDateTime(),
            rs.getTimestamp("update_date_time").toLocalDateTime()
        );
    }
} 