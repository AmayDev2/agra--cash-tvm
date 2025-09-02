package com.amay.tom.repository.user;


import com.amay.tom.exceptions.QueryNotAppropriateException;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.model.user.dto.UserPrivilegeDto;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.repository.user.UserRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public class UserRepositoryImpl extends UserRepository {

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    @Override
    void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_USER_TABLE_SQL);
            stmt.execute(CREATE_USER_PRIVILEGE_TABLE_SQL);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void save(UserDto user) throws SQLException {
//        user.setAccountExpiryDate(LocalDateTime.now().plusMonths(50));
//        user.setCredentialsExpiryDate(LocalDateTime.now().plusMonths(50));
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
        try {
            if(findByUsername(user.getUsername()).isPresent()){
                update(user);
                return;
            }
        } catch (SQLException _) {

        }

        try (PreparedStatement pstmt = connection.prepareStatement(USER_INSERT_SQL)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, String.join(",", user.getRoles())); // Example for roles, adjust as needed
            pstmt.setBoolean(4, user.isEnabled());
//            pstmt.setBoolean(5, user.isAccountNonExpired());
//            pstmt.setBoolean(6, user.isCredentialsNonExpired());
//            pstmt.setBoolean(7, user.isAccountNonLocked());
//            pstmt.setObject(8, user.getAccountExpiryDate());
//            pstmt.setObject(9, user.getCredentialsExpiryDate());
//            pstmt.setObject(10, user.getCreatedAt());
//            pstmt.setObject(11, user.getUpdatedAt());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Optional<UserDto> findByUsername(String username)  {
        try (PreparedStatement pstmt = connection.prepareStatement(USER_SELECT_BY_USERNAME_SQL)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserDto user = new UserDto()
                            .setUsername(rs.getString("username"))
                            .setPassword(rs.getString("password"))
                            .setRoles(Set.of(rs.getString("roles").split(","))) // Example for roles
                            .setEnabled(rs.getBoolean("enabled"));
//                            .setAccountNonExpired(rs.getBoolean("accountNonExpired"))
//                            .setCredentialsNonExpired(rs.getBoolean("credentialsNonExpired"))
//                            .setAccountNonLocked(rs.getBoolean("accountNonLocked"))
//                            .setAccountExpiryDate(rs.getObject("accountExpiryDate", LocalDateTime.class))
//                            .setCredentialsExpiryDate(rs.getObject("credentialsExpiryDate", LocalDateTime.class))
//                            .setCreatedAt(rs.getObject("createdAt", LocalDateTime.class))
//                            .setUpdatedAt(rs.getObject("updatedAt", LocalDateTime.class));
                    return Optional.of(user);
                }
            }catch (Exception e){
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }catch (Exception e){
            throw new QueryNotAppropriateException("Query Exception");
        }
        return Optional.empty(); // Return empty Optional if user is not found
    }

    @Override
    void update(UserDto user) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(USER_UPDATE_SQL)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, String.join(",", user.getRoles())); // Example for roles, adjust as needed
            pstmt.setBoolean(3, user.isEnabled());
