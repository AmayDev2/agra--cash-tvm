package com.amay.tom.repository.fareTable;

import com.amay.tom.model.faretable.FareRowEntity;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FareTableRepositoryImpl extends FareTableRepository {

    private final Connection connection;

    public FareTableRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Fare table", e);
        }
    }

    @Override
    public void insert(FareRowEntity fareRowEntity) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, fareRowEntity.getSource());
            stmt.setString(2, fareRowEntity.getDestination());
            stmt.setDouble(3, fareRowEntity.getFareAmount());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting fare", e);
        }
    }


    @Override
    public void insertOrUpdate(FareRowEntity fareEntity) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPSERT_SQL)) {
            pstmt.setString(1, fareEntity.getSource());
            pstmt.setString(2, fareEntity.getDestination());
            pstmt.setDouble(3, fareEntity.getFareAmount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting/updating fare", e);
        }
    }

    @Override
    public FareRowEntity findById(String source, String destination) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setString(1, source);
            pstmt.setString(2, destination);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new FareRowEntity(
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDouble("fare_amount")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding fare", e);
        }
    }

    @Override
    public int getUniqueSourceCount(){
        try(PreparedStatement psmt = connection.prepareStatement(GET_UNIQUE_SOURCE)){
            ResultSet rs = psmt.executeQuery();
            if(rs.next()) {
                return rs.getInt("source_count");
            }
        } catch (SQLException e) {
            Logger.error("Station Count is 0");
        }
        return 0;
    }

    @Override
    public List<FareRowEntity> findAll() {
        List<FareRowEntity> fares = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL);
            while (rs.next()) {
                fares.add(new FareRowEntity(
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDouble("fare_amount")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all fares", e);
        }
        return fares;
    }

    @Override
    public void deleteAll() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(DELETE_ALL_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all fares", e);
        }
    }
}

