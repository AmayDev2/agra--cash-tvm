package com.amay.tom.repository.refund;

import com.amay.tom.model.refund.Refund;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.tickets.TicketsRepository;
import jakarta.transaction.Transactional;
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
    public void insert(Refund refund) throws RuntimeException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, refund.getTicketNumber());
            pstmt.setDouble(2, refund.getAmount());
            pstmt.setString(3, refund.getShiftId());
            pstmt.setString(4, refund.getOperatorId());
            pstmt.setString(5, refund.getRefundMode());
            pstmt.setString(6, refund.getDeviceId());
            pstmt.setTimestamp(7, Timestamp.valueOf(refund.getCreationDateTime()));
            pstmt.setTimestamp(8, Timestamp.valueOf(refund.getUpdateDateTime()));
            pstmt.setString(9, refund.getTicketType());
            pstmt.setString(10, refund.getRefundId());
            pstmt.setString(11,refund.getStatus());
            pstmt.setBoolean(12, refund.isCcu());
            pstmt.setBoolean(13, refund.isScu());
            pstmt.executeUpdate();
            ticketsRepository.markTicketRefundedByTicketId( refund.getTicketNumber());
        } catch (SQLException e) {
            Logger.error("Error inserting refund: {}", e.getMessage());
            // Handle the exception, e.g., log it or rethrow it
            throw new RuntimeException("Failed to insert refund", e);
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
    public void updateRefundStatus(String refundId, String status) {
        // Validate status to avoid invalid updates
        if (!"SUCCESS".equalsIgnoreCase(status) && !"FAIL".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS_SQL)) {
            pstmt.setString(1, status.toUpperCase()); // ensure consistent values
            pstmt.setString(2, refundId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                Logger.error("No refund found with refund_id = " + refundId);
            }
        } catch (SQLException e) {
            Logger.error("message :"+e);
        }
    }


    @Override
    public List<Refund> findByShiftId(String shiftId) {
        List<Refund> refunds = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_EOS_VALID_SQL)) {
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

    @Override
    @Transactional
    public void pushRefunds(List<String> refundIds, String column) {
        // validate column name (avoid SQL injection)
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }

        String sql = "UPDATE " + TABLE_NAME + " SET " + column + " = true WHERE refund_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String refundId : refundIds) {
                pstmt.setString(1, refundId);
                pstmt.addBatch();
            }
            int[] updated = pstmt.executeBatch();
            Logger.info("Marked {} for {} refunds", column.toUpperCase(), updated.length);
        } catch (SQLException e) {
            Logger.error("Error marking {} refunds: {}", column.toUpperCase(), e.getMessage());
            throw new RuntimeException("Failed to update " + column.toUpperCase() + " refunds", e);
        }
    }

    @Override
    public List<Refund> findNotPushedTicket(String column) {
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }
        column+=" = false";

        String query="SELECT * FROM "+ TABLE_NAME +" WHERE "+ column+" AND STATUS = 'SUCCESS' ORDER BY creation_date_time DESC";

        List<Refund> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement( query)) {
//            pstmt.setString(1, column);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToRefund(rs)); // Add each Refund to the list
            }
        } catch (SQLException e) {
            Logger.debug("Error fetching QR tickets: {}", e.getMessage());

        }

        return list;
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
                rs.getTimestamp("update_date_time").toLocalDateTime(),
                rs.getString("ticket_type"),
                rs.getString("refund_id"),
                rs.getString("status"),
                rs.getBoolean("scu"),   // add SCU
                rs.getBoolean("ccu")
        );
    }
} 