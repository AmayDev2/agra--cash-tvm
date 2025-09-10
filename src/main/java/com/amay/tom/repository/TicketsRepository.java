package com.amay.tom.repository;

import com.amay.tom.model.QRTicket;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketsRepository {

    private static TicketsRepository instance = new TicketsRepository();

    private TicketsRepository(){}

    public static TicketsRepository getInstance(){
        return instance;
    }


//    public void testInsert(Connection connection){
//        String sql = "INSERT INTO tickets (ticketNo, initiateDateTime, expiryTime, \"from\", \"to\", type, fareMode, price, qrCodeData, qty) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setString(1, "000002");
//            statement.setString(2, "2024-04-01 03:27:57");
//            statement.setString(3, "2024-03-31 22:57:57");
//            statement.setString(4, "CUFFE PARADE");
//            statement.setString(5, "GRANT ROAD METRO");
//            statement.setString(6, "SJT");
//            statement.setString(7, "Card");
//            statement.setDouble(8, 30);
//            statement.setString(9, "MjQ2NDE6MDAwMDAyOjE3MTE5MjIyNzc6MTcxMTkyNTg3NzowMDMwOjAxOjA4OjAxOjAx");
//            statement.setInt(10, 1);
//
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            //System.out.println("Error inserting test data: " + e.getMessage());
//        }
//    }




    public void insertTicket(Connection connection, QRTicket ticket, String orderId) {
        String sql = "INSERT INTO tickets (ticketNo, initiateDateTime, expiryTime, from_date, to_date, type, fareMode, price, qrCodeData, qty,order_id,ticket_cancel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,false)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ticket.getTicketNo());
            statement.setString(2, ticket.getInitiateDateTime());
            statement.setString(3, ticket.getExpiryTime());
            statement.setString(4, ticket.getFrom());
            statement.setString(5, ticket.getTo());
            statement.setString(6, ticket.getType());
            statement.setString(7, ticket.getFareMode());
            statement.setDouble(8, Double.parseDouble(ticket.getPrice()));
            statement.setString(9, ticket.getQrCodeData());
            statement.setInt(10, ticket.getQty());
            statement.setString(11, orderId);

            statement.executeUpdate();
            Logger.debug("Ticket created successfully");
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting ticket: " + e.getMessage());
        }

    }

    public String getLastTicketNumber(Connection connection) {
        String sql = "SELECT ticketNo FROM tickets ORDER BY ticketNo DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("ticketNo");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            //System.out.println("Error getting last ticket number: " + e.getMessage());
            return null;
        }
    }

    public List<QRTicket> findQRTicketFrom(Connection connection, Timestamp createdAt){
        List<QRTicket> qrTickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE  createdAt>= ? order by createdAt desc";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, createdAt);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    QRTicket qrTicket= new QRTicket();
                    qrTicket.setTicketNo(resultSet.getString("ticketNo"));
                    qrTicket.setInitiateDateTime(resultSet.getString("initiateDateTime"));
                    qrTicket.setExpiryTime(resultSet.getString("expiryTime"));
                    qrTicket.setFrom(resultSet.getString("from_date"));
                    qrTicket.setTo(resultSet.getString("to_date"));
                    qrTicket.setType(resultSet.getString("type"));
                    qrTicket.setFareMode(resultSet.getString("fareMode"));
                    qrTicket.setPrice(resultSet.getString("price"));
                    qrTicket.setQrCodeData(resultSet.getString("qrCodeData"));
                    qrTicket.setQty(resultSet.getInt("qty"));
                    qrTickets.add(qrTicket);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error getting ticket by ticket number: {}", e);
        }
        return qrTickets;

    }

    public QRTicket getTicketByTicketNo(Connection connection, String ticketNo) {
        String sql = "SELECT * FROM tickets WHERE ticketNo = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ticketNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    QRTicket qrTicket= new QRTicket();
                    qrTicket.setTicketNo(resultSet.getString("ticketNo"));
                    qrTicket.setInitiateDateTime(resultSet.getString("initiateDateTime"));
                    qrTicket.setExpiryTime(resultSet.getString("expiryTime"));
                    qrTicket.setFrom(resultSet.getString("from_date"));
                    qrTicket.setTo(resultSet.getString("to_date"));
                    qrTicket.setType(resultSet.getString("type"));
                    qrTicket.setFareMode(resultSet.getString("fareMode"));
                    qrTicket.setPrice(resultSet.getString("price"));
                    qrTicket.setQrCodeData(resultSet.getString("qrCodeData"));
                    qrTicket.setQty(resultSet.getInt("qty"));
                    return qrTicket;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error getting ticket by ticket number: {}", e);
            return null;
        }
    }

    public List<QRTicket> getTicketByOrderId(Connection connection, String orderId) {
        String sql = "SELECT * FROM tickets  WHERE ticket_cancel =false AND order_id = ?";
        List<QRTicket> qrTickets = new ArrayList<>();
        //System.out.println("Order ID: " + orderId);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, orderId);

            Logger.debug("Statment {}",statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    QRTicket qrTicket = new QRTicket();
                    qrTicket.setTicketNo(resultSet.getString("ticketNo"));
                    qrTicket.setInitiateDateTime(resultSet.getString("initiateDateTime"));
                    qrTicket.setExpiryTime(resultSet.getString("expiryTime"));
                    qrTicket.setFrom(resultSet.getString("from_date"));
                    qrTicket.setTo(resultSet.getString("to_date"));
                    qrTicket.setType(resultSet.getString("type"));
                    qrTicket.setFareMode(resultSet.getString("fareMode"));
                    qrTicket.setPrice(resultSet.getString("price"));
                    qrTicket.setQrCodeData(resultSet.getString("qrCodeData"));
                    qrTicket.setQty(resultSet.getInt("qty"));
                    qrTicket.setOrderId(resultSet.getString("order_id"));
                    qrTickets.add(qrTicket);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error getting tickets by order ID: {}", e);
        }

        return qrTickets;
    }


    public void markTicketCancelByOrderId(Connection connection, String orderId) {
        String sql = "UPDATE tickets SET ticket_cancel = true WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, orderId);
            statement.executeUpdate();
            Logger.debug("Ticket marked as cancelled");
        } catch (SQLException e) {
            Logger.error("Error marking ticket as cancelled: {}", e);
        }
    }
}
