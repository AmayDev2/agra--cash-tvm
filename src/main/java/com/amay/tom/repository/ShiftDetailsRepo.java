package com.amay.tom.repository;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.siftdata.ShiftDetail;
import com.amay.tom.utils.time.TimeUtil;
import org.tinylog.Logger;

import java.sql.*;

public class ShiftDetailsRepo {
    private static ShiftDetailsRepo instance = new ShiftDetailsRepo();

    private ShiftDetailsRepo(){}

    public static ShiftDetailsRepo getInstance(){
        return instance;
    }

    public void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS shift_details (" +
                "shift_id VARCHAR(255) PRIMARY KEY AUTO_INCREMENT," +
                "shift_start Date NOT NULL," +
                "shift_end Date NOT NULL," +
                "station_id VARCHAR(255) NOT NULL," +
                "equipment_id VARCHAR(255) NOT NULL," +
                "operator_id VARCHAR(255) NOT NULL," +
                "shift_status VARCHAR(255) NOT NULL" +
                ")";


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            System.out.println("Shift details table created successfully.");
        }catch (Exception e){
            Logger.error("Shift details table creation {}",e.getMessage());
        }
    }

    public void dropTable(Connection connection) throws SQLException {
        String sql = "DROP TABLE IF EXISTS shift_details";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            System.out.println("Shift details table dropped successfully.");
        }catch (Exception e){
            Logger.error("Shift details table drop {}",e.getMessage());
        }
    }



    public int createShiftDetails(Connection connection, ShiftDetail shiftDetail) throws SQLException {
        String sql = "INSERT INTO shift_details (station_id, equipment_id, operator_id, shift_status) VALUES (?, ?, ?, ?)";
        int generatedKey = -1;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, shiftDetail.getStationId());
            statement.setString(2, shiftDetail.getEquipmentId());
            statement.setString(3, shiftDetail.getOperatorId());
            statement.setString(4, shiftDetail.getShiftStatus());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedKey = generatedKeys.getInt(1); // Assuming shift_id is an integer
                    }
                }
                System.out.println("Shift details created successfully.");
            } else {
                System.out.println("Failed to create shift details.");
            }
        } catch (Exception e) {
            Logger.error("Shift details creation {}", e.getMessage());
        }
        return generatedKey;
    }

    public void updateShiftDetails(Connection connection, ShiftDetail shiftDetail) throws SQLException {
        String sql = "UPDATE shift_details SET shift_start = ?, shift_end = ?, station_id = ?, equipment_id = ?, operator_id = ?, shift_status = ? WHERE shift_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, shiftDetail.getShiftStart());
            statement.setTimestamp(2,  shiftDetail.getShiftEnd());
            statement.setString(3, shiftDetail.getStationId());
            statement.setString(4, shiftDetail.getEquipmentId());
            statement.setString(5, shiftDetail.getOperatorId());
            statement.setString(6, shiftDetail.getShiftStatus());
            statement.setInt(7, shiftDetail.getShiftId());
            statement.executeUpdate();
            System.out.println("Shift details updated successfully.");
        }catch (Exception e){
            Logger.error("Shift details update {}",e.getMessage());
        }
    }



    public ShiftDetail updateShiftStatus(Connection connection, int shiftId, String shiftStatus) throws SQLException {
        String sql = "UPDATE shift_details SET shift_status = ? WHERE shift_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, shiftStatus);
            statement.setInt(2, shiftId);
            statement.executeUpdate();
            System.out.println("Shift status updated successfully.");
            return readShiftDetails(connection, shiftId);
        }catch (Exception e){
            Logger.error("Shift status update {}",e.getMessage());
            return null;
        }
    }


