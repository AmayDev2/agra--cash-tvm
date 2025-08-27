package com.amay.tom.repository.version;

import com.amay.tom.model.version.MasterConfigInfoEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VersionRepositoryImpl extends VersionRepository{
    private final Connection connection;

    public VersionRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

     private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table " + TABLE_NAME, e);
        }
    }

    @Override
    public void insert(MasterConfigInfoEntity entity) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, entity.getConfigVer());
            ps.setString(2, entity.getTomConfig());
            ps.setString(3, entity.getAgConfig());
            ps.setString(4, entity.getTvmConfig());
            ps.setString(5, entity.getTrConfig());
            ps.setString(6, entity.getTicketConfig());
            ps.setString(7, entity.getBusinessDayVer());
            ps.setString(8, entity.getPeakTimeVer());
            ps.setString(9, entity.getCalenderConfig());
            ps.setString(10, entity.getFareConfig());
            ps.setString(11, entity.getTopologyConfig());
            ps.setString(12, entity.getUserVer());
            ps.setString(13, entity.getProfileVer());
            ps.setString(14, entity.getTomSwVer());
            ps.setString(15, entity.getAgSwVer());
            ps.setString(16, entity.getTvmSwVer());
            ps.setString(17, entity.getTrSwVer());
            ps.setString(18, entity.getScSwVer());
            ps.setString(19,entity.getProductConfig());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting MasterConfigInfo", e);
        }
    }


    @Override
    public MasterConfigInfoEntity findByConfigVer(String configVer) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setString(1, configVer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding MasterConfigInfo by configVer", e);
        }
        return null;
    }

    @Override
    public List<MasterConfigInfoEntity> findAll() {
        List<MasterConfigInfoEntity> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all MasterConfigInfo records", e);
        }
        return list;
    }

    @Override
    public void deleteByConfigVer(String configVer) {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            ps.setString(1, configVer);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting MasterConfigInfo", e);
        }
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_ALL_SQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all MasterConfigInfo records", e);
        }
    }


    private MasterConfigInfoEntity mapRow(ResultSet rs) throws SQLException {
        MasterConfigInfoEntity entity = new MasterConfigInfoEntity();
        entity.setConfigVer(rs.getString("configVer"));
        entity.setTomConfig(rs.getString("tomConfig"));
        entity.setAgConfig(rs.getString("agConfig"));
        entity.setTvmConfig(rs.getString("tvmConfig"));
        entity.setTrConfig(rs.getString("trConfig"));
        entity.setTicketConfig(rs.getString("ticketConfig"));
        entity.setBusinessDayVer(rs.getString("businessDayVer"));
        entity.setPeakTimeVer(rs.getString("peakTimeVer"));
        entity.setCalenderConfig(rs.getString("calenderConfig"));
        entity.setFareConfig(rs.getString("fareConfig"));
        entity.setTopologyConfig(rs.getString("topologyConfig"));
        entity.setUserVer(rs.getString("userVer"));
        entity.setProfileVer(rs.getString("profileVer"));
        entity.setTomSwVer(rs.getString("tomSwVer"));
        entity.setAgSwVer(rs.getString("agSwVer"));
        entity.setTvmSwVer(rs.getString("tvmSwVer"));
        entity.setTrSwVer(rs.getString("trSwVer"));
        entity.setScSwVer(rs.getString("scSwVer"));
        entity.setProductConfig(rs.getString("productConfig"));
        return entity;
    }
}
