package com.amay.tom.api;

import com.amay.tom.model.ccuRest.TOMPermission;
import com.amay.tom.model.ccuRest.UserProfile;
import com.amay.tom.model.ccuRest.UserStatus;
import com.amay.tom.model.ccuRest.Users;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.model.user.dto.UserPrivilegeDto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserMapper {

    public static UserDto toUserDto(Users user) {
        if (user == null) {
            return null;
        }

        Set<String> roles = new HashSet<>();
        if (user.getRole() != null) {
            roles.add(user.getRole().name()); // assuming `Role` is an enum
        }

        return new UserDto()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setRoles(roles)
                .setEnabled(user.getUserStatus() != null && user.getUserStatus().equals(UserStatus.ACTIVE))
//                .setAccountNonExpired(true)
//                .setCredentialsNonExpired(true)
//                .setAccountNonLocked(true)
//                .setAccountExpiryDate(null)
//                .setCredentialsExpiryDate(null)
//                .setCreatedAt(user.getCreatedAt())
//                .setUpdatedAt(user.getUpdatedAt())
                ;
    }

    public static UserPrivilegeDto toUserPrivilegeDto(UserProfile profile) {
        if (profile == null || profile.getTomPermission() == null) {
            return new UserPrivilegeDto(); // return default privileges
        }

        TOMPermission perm = profile.getTomPermission();

        return new UserPrivilegeDto()
                .setQrTicketIssue(perm.isQrTicketSale())
                .setQrTicketAnalysis(perm.isQrTicketAnalysis())
                .setQrTicketAdjustment(perm.isQrTicketAdjustment())
                .setQrTicketCancellation(perm.isQrTicketCancellation())
                .setQrTicketRefund(perm.isQrTicketRefund())
                .setQrTicketReprint(perm.isQrTicketReprint())
                .setQrTicketReplacement(perm.isQrTicketReplacement())
                .setQrFreeTicket(perm.isQrFreeTicket())
                .setQrPaidTicket(perm.isQrPaidTicket())
                .setTvm(perm.isTvm())
                .setImportAndExport(perm.isImportAndExport())
                .setShutdownAndRestart(perm.isShutdownAndRestart());
    }


}
