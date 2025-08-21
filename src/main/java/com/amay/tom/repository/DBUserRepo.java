package com.amay.tom.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.amay.tom.model.siftdata.ShiftHeader;
import com.amay.tom.model.siftdata.User;
import com.amay.tom.model.user.dto.UserPrivilegeDto;


public class DBUserRepo {

    private static DBUserRepo instance = new DBUserRepo();

    private DBUserRepo() {
    }

    public static DBUserRepo getInstance() {
        return instance;
    }

    public void createUser(Connection connection, User user) throws SQLException {
        String sql = "INSERT INTO users (userId, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUserId());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.executeUpdate();
            System.out.println("User created successfully.");
        }
    }

    public User readUserById(Connection connection, int uId) throws SQLException {
        String sql = "SELECT * FROM users WHERE uId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, uId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUId(resultSet.getInt("uId"));
                    user.setUserId(resultSet.getString("userId"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));
                    return user;
                } else {
                    System.out.println("User not found.");
                    return null;
                }
            }
        }
    }

    public User readUserByUserId(Connection connection, String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE userId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUId(resultSet.getInt("uId"));
                    user.setUserId(resultSet.getString("userId"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));
                    return user;
                } else {
                    System.out.println("User not found.");
                    return null;
                }
            }
        }
    }

    public void updateUser(Connection connection, User user) throws SQLException {
        String sql = "UPDATE users SET userId = ?, password = ?, role = ? WHERE uId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUserId());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
//            statement.setInt(4, user.getUId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user found with the given ID.");
            }
        }
    }

    public void deleteUser(Connection connection, int uId) throws SQLException {
        String sql = "DELETE FROM users WHERE uId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, uId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("No user found with the given ID.");
            }
        }
    }

    public boolean checkPassword(Connection connection, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE userId = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ShiftHeader.getInstance().getOperatorId());
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public UserPrivilegeDto getUserPrivilegeById(Connection connection, String userId) {
        String sql = "SELECT * FROM users_privilege WHERE id = ?";
        UserPrivilegeDto userPrivilegeDto = new UserPrivilegeDto();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userPrivilegeDto.setQrTicketIssue(resultSet.getBoolean("qr_ticket_issue"));
                    userPrivilegeDto.setQrTicketAnalysis(resultSet.getBoolean("qr_ticket_analysis"));
                    userPrivilegeDto.setQrTicketAdjustment(resultSet.getBoolean("qr_ticket_adjustment"));
                    userPrivilegeDto.setQrTicketCancellation(resultSet.getBoolean("qr_ticket_cancellation"));
                    userPrivilegeDto.setQrTicketRefund(resultSet.getBoolean("qr_ticket_refund"));
                    userPrivilegeDto.setQrTicketReprint(resultSet.getBoolean("qr_ticket_reprint"));
                    userPrivilegeDto.setQrTicketReplacement(resultSet.getBoolean("qr_ticket_replacement"));
                    userPrivilegeDto.setQrFreeTicket(resultSet.getBoolean("qr_free_ticket"));
                    userPrivilegeDto.setQrPaidTicket(resultSet.getBoolean("qr_paid_ticket"));
                    userPrivilegeDto.setTvm(resultSet.getBoolean("tvm"));

                    System.out.println("User privileges retrieved successfully." + userPrivilegeDto.toString());
                } else {
                    System.out.println("User not found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userPrivilegeDto;
    }

}
