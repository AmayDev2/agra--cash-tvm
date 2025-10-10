package com.amay.tom.api;

import com.amay.tom.model.ccuRest.TVMPermission;
import com.amay.tom.model.ccuRest.UserProfile;
import com.amay.tom.model.ccuRest.UserStatus;
import com.amay.tom.model.ccuRest.Users;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.model.user.dto.UserPrivilegeDto;

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
        if (profile == null || profile.getTvmPermission() == null) {
            return new UserPrivilegeDto(); // return default privileges
        }

        TVMPermission perm = profile.getTvmPermission();

        return new UserPrivilegeDto()
                .setBnrCashAdd(perm.isBnrCashAdd())
                .setBnrTesting(perm.isBnrTesting())
                .setBnrMoveCash(perm.isBnrMoveCash())
                .setCheckAvailableCash(perm.isCheckAvailableCash())
                .setCoinRefill(perm.isCoinRefill())
                .setCoinModuleTesting(perm.isCoinModuleTesting())
                .setCoinDumping(perm.isCoinDumping())
                .setPeripheralTest(perm.isPeripheralTest())
                .setConfiguration(perm.isConfiguration())
                .setModeSettings(perm.isModeSettings())
                .setVersionCheck(perm.isVersionCheck())
                .setImportAndExport(perm.isImportAndExport())
                .setShutdownAndRestart(perm.isShutdownAndRestart());
    }


}
