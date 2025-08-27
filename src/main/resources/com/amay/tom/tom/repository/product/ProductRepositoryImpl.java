package com.amay.tom.repository.product;

import com.amay.tom.model.product.ProductEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl extends ProductRepository{

    private final Connection connection;

    public ProductRepositoryImpl(Connection connection) {
        this.connection = connection;
        this.createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Products table", e);
        }
    }

    @Override
    public void insert(ProductEntity productEntity) throws RuntimeException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setString(1, productEntity.getProductVersion());
            preparedStatement.setString(2, productEntity.getProductid());
            preparedStatement.setString(3, productEntity.getProductName());
            preparedStatement.setString(4, productEntity.getFareMediaType());
            preparedStatement.setString(5, productEntity.getProductType());
            preparedStatement.setInt(6, productEntity.getTripCount());
            preparedStatement.setDouble(7, productEntity.getAdministrationFee());
            preparedStatement.setDouble(8, productEntity.getOvertravelCharges());
            preparedStatement.setInt(9, productEntity.getMaxStaySameStation());
            preparedStatement.setInt(10, productEntity.getMaxStayOtherStation());
            preparedStatement.setDouble(11, productEntity.getTailgatingCharges());
            preparedStatement.setDouble(12, productEntity.getOverstayChargesPerHour());
            preparedStatement.setDouble(13, productEntity.getMaxOverstayCharges());
            preparedStatement.setDouble(14, productEntity.getTicketlessCharges());
            preparedStatement.setInt(15, productEntity.getAdjustmentAfterFirstEntry());
            preparedStatement.setInt(16, productEntity.getMaxTicket());
            preparedStatement.setInt(17, productEntity.getMinTicket());
            preparedStatement.setInt(18, productEntity.getEntryAfterSale());
            preparedStatement.setInt(19, productEntity.getRefundAfterSale());
            preparedStatement.setInt(20, productEntity.getEntryCount());
            preparedStatement.setInt(21, productEntity.getExitCount());
            preparedStatement.setInt(22, productEntity.getMaxAdjustmentLimit());
            preparedStatement.setString(23, productEntity.getCreatedBy());
            preparedStatement.setString(24, productEntity.getUpdatedBy());
            preparedStatement.setBoolean(25, productEntity.isActive());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting product into database", e);
        }
    }

    @Override
    public void insertOrUpdateProduct(ProductEntity product) {
        try (PreparedStatement stmt = connection.prepareStatement(UPSERT_SQL)) {
            stmt.setString(1, product.getProductid());
            stmt.setString(2, product.getProductVersion());
            stmt.setString(3, product.getProductName());
            stmt.setString(4, product.getFareMediaType());
            stmt.setString(5, product.getProductType());
            stmt.setInt(6, product.getTripCount());
            stmt.setDouble(7, product.getAdministrationFee());
            stmt.setDouble(8, product.getOvertravelCharges());
            stmt.setInt(9, product.getMaxStaySameStation());
            stmt.setInt(10, product.getMaxStayOtherStation());
            stmt.setDouble(11, product.getTailgatingCharges());
            stmt.setDouble(12, product.getOverstayChargesPerHour());
            stmt.setDouble(13, product.getMaxOverstayCharges());
            stmt.setDouble(14, product.getTicketlessCharges());
            stmt.setInt(15, product.getAdjustmentAfterFirstEntry());
            stmt.setInt(16, product.getMaxTicket());
            stmt.setInt(17, product.getMinTicket());
            stmt.setInt(18, product.getEntryAfterSale());
            stmt.setInt(19, product.getRefundAfterSale());
            stmt.setInt(20, product.getEntryCount());
            stmt.setInt(21, product.getExitCount());
            stmt.setInt(22, product.getMaxAdjustmentLimit());
            stmt.setString(23, product.getCreatedBy());
            stmt.setString(24, product.getUpdatedBy());
            stmt.setTimestamp(25, product.getCreatedAt());
            stmt.setTimestamp(26, product.getUpdatedAt());
            stmt.setBoolean(27, product.isActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting/updating product", e);
        }
    }


    @Override
    public List<ProductEntity> findAll() throws RuntimeException {
        List<ProductEntity> products = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ProductEntity product = new ProductEntity();
                product.setId(resultSet.getInt("id"));
                product.setProductVersion(resultSet.getString("productVersion"));
                product.setProductid(resultSet.getString("productid"));
                product.setProductName(resultSet.getString("productName"));
                product.setFareMediaType(resultSet.getString("fareMediaType"));
                product.setProductType(resultSet.getString("productType"));
                product.setTripCount(resultSet.getInt("tripCount"));
                product.setAdministrationFee(resultSet.getDouble("administrationFee"));
                product.setOvertravelCharges(resultSet.getDouble("overtravelCharges"));
                product.setMaxStaySameStation(resultSet.getInt("maxStaySameStation"));
                product.setMaxStayOtherStation(resultSet.getInt("maxStayOtherStation"));
                product.setTailgatingCharges(resultSet.getDouble("tailgatingCharges"));
                product.setOverstayChargesPerHour(resultSet.getDouble("overstayChargesPerHour"));
                product.setMaxOverstayCharges(resultSet.getDouble("maxOverstayCharges"));
                product.setTicketlessCharges(resultSet.getDouble("ticketlessCharges"));
                product.setAdjustmentAfterFirstEntry(resultSet.getInt("adjustmentAfterFirstEntry"));
                product.setMaxTicket(resultSet.getInt("maxTicket"));
                product.setMinTicket(resultSet.getInt("minTicket"));
                product.setEntryAfterSale(resultSet.getInt("entryAfterSale"));
                product.setRefundAfterSale(resultSet.getInt("refundAfterSale"));
                product.setEntryCount(resultSet.getInt("entryCount"));
                product.setExitCount(resultSet.getInt("exitCount"));
                product.setMaxAdjustmentLimit(resultSet.getInt("maxAdjustmentLimit"));
                product.setCreatedBy(resultSet.getString("createdBy"));
                product.setUpdatedBy(resultSet.getString("updatedBy"));
                product.setCreatedAt(resultSet.getTimestamp("createdAt"));
                product.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
                product.setActive(resultSet.getBoolean("active"));

                products.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all products", e);
        }

        return products;
    }


    @Override
    public ProductEntity findByProductId(String productId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            preparedStatement.setString(1, productId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    ProductEntity product = new ProductEntity();
                    product.setId(rs.getInt("id"));
                    product.setProductVersion(rs.getString("productVersion"));
                    product.setProductid(rs.getString("productid"));
                    product.setProductName(rs.getString("productName"));
                    product.setFareMediaType(rs.getString("fareMediaType"));
                    product.setProductType(rs.getString("productType"));
                    product.setTripCount(rs.getInt("tripCount"));
                    product.setAdministrationFee(rs.getDouble("administrationFee"));
                    product.setOvertravelCharges(rs.getDouble("overtravelCharges"));
                    product.setMaxStaySameStation(rs.getInt("maxStaySameStation"));
                    product.setMaxStayOtherStation(rs.getInt("maxStayOtherStation"));
                    product.setTailgatingCharges(rs.getDouble("tailgatingCharges"));
                    product.setOverstayChargesPerHour(rs.getDouble("overstayChargesPerHour"));
                    product.setMaxOverstayCharges(rs.getDouble("maxOverstayCharges"));
                    product.setTicketlessCharges(rs.getDouble("ticketlessCharges"));
                    product.setAdjustmentAfterFirstEntry(rs.getInt("adjustmentAfterFirstEntry"));
                    product.setMaxTicket(rs.getInt("maxTicket"));
                    product.setMinTicket(rs.getInt("minTicket"));
                    product.setEntryAfterSale(rs.getInt("entryAfterSale"));
                    product.setRefundAfterSale(rs.getInt("refundAfterSale"));
                    product.setEntryCount(rs.getInt("entryCount"));
                    product.setExitCount(rs.getInt("exitCount"));
                    product.setMaxAdjustmentLimit(rs.getInt("maxAdjustmentLimit"));
                    product.setCreatedBy(rs.getString("createdBy"));
                    product.setUpdatedBy(rs.getString("updatedBy"));
                    product.setCreatedAt(rs.getTimestamp("createdAt"));
                    product.setUpdatedAt(rs.getTimestamp("updatedAt"));
                    product.setActive(rs.getBoolean("active"));
                    return product;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching product by ID", e);
        }
        return null; // Not found
    }

    @Override
    public void deleteByProductId(String productId) throws RuntimeException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            preparedStatement.setString(1, productId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("No product found with productId: " + productId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product with productId: " + productId, e);
        }
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_ALL_SQL)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all PeakTimeConfig", e);
        }
    }


}
