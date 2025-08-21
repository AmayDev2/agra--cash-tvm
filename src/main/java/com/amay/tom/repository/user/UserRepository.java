package com.amay.tom.repository.user;

import com.amay.tom.exceptions.QueryNotAppropriateException;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.model.user.dto.UserPrivilegeDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public abstract class UserRepository {

    protected static final String USER_TABLE_NAME = "user";
    protected static final String USER_PRIVILEGE_TABLE_NAME = "user_privilege";
    protected Connection connection = null;

    protected static final String CREATE_USER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (" +
            "username VARCHAR(255) PRIMARY KEY," +
            "password VARCHAR(255)," +
            "roles VARCHAR(255)," +
            "enabled BOOLEAN" +   //removed comma
//            "accountNonExpired BOOLEAN," +
//            "credentialsNonExpired BOOLEAN," +
//            "accountNonLocked BOOLEAN," +
//            "accountExpiryDate DATETIME," +
//            "credentialsExpiryDate DATETIME," +
//            "createdAt DATETIME," +
//            "updatedAt DATETIME" +
            ");";

    // SQL for User Privilege Table
    protected static final String CREATE_USER_PRIVILEGE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + USER_PRIVILEGE_TABLE_NAME + " (" +
            "username VARCHAR(255) PRIMARY KEY," +
            "qrTicketIssue BOOLEAN," +
            "qrTicketAnalysis BOOLEAN," +
            "qrTicketAdjustment BOOLEAN," +
            "qrTicketCancellation BOOLEAN," +
            "qrTicketRefund BOOLEAN," +
            "qrTicketReprint BOOLEAN," +
            "qrTicketReplacement BOOLEAN," +
            "qrFreeTicket BOOLEAN," +
            "qrPaidTicket BOOLEAN," +
            "tvm BOOLEAN," +
            "importAndExport BOOLEAN," +
            "shutdownAndRestart BOOLEAN" +
            ");";


    protected static final String INSERT_USER_PRIVILEGE_SQL = "INSERT INTO " + USER_PRIVILEGE_TABLE_NAME + " (username, qrTicketIssue, qrTicketAnalysis, qrTicketAdjustment, qrTicketCancellation, qrTicketRefund, qrTicketReprint, qrTicketReplacement, qrFreeTicket, qrPaidTicket, tvm, importAndExport, shutdownAndRestart) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?)";

    protected static final String SELECT_USER_PRIVILEGE_BY_USERNAME_SQL = "SELECT * FROM " + USER_PRIVILEGE_TABLE_NAME + " WHERE username = ?";
    protected static final String UPDATE_USER_PRIVILEGE_SQL = "UPDATE " + USER_PRIVILEGE_TABLE_NAME + " SET qrTicketIssue=?, qrTicketAnalysis=?, qrTicketAdjustment=?, qrTicketCancellation=?, qrTicketRefund=?, qrTicketReprint=?, qrTicketReplacement=?, qrFreeTicket=?, qrPaidTicket=?, tvm=? ,importAndExport=?, shutdownAndRestart=? WHERE username=?";
    protected static final String DELETE_USER_PRIVILEGE_BY_USERNAME_SQL = "DELETE FROM " + USER_PRIVILEGE_TABLE_NAME + " WHERE username = ?";







    protected static final String USER_INSERT_SQL = "INSERT INTO " + USER_TABLE_NAME + " (username, password, roles, enabled" +
//            "/*, accountNonExpired, credentialsNonExpired, accountNonLocked, accountExpiryDate, credentialsExpiryDate, createdAt, updatedAt*/" +
            ") " +
            "VALUES (?, ?, ?, ?" +
//            " ?, ?, ?, ?, ?, ?, ?" +
            ")";

    protected static final String USER_SELECT_BY_USERNAME_SQL = "SELECT * FROM " + USER_TABLE_NAME + " WHERE username = ?";
    protected static final String USER_UPDATE_SQL = "UPDATE " + USER_TABLE_NAME + " SET password=?, roles=?, enabled=?" +
//            ", accountNonExpired=?, credentialsNonExpired=?, accountNonLocked=?, accountExpiryDate=?, credentialsExpiryDate=?, createdAt=?, updatedAt=?" +
            " WHERE username=?";
    protected static final String USER_DELETE_BY_USERNAME_SQL = "DELETE FROM " + USER_TABLE_NAME + " WHERE username = ?";

    abstract void createTableIfNotExists() throws SQLException;
    public abstract void save(UserDto user) throws SQLException;
    public abstract Optional<UserDto> findByUsername(String username) throws UsernameNotFoundException, QueryNotAppropriateException;
    abstract void update(UserDto user) throws SQLException;
    abstract void deleteByUsername(String username) throws SQLException;


    // User Privilege Table Methods
    abstract void createUserPrivilegeTableIfNotExists() throws SQLException;
    public abstract void saveUserPrivilege(UserPrivilegeDto userPrivilege) throws SQLException;
    public abstract UserPrivilegeDto findUserPrivilegeByUsername(String username) throws SQLException;
    abstract void updateUserPrivilege(UserPrivilegeDto userPrivilege) throws SQLException;
    abstract void deleteUserPrivilegeByUsername(String username) throws SQLException;
}

