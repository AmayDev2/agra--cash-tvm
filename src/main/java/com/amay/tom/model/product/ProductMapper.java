package com.amay.tom.model.product;



    public class ProductMapper {

        public static Product dtoToProduct(ProductDTO dto) {
            if (dto == null) {
                return null;
            }
            Product product = new Product();
            product.setId(dto.getId());
            product.setProductVersion(dto.getProductVersion());
            product.setProductid(dto.getProductid());
            product.setProductName(dto.getProductName());
            product.setFareMediaType(dto.getFareMediaType());
            product.setProductType(dto.getProductType());
            product.setTripCount(dto.getTripCount());
            product.setAdministrationFee(dto.getAdministrationFee());
            product.setOvertravelCharges(dto.getOvertravelCharges());
            product.setMaxStaySameStation(dto.getMaxStaySameStation());
            product.setMaxStayOtherStation(dto.getMaxStayOtherStation());
            product.setTailgatingCharges(dto.getTailgatingCharges());
            product.setOverstayChargesPerHour(dto.getOverstayChargesPerHour());
            product.setTicketlessCharges(dto.getTicketlessCharges());
            product.setMaxOverstayCharges(dto.getMaxOverstayCharges());
            product.setAdjustmentAfterFirstEntry(dto.getAdjustmentAfterFirstEntry());
            product.setMaxTicket(dto.getMaxTicket());
            product.setMinTicket(dto.getMinTicket());
            product.setEntryAfterSale(dto.getEntryAfterSale());
            product.setRefundAfterSale(dto.getRefundAfterSale());
            product.setEntryCount(dto.getEntryCount());
            product.setExitCount(dto.getExitCount());
            product.setMaxAdjustmentLimit(dto.getMaxAdjustmentLimit());
            product.setCreatedBy(dto.getCreatedBy());
            product.setUpdatedBy(dto.getUpdatedBy());
            product.setActive(dto.isActive());
            return product;
        }

        public static ProductDTO productToDto(Product product) {
            if (product == null) {
                return null;
            }
            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            dto.setProductVersion(product.getProductVersion());
            dto.setProductid(product.getProductid());
            dto.setProductName(product.getProductName());
            dto.setFareMediaType(product.getFareMediaType());
            dto.setProductType(product.getProductType());
            dto.setTripCount(product.getTripCount());
            dto.setAdministrationFee(product.getAdministrationFee());
            dto.setOvertravelCharges(product.getOvertravelCharges());
            dto.setMaxStaySameStation(product.getMaxStaySameStation());
            dto.setMaxStayOtherStation(product.getMaxStayOtherStation());
            dto.setTailgatingCharges(product.getTailgatingCharges());
            dto.setOverstayChargesPerHour(product.getOverstayChargesPerHour());
            dto.setTicketlessCharges(product.getTicketlessCharges());
            dto.setMaxOverstayCharges(product.getMaxOverstayCharges());
            dto.setAdjustmentAfterFirstEntry(product.getAdjustmentAfterFirstEntry());
            dto.setMaxTicket(product.getMaxTicket());
            dto.setMinTicket(product.getMinTicket());
            dto.setEntryAfterSale(product.getEntryAfterSale());
            dto.setRefundAfterSale(product.getRefundAfterSale());
            dto.setEntryCount(product.getEntryCount());
            dto.setExitCount(product.getExitCount());
            dto.setMaxAdjustmentLimit(product.getMaxAdjustmentLimit());
            dto.setCreatedBy(product.getCreatedBy());
            dto.setUpdatedBy(product.getUpdatedBy());
            dto.setActive(product.isActive());
            return dto;
        }
        
        public static ProductEntity dtoToEntity(ProductDTO dto){
            if (dto == null) {
                return null;
            }
            ProductEntity productEntity = new ProductEntity();
            productEntity.setId(dto.getId());
            productEntity.setProductVersion(dto.getProductVersion());
            productEntity.setProductid(dto.getProductid());
            productEntity.setProductName(dto.getProductName());
            productEntity.setFareMediaType(dto.getFareMediaType());
            productEntity.setProductType(dto.getProductType());
            productEntity.setTripCount(dto.getTripCount());
            productEntity.setAdministrationFee(dto.getAdministrationFee());
            productEntity.setOvertravelCharges(dto.getOvertravelCharges());
            productEntity.setMaxStaySameStation(dto.getMaxStaySameStation());
            productEntity.setMaxStayOtherStation(dto.getMaxStayOtherStation());
            productEntity.setTailgatingCharges(dto.getTailgatingCharges());
            productEntity.setOverstayChargesPerHour(dto.getOverstayChargesPerHour());
            productEntity.setTicketlessCharges(dto.getTicketlessCharges());
            productEntity.setMaxOverstayCharges(dto.getMaxOverstayCharges());
            productEntity.setAdjustmentAfterFirstEntry(dto.getAdjustmentAfterFirstEntry());
            productEntity.setMaxTicket(dto.getMaxTicket());
            productEntity.setMinTicket(dto.getMinTicket());
            productEntity.setEntryAfterSale(dto.getEntryAfterSale());
            productEntity.setRefundAfterSale(dto.getRefundAfterSale());
            productEntity.setEntryCount(dto.getEntryCount());
            productEntity.setExitCount(dto.getExitCount());
            productEntity.setMaxAdjustmentLimit(dto.getMaxAdjustmentLimit());
            productEntity.setCreatedBy(dto.getCreatedBy());
            productEntity.setUpdatedBy(dto.getUpdatedBy());
            productEntity.setActive(dto.isActive());
            return productEntity;
        }

        public static Product EntityToProduct(ProductEntity entity){
            if (entity == null) {
                return null;
            }
            Product product = new Product();
            product.setId(entity.getId());
            product.setProductVersion(entity.getProductVersion());
            product.setProductid(entity.getProductid());
            product.setProductName(entity.getProductName());
            product.setFareMediaType(entity.getFareMediaType());
            product.setProductType(entity.getProductType());
            product.setTripCount(entity.getTripCount());
            product.setAdministrationFee(entity.getAdministrationFee());
            product.setOvertravelCharges(entity.getOvertravelCharges());
            product.setMaxStaySameStation(entity.getMaxStaySameStation());
            product.setMaxStayOtherStation(entity.getMaxStayOtherStation());
            product.setTailgatingCharges(entity.getTailgatingCharges());
            product.setOverstayChargesPerHour(entity.getOverstayChargesPerHour());
            product.setTicketlessCharges(entity.getTicketlessCharges());
            product.setMaxOverstayCharges(entity.getMaxOverstayCharges());
            product.setAdjustmentAfterFirstEntry(entity.getAdjustmentAfterFirstEntry());
            product.setMaxTicket(entity.getMaxTicket());
            product.setMinTicket(entity.getMinTicket());
            product.setEntryAfterSale(entity.getEntryAfterSale());
            product.setRefundAfterSale(entity.getRefundAfterSale());
            product.setEntryCount(entity.getEntryCount());
            product.setExitCount(entity.getExitCount());
            product.setMaxAdjustmentLimit(entity.getMaxAdjustmentLimit());
            product.setCreatedBy(entity.getCreatedBy());
            product.setUpdatedBy(entity.getUpdatedBy());
            product.setActive(entity.isActive());
            return product;
        }


    }

