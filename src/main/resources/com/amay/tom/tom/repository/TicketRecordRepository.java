package com.amay.tom.repository;

import com.amay.tom.model.siftdata.TicketRecord;
import com.amay.tom.utils.time.TimeUtil;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TicketRecordRepository {

    private static TicketRecordRepository instance = new TicketRecordRepository();

    private TicketRecordRepository(){}

    public static TicketRecordRepository getInstance(){
        return instance;
    }

    public void createTicketRecord(Connection connection, TicketRecord ticketRecord) throws SQLException {
        String sql = "INSERT INTO ticketsrecord (tickettype, price, siftdate, count, sift) VALUES (?, ?, ?, 1, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ticketRecord.getTicketType());
            statement.setInt(2, ticketRecord.getPrice());
            statement.setString(3, ticketRecord.getSiftdate());
            statement.setInt(4, ticketRecord.getSift());
            statement.executeUpdate();
            System.out.println("Ticket record created successfully.");
        }catch (Exception e){
            Logger.error("ticket creation {}",e.getMessage());
        }
    }

    public void upsertTicketRecord(Connection connection, TicketRecord ticketRecord) throws SQLException {
        String sql = "INSERT INTO ticketsrecord (tickettype, price, siftdate, count, sift) " +
                "VALUES (?, ?, ?, 1, ?) " +
                "ON CONFLICT(tickettype, sift, time) DO UPDATE SET count = count + 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ticketRecord.getTicketType());
            statement.setInt(2, ticketRecord.getPrice());
            statement.setString(3, String.valueOf(ticketRecord.getSiftdate()));
            statement.setInt(4, ticketRecord.getSift());
            statement.executeUpdate();
            System.out.println("Ticket record upserted successfully.");
        }
    }


    public TicketRecord readTicketRecord(Connection connection, String ticketType, int price, String sift,String siftDate) throws SQLException {
        Logger.info("Reading ticket record with the given details. {} {} {} {}", ticketType, price, sift,siftDate);
        String sql = "SELECT * FROM ticketsrecord WHERE tickettype = ? AND price = ? AND sift = ? AND siftdate=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ticketType);
            statement.setInt(2, price);
            statement.setString(3, sift);
            statement.setString(4, siftDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    TicketRecord ticketRecord = new TicketRecord();
                    ticketRecord.setTicketType(resultSet.getString("tickettype"));
                    ticketRecord.setPrice(resultSet.getInt("price"));
                    ticketRecord.setSiftdate(resultSet.getString("siftdate"));
                    ticketRecord.setCount(resultSet.getInt("count"));
                    ticketRecord.setSift(resultSet.getInt("sift"));
                    return ticketRecord;
                } else {
                    Logger.info("No ticket record found with the given details. {} {} {}", ticketType, price, sift);
                    return null;
                }
            }
        }
    }

    public ArrayList<TicketRecord> readAllTicketRecords(Connection connection) throws SQLException {
        String sql = "SELECT * FROM ticketsrecord";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            ArrayList<TicketRecord> ticketRecords = new ArrayList<>();
            while (resultSet.next()) {
                TicketRecord ticketRecord = new TicketRecord();
                ticketRecord.setTicketType(resultSet.getString("tickettype"));
                ticketRecord.setPrice(resultSet.getInt("price"));
                ticketRecord.setSiftdate(resultSet.getString("siftdate"));
                ticketRecord.setCount(resultSet.getInt("count"));
                ticketRecord.setSift(resultSet.getInt("sift"));
                ticketRecords.add(ticketRecord);

//                System.out.println(ticketRecord);
            }
            return ticketRecords;
        }
    }


    public ArrayList<TicketRecord> readAllTicketOfCurrentShift(Connection connection,int shiftId){
        Logger.debug("Reading all ticket records of the current shift. {} {}", shiftId,connection);
        String sql = "SELECT * FROM ticketsrecord WHERE sift = ?";
        ArrayList<TicketRecord> ticketRecords = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shiftId);
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    TicketRecord ticketRecord = new TicketRecord();
                    ticketRecord.setTicketType(resultSet.getString("tickettype"));
                    ticketRecord.setPrice(resultSet.getInt("price"));
                    ticketRecord.setSiftdate(resultSet.getString("siftdate"));
                    ticketRecord.setCount(resultSet.getInt("count"));
                    ticketRecord.setSift(resultSet.getInt("sift"));
                    ticketRecords.add(ticketRecord);
                }
            }
        }catch (Exception e){

            Logger.error("Error in reading ticket records {}",e.getMessage());
            e.printStackTrace();
        }
        return ticketRecords;
    }

    public ArrayList<TicketRecord> readAllTicketRecordsByToday(Connection connection) throws SQLException {
        String sql = "SELECT * FROM ticketsrecord WHERE siftdate = "+TimeUtil.getCurrentYearMonthDay();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            ArrayList<TicketRecord> ticketRecords = new ArrayList<>();
            while (resultSet.next()) {
                TicketRecord ticketRecord = new TicketRecord();
                ticketRecord.setTicketType(resultSet.getString("tickettype"));
                ticketRecord.setPrice(resultSet.getInt("price"));
                ticketRecord.setSiftdate(resultSet.getString("siftdate"));
                ticketRecord.setCount(resultSet.getInt("count"));
                ticketRecord.setSift(resultSet.getInt("sift"));
                ticketRecords.add(ticketRecord);

//                System.out.println(ticketRecord);
            }
            return ticketRecords;
        }
    }

    public void updateTicketRecord(Connection connection, TicketRecord ticketRecord) throws SQLException {
        String sql = "UPDATE ticketsrecord SET count = count + 1 WHERE tickettype = ? AND price = ? AND  sift = ? AND siftdate = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ticketRecord.getTicketType());
            statement.setInt(2, ticketRecord.getPrice());
            statement.setInt(3, ticketRecord.getSift());
            statement.setString(4, ticketRecord.getSiftdate());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Ticket record updated successfully.");
            } else {
                System.out.println("No ticket record found with the given details.");
            }
        }
    }

    public void updateTicketRecordByPKey(Connection connection, TicketRecord ticketRecord) throws SQLException {
        String sql = "UPDATE ticketsrecord SET count = count + 1 WHERE pid=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketRecord.getPid());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Ticket record updated successfully.");
            } else {
                System.out.println("No ticket record found with the given details.");
            }
        }
    }

    public void deleteTicketRecord(Connection connection, String ticketType, int price, String sift) throws SQLException {
        String sql = "DELETE FROM ticketsrecord WHERE tickettype = ? AND price = ? AND sift = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ticketType);
            statement.setInt(2, price);
            statement.setString(3, sift);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Ticket record deleted successfully.");
            } else {
                System.out.println("No ticket record found with the given details.");
            }
        }
    }
}

