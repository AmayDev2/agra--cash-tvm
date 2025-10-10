package com.amay.tom.model.user;

import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.model.user.dto.UserPrivilegeDto;
import com.amay.tom.model.user.entity.User;
import com.amay.tom.model.user.entity.UserPrivilege;

public class UserMapper {

    public static UserPrivilege mapToUserPrivilege(UserPrivilegeDto userPrivilegeDto) {
        return new UserPrivilege(
                userPrivilegeDto.isBnrCashAdd(),
                userPrivilegeDto.isBnrTesting(),
                userPrivilegeDto.isBnrMoveCash(),
                userPrivilegeDto.isCheckAvailableCash(),
                userPrivilegeDto.isCoinRefill(),
                userPrivilegeDto.isCoinModuleTesting(),
                userPrivilegeDto.isCoinDumping(),
                userPrivilegeDto.isPeripheralTest(),
                userPrivilegeDto.isConfiguration(),
                userPrivilegeDto.isModeSettings(),
                userPrivilegeDto.isVersionCheck(),
                userPrivilegeDto.isImportAndExport(),
                userPrivilegeDto.isShutdownAndRestart()
        );
    }
    public static UserPrivilege testMapToUserPrivilege() {
        return new UserPrivilege(true, true, true, true, true, true, true, true, true, true,true,true,true);
    }

    public static User mapToUser(UserDto userDto) {
        return new User(userDto.getUsername(), userDto.getPassword(), userDto.getRoles(), userDto.isEnabled(),
                userDto.isAccountNonExpired(), userDto.isCredentialsNonExpired(), userDto.isAccountNonLocked(),
                userDto.getAccountExpiryDate(), userDto.getCredentialsExpiryDate(), userDto.getCreatedAt(),
                userDto.getUpdatedAt());
    }

    public static UserDto mapToUserDto(User userDto){
        return new UserDto(userDto.getUsername(), userDto.getPassword(), userDto.getRoles(), userDto.isEnabled(),
                userDto.isAccountNonExpired(), userDto.isCredentialsNonExpired(), userDto.isAccountNonLocked(),
                userDto.getAccountExpiryDate(), userDto.getCredentialsExpiryDate(), userDto.getCreatedAt(),
                userDto.getUpdatedAt());
    }
}
