package com.amay.tom.repository.Replacement;

import com.amay.tom.model.replacement.Replacement;
import com.amay.tom.repository.tickets.TicketsRepository;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ReplacementTicketRepositoryImpl extends ReplacementTicketRepository {

    private final Connection connection;
    private TicketsRepository ticketsRepository;

    public ReplacementTicketRepositoryImpl(Connection connection, TicketsRepository ticketsRepository)  {
        this.connection = connection;
        this.ticketsRepository=ticketsRepository;
        this.createTableIfNotExists();
    }

    private void createTableIfNotExists() {


        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL1);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("Error creating Replacements table: {}", e.getMessage());
        }
    }

    @Override
    public void insert(Replacement Replacement) throws RuntimeException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, Replacement.getTicketNumber());
            pstmt.setDouble(2, Replacement.getAmount());
            pstmt.setString(3, Replacement.getShiftId());
            pstmt.setString(4, Replacement.getOperatorId());
            pstmt.setString(5, Replacement.getDeviceId());
            pstmt.setTimestamp(6, Timestamp.valueOf(Replacement.getCreationDateTime()));
            pstmt.setTimestamp(7, Timestamp.valueOf(Replacement.getUpdateDateTime()));
            pstmt.setString(8,Replacement.getTicketType().toString());
            pstmt.executeUpdate();
//            ticketsRepository.markTicketReplacementedByTicketId( Replacement.getTicketNumber());
        } catch (SQLException e) {
            Logger.error("Error inserting Replacement: {}", e.getMessage());
            // Handle the exception, e.g., log it or rethrow it
            throw new RuntimeException("Failed to insert Replacement", e);
        }
    }

    @Override
    public Replacement findByTicketNumber(String ticketNumber) {
        
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, ticketNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReplacement(rs);
            }
        } catch (SQLException e) {
            Logger.error("Error finding Replacement by ticket number: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<Replacement> findByShiftId(String shiftId) {
        List<Replacement> Replacements = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_EOS_SQL)) {
            pstmt.setString(1, shiftId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Replacements.add(mapResultSetToReplacement(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding Replacements by shift ID: {}", e.getMessage());
        }
        return Replacements;
    }

    @Override
    public List<Replacement> findAllQRTicketsFrom(Timestamp from) {
        List<Replacement> Replacements = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_Replacement_TICKETS_FROM_SQL)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Replacements.add(mapResultSetToReplacement(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding all QR tickets from: {}", e.getMessage());
        }
        return Replacements;
    }

    private Replacement mapResultSetToReplacement(ResultSet rs) throws SQLException {
        return new Replacement(
            rs.getString("ticket_number"),
            rs.getDouble("amount"),
            rs.getString("shift_id"),
            rs.getString("operator_id"),
            rs.getString("device_id"),
            rs.getTimestamp("creation_date_time").toLocalDateTime(),
            rs.getTimestamp("update_date_time").toLocalDateTime(),
                rs.getString("ticket_type")
        );
    }
} 