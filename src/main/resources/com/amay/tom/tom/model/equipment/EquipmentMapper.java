package com.amay.tom.model.equipment;

import com.amay.tom.model.equipment.dto.EquipmentPrivilegeDto;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;

public class EquipmentMapper {

    public static EquipmentPrivilege mapToEquipmentPrivilege(EquipmentPrivilegeDto equipmentPrivilegeDto) {
        return new EquipmentPrivilege(equipmentPrivilegeDto.isQrTicketIssue(), equipmentPrivilegeDto.isQrTicketAnalysis(),
                equipmentPrivilegeDto.isQrTicketAdjustment(), equipmentPrivilegeDto.isQrTicketCancellation(),
                equipmentPrivilegeDto.isQrTicketRefund(), equipmentPrivilegeDto.isQrTicketReprint(),
                equipmentPrivilegeDto.isQrTicketReplacement(), equipmentPrivilegeDto.isQrFreeTicket(),
                equipmentPrivilegeDto.isQrPaidTicket(), equipmentPrivilegeDto.isTvm());
    }
    public static EquipmentPrivilege mapToEquipmentPrivilege(EquipmentPrivilegeDto equipmentPrivilegeDto, EquipmentPrivilege equipmentPrivilege) {
        equipmentPrivilege.setQrTicketIssue(equipmentPrivilegeDto.isQrTicketIssue());
        equipmentPrivilege.setQrTicketAnalysis(equipmentPrivilegeDto.isQrTicketAnalysis());
        equipmentPrivilege.setQrTicketAdjustment(equipmentPrivilegeDto.isQrTicketAdjustment());
        equipmentPrivilege.setQrTicketCancellation(equipmentPrivilegeDto.isQrTicketCancellation());
        equipmentPrivilege.setQrTicketRefund(equipmentPrivilegeDto.isQrTicketRefund());
        equipmentPrivilege.setQrTicketReprint(equipmentPrivilegeDto.isQrTicketReprint());
        equipmentPrivilege.setQrTicketReplacement(equipmentPrivilegeDto.isQrTicketReplacement());
        equipmentPrivilege.setQrFreeTicket(equipmentPrivilegeDto.isQrFreeTicket());
        equipmentPrivilege.setQrPaidTicket(equipmentPrivilegeDto.isQrPaidTicket());
        equipmentPrivilege.setTvm(equipmentPrivilegeDto.isTvm());
        return equipmentPrivilege;
        
    }
    public static EquipmentPrivilege testMapToEquipmentPrivilege() {
        return new EquipmentPrivilege(true, false, true, true, true, true, true, true, true, true);
    }

}
