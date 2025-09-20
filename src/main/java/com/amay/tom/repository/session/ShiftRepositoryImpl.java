package com.amay.tom.repository.session;

import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.repository.session.ShiftRepository;
import jakarta.transaction.Transactional;
import org.tinylog.Logger;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShiftRepositoryImpl extends ShiftRepository {


    private final Connection connection;

    public ShiftRepositoryImpl(Connection connection) {
        this.connection = connection;
        this.createTableIfNotExists();
    }

    @Override
    public void createTableIfNotExists()  {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public int save(ShiftDto shift)  {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL)) {

            pstmt.setString(1, shift.getShiftId());
            pstmt.setString(2, shift.getOperatorId());
            pstmt.setString(3, shift.getDeviceId());
            pstmt.setString(4, shift.getDeviceSerial());
            pstmt.setTimestamp(5, shift.getCreatedAt() != null ? shift.getCreatedAt() : null);
            pstmt.setTimestamp(6, shift.getStartTime() != null ? shift.getStartTime() : null);
            pstmt.setTimestamp(7, shift.getEndTime() != null ? shift.getEndTime() : null);
            pstmt.setString(8, shift.getSerialNo());
            pstmt.setString(9, shift.getStationId());
            pstmt.setString(10, shift.getLineNo());
            pstmt.setString(11, shift.getReason());
            pstmt.setString(12, shift.getCurrentStatus());
            pstmt.setTimestamp(13, shift.getUpdatedAt() != null ? shift.getUpdatedAt() : null); // maps to update_at
            pstmt.setString(14, shift.getImprest_money());
            pstmt.setString(15, shift.getConfig_version());
            //System.out.println(pstmt);
            // Execute the update
            int affectedRows = pstmt.executeUpdate();
            //System.out.println(affectedRows);
//
//            // Check if the update was successful and if keys are generated
//            if (affectedRows > 0) {
//                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
//                    if (generatedKeys.next()) {
//                        return generatedKeys.getInt(1); // Return the auto-generated key
//                    }
//                }
//            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public Optional<ShiftDto> findById(String shiftId)  {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, shiftId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ShiftDto shift = mapResultSetToShiftDto(rs);
                return Optional.of(shift);
            }
        } catch (Exception e) {
            Logger.error("Error finding shift by ID: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<ShiftDto> findAll() throws SQLException {
        List<ShiftDto> shifts = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
            while (rs.next()) {
                ShiftDto shift = mapResultSetToShiftDto(rs);
                shifts.add(shift);
            }
        }
        return shifts;
    }

    @Override
    public void update(ShiftDto shift) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL)) {
            pstmt.setString(1, shift.getOperatorId());
            pstmt.setString(2, shift.getDeviceId());
            pstmt.setString(3, shift.getDeviceSerial());
            pstmt.setTimestamp(4, Timestamp.valueOf(shift.getCreatedAt().toLocalDateTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(shift.getStartTime().toLocalDateTime()));
            pstmt.setTimestamp(6, Timestamp.valueOf(shift.getEndTime().toLocalDateTime()));
            pstmt.setString(7, shift.getSerialNo());
            pstmt.setString(8, shift.getStationId());
            pstmt.setString(9, shift.getLineNo());
            pstmt.setString(10, shift.getReason());
            pstmt.setString(11, shift.getCurrentStatus());
            pstmt.setString(12, shift.getShiftId());
            pstmt.setTimestamp(13, Timestamp.valueOf(shift.getUpdatedAt().toLocalDateTime()));
            pstmt.setString(14,shift.getImprest_money());
            pstmt.setString(15,shift.getConfig_version());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(String shiftId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            pstmt.setString(1, shiftId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public int startShift(ShiftDto shift) throws SQLException {
        return this.save(shift);
    }

    @Override
    public void endShift(ShiftDto shift) throws SQLException {
        try(PreparedStatement pstmt = connection.prepareStatement(END_SHIFT_SQL)){
            pstmt.setTimestamp(1, shift.getEndTime() != null ? shift.getEndTime() : null);
            pstmt.setString(2, shift.getReason());
            pstmt.setString(3, shift.getCurrentStatus());
            pstmt.setTimestamp(4, shift.getUpdatedAt() != null ? shift.getUpdatedAt() : null);
            pstmt.setString(5, shift.getShiftId());
            pstmt.setString(6, shift.getOperatorId());
            //System.out.println(pstmt);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateImprest(String shiftId, String imprest){
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_IMPREST_SQL)) {
            pstmt.setString(1, imprest);
            pstmt.setString(2, shiftId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String findLastShiftId() throws SQLException{
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT shift_id FROM "+TABLE_NAME+" ORDER BY shift_id DESC LIMIT 1");
            if(rs.next()){
                //System.out.println("Last Shift Id : "+rs.getString("shift_id"));
                return rs.getString("shift_id");
            }
        }
        return "0";
    }

    @Override
    public int countShiftIdsWithPrefix(String shiftPrefix) throws SQLException {
        String query = String.format(
                "SELECT COUNT(*) AS count FROM %s " +
                        "WHERE LEFT(shift_id, CHAR_LENGTH(shift_id) - 2) = '%s'",
                TABLE_NAME, shiftPrefix
        );

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int count = rs.getInt("count");
                //System.out.println("Count for prefix [" + shiftPrefix + "]: " + count);
                return count;
            }
        }
        return 0;
    }


    @Override
    public void shiftPauseResume(ShiftDto shift) throws SQLException {
        try(PreparedStatement pstmt = connection.prepareStatement(PAUSE_RESUME_SHIFT_SQL)){
            pstmt.setString(1, shift.getCurrentStatus());
            pstmt.setTimestamp(2, shift.getUpdatedAt() != null ? shift.getUpdatedAt() : null);
            pstmt.setString(3, shift.getShiftId());
            pstmt.setString(4, shift.getOperatorId());
//            pstmt.setTimestamp(5, shift.getStartTime());
            //System.out.println(pstmt);

            int affectedRows = pstmt.executeUpdate();
            //System.out.println("Resume Paused : "+affectedRows);
        }
    }


    @Override
    public Optional<String> findLastUncompletedShiftId() {
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(FIND_LAST_SHIFT_ID_SQL);
            if(rs.next()){
                //System.out.println("Last Shift Id : "+rs.getString("shift_id"));
                return Optional.ofNullable(rs.getString("shift_id"));
            }
        } catch (RuntimeException | SQLException e) {
            Logger.info("Error finding last uncompleted shift ID: {}", e.getMessage());
        }
        return Optional.empty();
    }


    @Override
    public Optional<String> findLastShift() {
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(FIND_LAST_SHIFT);
            if(rs.next()){
                //System.out.println("Last Shift Id : "+rs.getString("shift_id"));
                return Optional.ofNullable(rs.getString("shift_id"));
            }
        } catch (RuntimeException | SQLException e) {
            Logger.info("Error finding last uncompleted shift ID: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean markLastShiftAsCompleted(ShiftDto shiftDto) {
        try (PreparedStatement pstmt = connection.prepareStatement(END_LAST_SHIFT_SQL)) {

            pstmt.setTimestamp(1, shiftDto.getEndTime() != null ? shiftDto.getEndTime() : null);
            pstmt.setString(2, shiftDto.getReason());
            pstmt.setString(3, shiftDto.getCurrentStatus());
            pstmt.setTimestamp(4, shiftDto.getUpdatedAt() != null ? shiftDto.getUpdatedAt() : null);
            pstmt.setString(5, shiftDto.getShiftId());
            //System.out.println(pstmt);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.error("Error marking last shift as completed: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String findOperatorIdByShiftId(String shiftId) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_OPERATOR_ID_BY_SHIFT_ID_SQL)) {
            pstmt.setString(1, shiftId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("operator_id");
            }
        } catch (SQLException e) {
            Logger.error("Error finding operator ID by shift ID: {}", e.getMessage());
        }
        return null;

    }

    @Override
    public List<ShiftDto> findShiftFrom(Timestamp from) {
        List<ShiftDto> shifts = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_SHIFT_FROM_SQL)) {
            pstmt.setTimestamp(1, from);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ShiftDto shift = mapResultSetToShiftDto(rs);
                shifts.add(shift);
                Logger.debug("Shift found: " + shift);
            }
        } catch (SQLException e) {
            Logger.error("Error finding shifts from timestamp: {}", e.getMessage());
        }
        if (shifts.isEmpty()) {
            Logger.info("No shifts found from the specified timestamp: {}", from);
        } else {
            Logger.info("Found {} shifts from the specified timestamp: {}", shifts.size(), from);
        }
        return shifts;

    }

    @Override
    @Transactional
    public void pushShifts(List<String> shiftIds, String column) {
        // validate column name to prevent SQL injection
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }

        String sql = "UPDATE " + TABLE_NAME + " SET " + column + " = true WHERE shift_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String id : shiftIds) {
                pstmt.setString(1, id);
                pstmt.addBatch();
            }
            int[] updated = pstmt.executeBatch();
            Logger.info("Marked {} for {} shifts", column.toUpperCase(), updated.length);
        } catch (SQLException e) {
            Logger.error("Error marking {} for shifts: {}", column.toUpperCase(), e.getMessage());
            throw new RuntimeException("Failed to mark " + column.toUpperCase() + " for shifts", e);
        }
    }

    @Override
    public List<ShiftDto> findNotPushedShifts(String column) {
        List<ShiftDto> shifts = new ArrayList<>();
        if (!"scu".equalsIgnoreCase(column) && !"ccu".equalsIgnoreCase(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }
        column+=" = false";

        String query="SELECT * FROM " + TABLE_NAME + " WHERE "+column+" ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//            pstmt.setString(1, column);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ShiftDto shift = mapResultSetToShiftDto(rs);
                shifts.add(shift);
                Logger.debug("Shift found: " + shift);
            }
        } catch (SQLException e) {
            Logger.error("Error finding shifts from timestamp: {}", e.getMessage());
        }
        if (shifts.isEmpty()) {
            Logger.info("No shifts found from the specified timestamp: {}");
        } else {
            Logger.info("Found {} shifts from the specified timestamp: {}", shifts.size());
        }
        return shifts;
    }


    private ShiftDto mapResultSetToShiftDto(ResultSet rs) throws SQLException {
        return new ShiftDto(
                rs.getString("shift_id"),
                rs.getString("operator_id"),
                rs.getString("device_id"),
                rs.getString("device_serial"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("start_time"),
                rs.getTimestamp("end_time"),
                rs.getString("serial_no"),
                rs.getString("station_id"),
                rs.getString("line_no"),
                rs.getString("reason"),
                rs.getString("current_status"),
                rs.getTimestamp("update_at"),
                rs.getString("config_version"),
                rs.getString("config_version"),
                rs.getBoolean("ccu"),
                rs.getBoolean("scu")
        );
    }
}