package com.amay.tom.repository.equipment;

import com.amay.tom.model.equipment.entity.Equipment;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EquipmentRepositoryImpl extends EquipmentRepository {

    private final Connection connection;

    public EquipmentRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating equipment table", e);
        }
    }

    @Override
    public void insertIfEmpty(Equipment equipment) {
        if(get()==null) {
            try (PreparedStatement pstmt = connection.prepareStatement(UPSERT_SQL)) {
                pstmt.setString(1, "singleton"); // fixed key ensures only 1 row
                pstmt.setString(2, equipment.getEquipmentId());
                pstmt.setString(3, equipment.getLineId());
                pstmt.setString(4, equipment.getStationId());
                pstmt.setString(5, equipment.getEquipmentTypeId());
                pstmt.setString(6, equipment.getEquipmentSerial());
                pstmt.setString(7, equipment.getEquipmentName());
                pstmt.setString(8, equipment.getEquipmentIp());
                pstmt.setString(9, equipment.getStatus());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error inserting/updating equipment", e);
            }
        }else {
            Logger.warn("Equipment row already exists — cannot update");
        }
    }

    @Override
    public Equipment get() {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_SQL)) {
            pstmt.setString(1, "singleton");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Equipment(
                        rs.getString("line_id"),
                        rs.getString("station_id"),
                        rs.getString("equipment_type_id"),
                        rs.getString("equipment_serial"),
                        rs.getString("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("equipment_ip"),
                        rs.getString("status")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching equipment", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(DELETE_ALL_SQL);
        } catch (SQLException e) {
            Logger.error("Error deleting all equipment rows", e);
        }
    }
}