package com.amay.tom.repository.tickets;


import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.tickets.TicketsRepository;
import jakarta.transaction.Transactional;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketsRepositoryImpl extends TicketsRepository {



    public TicketsRepositoryImpl(Connection connection) {
        try {
            this.connection = connection;
            this.createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    void createTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String save(TicketsDto tickets) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, tickets.getOrderId());
            pstmt.setString(2, tickets.getTicketId());
            pstmt.setLong(3, tickets.getIssueAt());
            pstmt.setLong(4, tickets.getEntryTime());
            pstmt.setLong(5, tickets.getValidUntil());
            pstmt.setString(6, tickets.getInStation());
            pstmt.setString(7, tickets.getOutStation());
            pstmt.setString(8, tickets.getLanguage());
            pstmt.setString(9, tickets.getTicketType());
            pstmt.setString(10, tickets.getQrData());
            pstmt.setString(11, tickets.getOperatorId());
            pstmt.setString(12, tickets.getShiftId());
            pstmt.setString(13, tickets.getDeviceId());
            pstmt.setString(14, tickets.getDeviceType());
            pstmt.setString(15, tickets.getDeviceSerial());
            pstmt.setString(16, tickets.getLineId());
            pstmt.setString(17, tickets.getStationId());
            pstmt.setDouble(18, tickets.getAmount());
            pstmt.setDouble(19, tickets.getDiscount());
            pstmt.setString(20, tickets.getPaymentMode());
            pstmt.setBoolean(21, tickets.isCanceled());
            pstmt.setBoolean(22, tickets.isRefund());
            pstmt.setBoolean(23, tickets.isReplaced());
            pstmt.setBoolean(24, tickets.isAdjusted());
            pstmt.setBoolean(25, tickets.isActive());
            pstmt.setString(26, tickets.getSoftwareVer());
            pstmt.setString(27, tickets.getTicketVer());
            pstmt.setString(28, tickets.getFaretableVer());
            pstmt.setInt(29, tickets.getQuantity());
            pstmt.setString(30, tickets.getStatus());
            pstmt.setString(31, tickets.getTransactionId());
            pstmt.setTimestamp(32, Timestamp.valueOf(tickets.getCreatedAt()));
            pstmt.setTimestamp(33, Timestamp.valueOf(tickets.getUpdatedAt()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);
                }
            }
        } catch (SQLException e) {
            Logger.debug("Error saving ticket: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public TicketsDto findById(String ticketId) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(TicketsDto tickets) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL)) {
            pstmt.setString(1, tickets.getOrderId());
            pstmt.setLong(2, tickets.getIssueAt());
            pstmt.setLong(3, tickets.getEntryTime());
            pstmt.setLong(4, tickets.getValidUntil());
            pstmt.setString(5, tickets.getInStation());
            pstmt.setString(6, tickets.getOutStation());
            pstmt.setString(7, tickets.getLanguage());
            pstmt.setString(8, tickets.getTicketType());
            pstmt.setString(9, tickets.getQrData());
            pstmt.setString(10, tickets.getOperatorId());
            pstmt.setString(11, tickets.getShiftId());
            pstmt.setString(12, tickets.getDeviceId());
            pstmt.setString(13, tickets.getDeviceType());
            pstmt.setString(14, tickets.getDeviceSerial());
            pstmt.setString(15, tickets.getLineId());
            pstmt.setString(16, tickets.getStationId());
            pstmt.setDouble(17, tickets.getAmount());
            pstmt.setDouble(18, tickets.getDiscount());
            pstmt.setString(19, tickets.getPaymentMode());
            pstmt.setBoolean(20, tickets.isCanceled());
            pstmt.setBoolean(21, tickets.isRefund());
            pstmt.setBoolean(22, tickets.isReplaced());
            pstmt.setBoolean(23, tickets.isAdjusted());
            pstmt.setBoolean(24, tickets.isActive());
            pstmt.setString(25, tickets.getSoftwareVer());
            pstmt.setString(26, tickets.getTicketVer());
            pstmt.setString(27, tickets.getFaretableVer());
            pstmt.setInt(28, tickets.getQuantity());
            pstmt.setString(29, tickets.getStatus());
            pstmt.setString(30, tickets.getTransactionId());
            pstmt.setTimestamp(31, Timestamp.valueOf(tickets.getCreatedAt()));
            pstmt.setTimestamp(32, Timestamp.valueOf(tickets.getUpdatedAt()));
            pstmt.setString(33, tickets.getTicketId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(String ticketId) {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            pstmt.setString(1, ticketId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLastTicketNumber() {

        try (PreparedStatement statement = connection.prepareStatement(SELECT_LAST_TICKET_SQL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("ticketId");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            //System.out.println("Error getting last ticket number: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void markTicketCancelByOrderId(String orderId) {
        try (PreparedStatement statement = connection.prepareStatement(CANCEL_TICKET_SQL)) {
            statement.setString(1, orderId);
            statement.executeUpdate();
            Logger.debug("Ticket marked as cancelled");
        } catch (SQLException e) {
            Logger.error("Error marking ticket as cancelled: {}", e);
        }
    }

    @Override
    public void markTicketRefundedByTicketId(String ticketId) {
        try (PreparedStatement statement = connection.prepareStatement(REFUNDED_TICKET_SQL)) {
            statement.setString(1, ticketId);
            statement.executeUpdate();
            Logger.debug("Ticket marked as cancelled");
        } catch (SQLException e) {
            Logger.error("Error marking ticket as cancelled: {}", e);
        }
    }

    @Override
    public void markTicketAdjustedByTicketId(String ticketId) {
        try (PreparedStatement statement = connection.prepareStatement(ADJUSTED_TICKET_SQL)) {
            statement.setString(1, ticketId);
            statement.executeUpdate();
            Logger.debug("Ticket marked as adjusted");
        } catch (SQLException e) {
            Logger.error("Error marking ticket as adjusted: {}", e);
        }
    }

    @Override
    public List<TicketsDto> getTicketByOrderId(String orderId) {
        List<TicketsDto> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_TICKET_BY_ORDER_ID_SQL)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs)); // Add each TicketsDto to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list; // Return the list of TicketsDto
    }

    @Override
    public List<TicketsDto> getTicketByShiftId(String shiftId) {
        List<TicketsDto> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_EOS_SQL)) {
            pstmt.setString(1, shiftId);
            ResultSet rs = pstmt.executeQuery();
            Logger.debug("QUERY "+pstmt);
            while (rs.next()) {
                list.add(mapRow(rs)); // Add each TicketsDto to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list; // Return the list of TicketsDto
    }

    @Override
    public List<TicketsDto> findAllQRTicketsFrom(Timestamp timestamp) {
        List<TicketsDto> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_QR_TICKETS_FROM_SQL)) {
            pstmt.setTimestamp(1, timestamp);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs)); // Add each TicketsDto to the list
            }
        } catch (SQLException e) {
            Logger.debug("Error fetching QR tickets: {}", e.getMessage());

        }

        return list;
    }

    @Override
    public List<TicketsDto> findNotPushedTicket(String column) {
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }
        column+=" = false";

        String query="SELECT * FROM "+ TABLE_NAME +" WHERE "+ column+" ORDER BY createdAt DESC";

        List<TicketsDto> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement( query)) {
//            pstmt.setString(1, column);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs)); // Add each TicketsDto to the list
            }
        } catch (SQLException e) {
            Logger.debug("Error fetching QR tickets: {}", e.getMessage());

        }

        return list;
    }


    @Override
    @Transactional
    public void pushTickets(List<String> ticketIds, String column) {
        // validate column name to prevent SQL injection
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }

        String sql = "UPDATE " + TABLE_NAME + " SET " + column + " = true WHERE ticketId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String id : ticketIds) {
                pstmt.setString(1, id);
                pstmt.addBatch();
            }
            int[] updated = pstmt.executeBatch();
            Logger.info("Marked {} for {} tickets", column.toUpperCase(), updated.length);
        } catch (SQLException e) {
            Logger.error("Error marking {} for tickets: {}", column.toUpperCase(), e.getMessage());
            throw new RuntimeException("Failed to mark " + column.toUpperCase() + " for tickets", e);
        }
    }



    private TicketsDto mapRow(ResultSet rs) throws SQLException {
        TicketsDto ticket = new TicketsDto();
        ticket.setOrderId(rs.getString("orderId"));
        ticket.setTicketId(rs.getString("ticketId"));
        ticket.setIssueAt(rs.getLong("issueAt"));
        ticket.setEntryTime(rs.getLong("entryTime"));
        ticket.setValidUntil(rs.getLong("validUntil"));
        ticket.setInStation(rs.getString("inStation"));
        ticket.setOutStation(rs.getString("outStation"));
        ticket.setLanguage(rs.getString("language"));
        ticket.setTicketType(rs.getString("ticketType"));
        ticket.setQrData(rs.getString("qrData"));
        ticket.setOperatorId(rs.getString("operatorId"));
        ticket.setShiftId(rs.getString("shiftId"));
        ticket.setDeviceId(rs.getString("deviceId"));
        ticket.setDeviceType(rs.getString("deviceType"));
        ticket.setDeviceSerial(rs.getString("deviceSerial"));
        ticket.setLineId(rs.getString("lineId"));
        ticket.setStationId(rs.getString("stationId"));
        ticket.setAmount(rs.getDouble("amount"));
        ticket.setDiscount(rs.getDouble("discount"));
        ticket.setPaymentMode(rs.getString("paymentMode"));
        ticket.setCanceled(rs.getBoolean("isCanceled"));
        ticket.setRefund(rs.getBoolean("isRefund"));
        ticket.setReplaced(rs.getBoolean("isReplaced"));
        ticket.setAdjusted(rs.getBoolean("isAdjusted"));
        ticket.setActive(rs.getBoolean("isActive"));
        ticket.setSoftwareVer(rs.getString("softwareVer"));
        ticket.setTicketVer(rs.getString("ticketVer"));
        ticket.setFaretableVer(rs.getString("faretableVer"));
        ticket.setQuantity(rs.getInt("quantity"));
        ticket.setStatus(rs.getString("status"));
        ticket.setTransactionId(rs.getString("transactionId"));
        ticket.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        ticket.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
        return ticket;
    }
}