//            pstmt.setBoolean(4, user.isAccountNonExpired());
//            pstmt.setBoolean(5, user.isCredentialsNonExpired());
//            pstmt.setBoolean(6, user.isAccountNonLocked());
//            pstmt.setObject(7, user.getAccountExpiryDate());
//            pstmt.setObject(8, user.getCredentialsExpiryDate());
//            pstmt.setObject(9, user.getCreatedAt());
//            pstmt.setObject(10, user.getUpdatedAt());
            pstmt.setString(4, user.getUsername());
            pstmt.executeUpdate();
        }
    }

    @Override
    void deleteByUsername(String username) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(USER_DELETE_BY_USERNAME_SQL)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }

    //TODO: userprivillage
    @Override
    void createUserPrivilegeTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_USER_PRIVILEGE_TABLE_SQL);
        }
    }

    @Override
    public void saveUserPrivilege(UserPrivilegeDto userPrivilege) throws SQLException {
        try {
            UserPrivilegeDto userPrivilegeDto=findUserPrivilegeByUsername(userPrivilege.getUsername());
            if(userPrivilegeDto!=null){
                updateUserPrivilege(userPrivilege);
            } else {
                try (PreparedStatement pstmt = connection.prepareStatement(INSERT_USER_PRIVILEGE_SQL)) {
                    pstmt.setString(1, userPrivilege.getUsername());
                    pstmt.setBoolean(2, userPrivilege.isQrTicketIssue());
                    pstmt.setBoolean(3, userPrivilege.isQrTicketAnalysis());
                    pstmt.setBoolean(4, userPrivilege.isQrTicketAdjustment());
                    pstmt.setBoolean(5, userPrivilege.isQrTicketCancellation());
                    pstmt.setBoolean(6, userPrivilege.isQrTicketRefund());
                    pstmt.setBoolean(7, userPrivilege.isQrTicketReprint());
                    pstmt.setBoolean(8, userPrivilege.isQrTicketReplacement());
                    pstmt.setBoolean(9, userPrivilege.isQrFreeTicket());
                    pstmt.setBoolean(10, userPrivilege.isQrPaidTicket());
                    pstmt.setBoolean(11, userPrivilege.isTvm());
                    pstmt.setBoolean(12, userPrivilege.isImportAndExport());
                    pstmt.setBoolean(13, userPrivilege.isShutdownAndRestart());
                    pstmt.executeUpdate();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error during find and update "+ex.getMessage());
        }
    }

    @Override
    public  UserPrivilegeDto findUserPrivilegeByUsername(String username) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_USER_PRIVILEGE_BY_USERNAME_SQL)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserPrivilegeDto()
                            .setUsername(rs.getString("username"))
                            .setQrTicketIssue(rs.getBoolean("qrTicketIssue"))
                            .setQrTicketAnalysis(rs.getBoolean("qrTicketAnalysis"))
                            .setQrTicketAdjustment(rs.getBoolean("qrTicketAdjustment"))
                            .setQrTicketCancellation(rs.getBoolean("qrTicketCancellation"))
                            .setQrTicketRefund(rs.getBoolean("qrTicketRefund"))
                            .setQrTicketReprint(rs.getBoolean("qrTicketReprint"))
                            .setQrTicketReplacement(rs.getBoolean("qrTicketReplacement"))
                            .setQrFreeTicket(rs.getBoolean("qrFreeTicket"))
                            .setQrPaidTicket(rs.getBoolean("qrPaidTicket"))
                            .setTvm(rs.getBoolean("tvm"))
                            .setImportAndExport(rs.getBoolean("importAndExport"))
                            .setShutdownAndRestart(rs.getBoolean("shutdownAndRestart"));
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
        return null; // Or throw an exception if not found
    }

    @Override
    void updateUserPrivilege(UserPrivilegeDto userPrivilege) throws SQLException,SQLIntegrityConstraintViolationException {

        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_USER_PRIVILEGE_SQL)) {
            pstmt.setBoolean(1, userPrivilege.isQrTicketIssue());
            pstmt.setBoolean(2, userPrivilege.isQrTicketAnalysis());
            pstmt.setBoolean(3, userPrivilege.isQrTicketAdjustment());
            pstmt.setBoolean(4, userPrivilege.isQrTicketCancellation());
            pstmt.setBoolean(5, userPrivilege.isQrTicketRefund());
            pstmt.setBoolean(6, userPrivilege.isQrTicketReprint());
            pstmt.setBoolean(7, userPrivilege.isQrTicketReplacement());
            pstmt.setBoolean(8, userPrivilege.isQrFreeTicket());
            pstmt.setBoolean(9, userPrivilege.isQrPaidTicket());
            pstmt.setBoolean(10, userPrivilege.isTvm());
            pstmt.setBoolean(11,userPrivilege.isImportAndExport());
            pstmt.setBoolean(12,userPrivilege.isShutdownAndRestart());
            pstmt.setString(13,userPrivilege.getUsername());
            pstmt.executeUpdate();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    void deleteUserPrivilegeByUsername(String username) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_USER_PRIVILEGE_BY_USERNAME_SQL)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }
}