//    public void updateShiftByShiftId(Connection connection, ShiftDetail shiftDetail){
//        String sql = "UPDATE shift_details SET shift_start = ?, shift_end = ?, station_id = ?, equipment_id = ?, operator_id = ?, shift_status = ? WHERE shift_id = ?";
//        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setDate(1, (Date) shiftDetail.getShiftStart());
//            statement.setDate(2, (Date) shiftDetail.getShiftEnd());
//            statement.setString(3, shiftDetail.getStationId());
//            statement.setString(4, shiftDetail.getEquipmentId());
//            statement.setString(5, shiftDetail.getOperatorId());
//            statement.setString(6, shiftDetail.getShiftStatus());
//            statement.setInt(7, shiftDetail.getShiftId());
//            statement.executeUpdate();
//            System.out.println("Shift details updated successfully.");
//        }catch (Exception e){
//            Logger.error("Shift details update {}",e.getMessage());
//        }
//    }

    public void deleteShiftDetails(Connection connection, String shiftId) throws SQLException {


        String sql = "DELETE FROM shift_details WHERE shift_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setString(1, shiftId);
//            statement.executeUpdate();
            System.out.println("Shift details deleted successfully.");
        }catch (Exception e){
            Logger.error("Shift details deletion {}",e.getMessage());
        }
    }

    public ShiftDetail readShiftDetails(Connection connection, int shiftId) throws SQLException {
        Logger.info("Reading shift details with the given shift id. {}", shiftId);
        String sql = "SELECT * FROM shift_details WHERE shift_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shiftId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    ShiftDetail shiftDetail = new ShiftDetail();
                    shiftDetail.setShiftId(resultSet.getInt("shift_id"));
                    shiftDetail.setShiftStart(resultSet.getTimestamp("shift_start"));
                    shiftDetail.setShiftEnd(resultSet.getTimestamp("shift_end"));
                    shiftDetail.setStationId(resultSet.getString("station_id"));
                    shiftDetail.setEquipmentId(resultSet.getString("equipment_id"));
                    shiftDetail.setOperatorId(resultSet.getString("operator_id"));
                    shiftDetail.setShiftStatus(resultSet.getString("shift_status"));
                    return shiftDetail;
                } else {
                    Logger.info("No shift details found with the given shift id. {}", shiftId);
                    return null;
                }
            }
        }
    }


    public ShiftDetail completeShiftDetails(Connection connection, int shiftId) {
        String sql = "UPDATE shift_details SET shift_end = ?, shift_status = ? WHERE shift_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, new Timestamp(TimeUtil.getCurrentDateTimeInLong()));
            statement.setString(2, "completed");
            statement.setInt(3, shiftId);

            int affected = statement.executeUpdate();

            if (affected>0) {
                return this.readShiftDetails(connection, shiftId);

            } else {
                // If no rows were updated
                Logger.error("No rows were updated.");
                return null;
            }
        } catch (Exception e) {
            Logger.error("Error updating shift details: {}", e.getMessage());
            return null;
        }

    }

    public ShiftDetail shiftLogout(Connection connection, int shiftId, String logoutType) {
        String sql = "UPDATE shift_details SET shift_end = ?, shift_status = ? WHERE shift_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, new Timestamp(TimeUtil.getCurrentDateTimeInLong()));
            statement.setString(2, logoutType);
            statement.setInt(3, shiftId);

            int affected = statement.executeUpdate();

            if (affected>0) {
                return this.readShiftDetails(connection, shiftId);

            } else {
                // If no rows were updated
                Logger.error("No rows were updated.");
                return null;
            }
        } catch (Exception e) {
            Logger.error("Error updating shift details: {}", e.getMessage());
            return null;
        }

    }

    public ShiftDetail getLastShiftDetails(Connection connection)  {
        String sql = "SELECT * FROM shift_details WHERE equipment_id= ? ORDER BY shift_id DESC LIMIT 1";
          try(PreparedStatement preparedStatement=connection.prepareStatement(sql)){
              preparedStatement.setString(1, SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
                try(ResultSet resultSet=preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        ShiftDetail shiftDetail=new ShiftDetail();
                        shiftDetail.setShiftId(resultSet.getInt("shift_id"));
                        shiftDetail.setShiftStart(resultSet.getTimestamp("shift_start"));
                        shiftDetail.setShiftEnd(resultSet.getTimestamp("shift_end"));
                        shiftDetail.setStationId(resultSet.getString("station_id"));
                        shiftDetail.setEquipmentId(resultSet.getString("equipment_id"));
                        shiftDetail.setOperatorId(resultSet.getString("operator_id"));
                        shiftDetail.setShiftStatus(resultSet.getString("shift_status"));
                        return shiftDetail;
                    }else {
                        Logger.info("No shift details found with the given shift id. {}", "equipment_id");
                        return null;
                    }
                }catch (Exception e){
                    Logger.error("Error reading shift details: {}",e.getMessage());
                    return null;
                }
            }catch (SQLException e){
              Logger.error("Error reading shift details: {}",e.getMessage());
              return null;
          }

    }
}
