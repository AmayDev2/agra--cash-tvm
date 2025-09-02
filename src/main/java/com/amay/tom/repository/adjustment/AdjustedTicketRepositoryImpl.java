package com.amay.tom.repository.adjustment;

import com.amay.tom.model.adjust.AdjustedTicketDto;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.tickets.TicketsRepository;
import jakarta.transaction.Transactional;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdjustedTicketRepositoryImpl  extends com.amay.tom.repository.adjustment.AdjustedTicketRepository {

    private TicketsRepository ticketsRepository;
    public AdjustedTicketRepositoryImpl(Connection connection, TicketsRepository ticketsRepository)  {
        try {
            this.connection = connection;
            this.ticketsRepository=ticketsRepository;
            this.createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    void createTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(AdjustedTicketRepository.CREATE_TABLE_SQL1);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public String save(AdjustedTicketDto adjustedTicket) {
        Logger.debug("Adjusted Ticket : ",adjustedTicket.toString());

        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            ticketsRepository.markTicketAdjustedByTicketId(adjustedTicket.getTicketNumber());
            pstmt.setString(1, adjustedTicket.getOrderId());
            pstmt.setString(2, adjustedTicket.getAdjustId());
            pstmt.setString(3, adjustedTicket.getAdjustmentType());
            pstmt.setString(4, adjustedTicket.getIssueTime());
            pstmt.setString(5, adjustedTicket.getEntryTime());
            pstmt.setString(6, adjustedTicket.getExitTime());
            pstmt.setString(7, adjustedTicket.getDestination());
            pstmt.setString(8, adjustedTicket.getTicketNumber());
            pstmt.setString(9, adjustedTicket.getDeviceId());
            pstmt.setString(10, adjustedTicket.getOperatorId());
            pstmt.setString(11, adjustedTicket.getReason());
            pstmt.setString(12, adjustedTicket.getArea());
            pstmt.setString(13, adjustedTicket.getPaymentMode());
            pstmt.setString(14, adjustedTicket.getTransactionId());
            pstmt.setTimestamp(15, Timestamp.valueOf(adjustedTicket.getCreatedAt()));
            pstmt.setTimestamp(16, Timestamp.valueOf(adjustedTicket.getUpdatedAt()));
            pstmt.setTimestamp(17, Timestamp.valueOf(adjustedTicket.getTransactionTime()));
            pstmt.setString(18, adjustedTicket.getEncryptedQR());
            pstmt.setString(19,adjustedTicket.getPenaltyAmount());
            pstmt.setString(20,adjustedTicket.getShiftId());
            pstmt.executeUpdate();
//            try (ResultSet generatedKeys = pstmt.getA()) {
//                if (generatedKeys.next()) {
//                    return String.valueOf(generatedKeys.getInt(1));
//                }
//            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AdjustedTicketDto findById(String adjustId) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, adjustId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AdjustedTicketDto> findForEOS(String shiftId) {
        List<AdjustedTicketDto> adjustedTicketDtos=new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_EOS_SQL)) {
            pstmt.setString(1, shiftId);
            ResultSet rs = pstmt.executeQuery();
            Logger.debug("QUERY: "+pstmt);
            while (rs.next()) {
                adjustedTicketDtos.add(mapRow(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return adjustedTicketDtos;
    }

    @Override
    public List<AdjustedTicketDto> findAllQRTicketsFrom(Timestamp from) {
        List<AdjustedTicketDto> adjustedTicketDtos=new ArrayList<>();
        String sql = "SELECT * FROM adjusted_tickets WHERE createdAt >= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                adjustedTicketDtos.add(mapRow(rs));
            }
        } catch (SQLException e) {
            Logger.debug("Error executing query: {}", e.getMessage());
        }
        return adjustedTicketDtos;


    }

    @Override
    public void update(AdjustedTicketDto adjustedTicket)  {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL)) {
            pstmt.setString(1, adjustedTicket.getOrderId());
            pstmt.setString(2, adjustedTicket.getAdjustmentType());
            pstmt.setString(3, adjustedTicket.getIssueTime());
            pstmt.setString(4, adjustedTicket.getEntryTime());
            pstmt.setString(5, adjustedTicket.getExitTime());
            pstmt.setString(6, adjustedTicket.getDestination());
            pstmt.setString(7, adjustedTicket.getTicketNumber());
            pstmt.setString(8, adjustedTicket.getDeviceId());
            pstmt.setString(9, adjustedTicket.getOperatorId());
            pstmt.setString(10, adjustedTicket.getReason());
            pstmt.setString(11, adjustedTicket.getArea());
            pstmt.setString(12, adjustedTicket.getPaymentMode());
            pstmt.setString(13, adjustedTicket.getTransactionId());
            pstmt.setTimestamp(14, Timestamp.valueOf(adjustedTicket.getCreatedAt()));
            pstmt.setTimestamp(15, Timestamp.valueOf(adjustedTicket.getUpdatedAt()));
            pstmt.setTimestamp(16, Timestamp.valueOf(adjustedTicket.getTransactionTime()));
            pstmt.setString(17, adjustedTicket.getAdjustId());
            pstmt.setString(18, adjustedTicket.getEncryptedQR());
            pstmt.setString(19,adjustedTicket.getPenaltyAmount());
            pstmt.setString(20,adjustedTicket.getShiftId());
            pstmt.setBoolean(21, adjustedTicket.isCcu());
            pstmt.setBoolean(22,adjustedTicket.isScu());
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(String adjustId)  {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            pstmt.setString(1, adjustId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void pushTickets(List<String> ticketIds, String column) {
        // validate column name to prevent SQL injection
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }

        String sql = "UPDATE " + TABLE_NAME + " SET " + column + " = true WHERE adjustId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String id : ticketIds) {
                pstmt.setString(1, id);
                pstmt.addBatch();
            }
            int[] updated = pstmt.executeBatch();
            Logger.info("Marked {} for {} adjusts", column.toUpperCase(), updated.length);
        } catch (SQLException e) {
            Logger.error("Error marking {} for adjusts: {}", column.toUpperCase(), e.getMessage());
            throw new RuntimeException("Failed to mark " + column.toUpperCase() + " for adjusts", e);
        }
    }

    @Override
    public List<AdjustedTicketDto> findNotPushedTicket(String column) {
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }
        column+=" = false";

        String query="SELECT * FROM "+ TABLE_NAME +" WHERE "+ column+" ORDER BY createdAt DESC";

        List<AdjustedTicketDto> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement( query)) {
//            pstmt.setString(1, column);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs)); // Add each AdjustedTicketDto to the list
            }
        } catch (SQLException e) {
            Logger.debug("Error fetching QR tickets: {}", e.getMessage());

        }

        return list;
    }


    private AdjustedTicketDto mapRow(ResultSet rs) throws SQLException {
        AdjustedTicketDto adjustedTicket = new AdjustedTicketDto();
        adjustedTicket.setOrderId(rs.getString("orderId"));
        adjustedTicket.setAdjustId(rs.getString("adjustId"));
        adjustedTicket.setAdjustmentType(rs.getString("adjustmentType"));
        adjustedTicket.setIssueTime(rs.getString("issueTime"));
        adjustedTicket.setEntryTime(rs.getString("entryTime"));
        adjustedTicket.setExitTime(rs.getString("exitTime"));
        adjustedTicket.setDestination(rs.getString("destination"));
        adjustedTicket.setTicketNumber(rs.getString("ticketNumber"));
        adjustedTicket.setDeviceId(rs.getString("deviceId"));
        adjustedTicket.setOperatorId(rs.getString("operatorId"));
        adjustedTicket.setReason(rs.getString("reason"));
        adjustedTicket.setArea(rs.getString("area"));
        adjustedTicket.setPaymentMode(rs.getString("paymentMode"));
        adjustedTicket.setTransactionId(rs.getString("transactionId"));
        adjustedTicket.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        adjustedTicket.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
        adjustedTicket.setTransactionTime(rs.getTimestamp("transactionTime").toLocalDateTime());
        adjustedTicket.setEncryptedQR(rs.getString("encryptedQR"));
        adjustedTicket.setPenaltyAmount(rs.getString("penaltyAmount"));
        adjustedTicket.setShiftId(rs.getString("shiftId"));
        adjustedTicket.setCcu(rs.getBoolean("ccu"));
        adjustedTicket.setScu(rs.getBoolean("scu"));
        return adjustedTicket;
    }
}
