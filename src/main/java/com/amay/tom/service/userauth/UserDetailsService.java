package com.amay.tom.service.userauth;

import com.amay.tom.exceptions.QueryNotAppropriateException;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.model.user.UserMapper;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.model.user.dto.UserPrivilegeDto;
import com.amay.tom.model.user.entity.User;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.repository.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDetailsService {

    private final UserRepository userRepository;
    public UserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    public User loadUserByUsername(String username) throws UsernameNotFoundException, QueryNotAppropriateException {
       UserDto userDto = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with User ID: " + username));
//        boolean accountNonExpired = userDto.getAccountExpiryDate() == null || userDto.getAccountExpiryDate().isAfter(LocalDateTime.now());
//        boolean credentialsNonExpired = userDto.getCredentialsExpiryDate() == null || userDto.getCredentialsExpiryDate().isAfter(LocalDateTime.now());
        boolean isEnable = userDto.isEnabled();

        if(!isEnable){
            throw new UsernameNotFoundException("User Disabled: " + username);
        }
        return UserMapper.mapToUser(userDto);
    }

    public UserPrivilege loadUserPrivilege(String username)  {
        try {
            UserPrivilegeDto userPrivilegeDto = userRepository.findUserPrivilegeByUsername(username);

            UserPrivilege userPrivilege = UserMapper.mapToUserPrivilege(userPrivilegeDto);
            return userPrivilege;
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
