package com.amay.tom.repository.session;

import com.amay.tom.model.session.ShiftDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public abstract class ShiftRepository {

    protected static final String TABLE_NAME = "shift_session";
    protected Connection connection = null;

    protected static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " + // Auto-increment column as the primary key
            "shift_id VARCHAR(255) UNIQUE, " + // Unique key constraint on shift_id
            "operator_id VARCHAR(255), " +
            "device_id VARCHAR(255), " +
            "device_serial VARCHAR(255), " +
            "created_at TIMESTAMP, " +
            "start_time TIMESTAMP, " +
            "end_time TIMESTAMP, " +
            "serial_no VARCHAR(255), " +
            "station_id VARCHAR(255), " +
            "line_no VARCHAR(255), " +
            "reason VARCHAR(255), " +
            "current_status VARCHAR(255), " +
            "update_at TIMESTAMP, " +
            "imprest_money VARCHAR(255), "+
            "config_version VARCHAR(255), "+
            "ccu BOOLEAN DEFAULT FALSE, "+
            "scu BOOLEAN DEFAULT FALSE"+// Fixed column name
            ");";


    protected static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (shift_id, operator_id, device_id, device_serial, created_at, start_time, end_time, serial_no, station_id, line_no, reason, current_status, update_at, imprest_money, config_version) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    protected static final String SELECT_BY_ID_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE shift_id = ?";
    protected static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + " SET operator_id=?, device_id=?, device_serial=?, created_at=?, start_time=?, end_time=?, serial_no=?, station_id=?, line_no=?, reason=?, current_status=? update_at=? WHERE shift_id=?";
    protected static final String UPDATE_IMPREST_SQL = "UPDATE " + TABLE_NAME + " SET imprest_money=? WHERE shift_id=?";
    protected static final String DELETE_BY_ID_SQL = "DELETE FROM " + TABLE_NAME + " WHERE shift_id = ?";
    protected static final String END_SHIFT_SQL = "UPDATE " + TABLE_NAME + " SET end_time=?, reason=?, current_status=?, update_at=? WHERE shift_id=? AND operator_id=? AND end_time IS NULL";
    protected static final String PAUSE_RESUME_SHIFT_SQL = "UPDATE " + TABLE_NAME + " SET current_status=?, update_at=? WHERE shift_id=? AND operator_id=? AND end_time IS NULL";
    protected static final String FIND_LAST_SHIFT_ID_SQL = "SELECT shift_id FROM " + TABLE_NAME + " WHERE current_status != 'COMPLETED'  ORDER BY created_at DESC LIMIT 1 ";
    protected static final String END_LAST_SHIFT_SQL = "UPDATE " + TABLE_NAME + " SET end_time=?, reason=?, current_status=?, update_at=? WHERE shift_id=? AND end_time IS NULL";
    protected static final String FIND_OPERATOR_ID_BY_SHIFT_ID_SQL = "SELECT operator_id FROM " + TABLE_NAME + " WHERE shift_id = ?";
    protected static final String FIND_SHIFT_FROM_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE created_at >= ? ORDER BY created_at DESC";
    protected static final String FIND_NOT_PUSHED_SHIFT = "SELECT * FROM " + TABLE_NAME + " WHERE ? ORDER BY created_at DESC";
    protected static final String FIND_LAST_SHIFT = "SELECT shift_id FROM " + TABLE_NAME + " WHERE CAST(created_at AS DATE) = CURRENT_DATE ORDER BY created_at DESC LIMIT 1";


    abstract void createTableIfNotExists() throws SQLException;

    public abstract int save(ShiftDto shift) throws SQLException;

    public abstract Optional<ShiftDto> findById(String shiftId);

    public abstract List<ShiftDto> findAll() throws SQLException;

    public abstract void update(ShiftDto shift) throws SQLException;

    public abstract void deleteById(String shiftId) throws SQLException;

    public abstract int startShift(ShiftDto shift) throws SQLException;

    public abstract void updateImprest(String shiftId, String imprest);

    public abstract String findLastShiftId() throws SQLException;

    public abstract void endShift(ShiftDto dto) throws SQLException;

    public abstract int countShiftIdsWithPrefix(String shiftPrefix) throws SQLException;

    public abstract void shiftPauseResume(ShiftDto shift) throws SQLException;

    public abstract Optional<String> findLastUncompletedShiftId();

    public abstract Optional<String> findLastShift();

    public abstract boolean markLastShiftAsCompleted(ShiftDto shiftDto);

    public abstract String findOperatorIdByShiftId(String shiftId);

    public abstract List<ShiftDto> findShiftFrom(Timestamp from);

    public abstract void pushShifts(List<String> shiftIds, String column);

    public abstract List<ShiftDto> findNotPushedShifts(String column);
}
