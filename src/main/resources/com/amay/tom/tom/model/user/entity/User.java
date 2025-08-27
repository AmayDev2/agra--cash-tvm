package com.amay.tom.model.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User{
    private String username;
    private String password;
    private Set<String> roles;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private LocalDateTime accountExpiryDate;
    private LocalDateTime credentialsExpiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}