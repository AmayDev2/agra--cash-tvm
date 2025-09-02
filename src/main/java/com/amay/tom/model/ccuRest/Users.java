package com.amay.tom.model.ccuRest;

import com.amay.tom.model.ccuRest.Role;
import com.amay.tom.model.ccuRest.UserProfile;
import com.amay.tom.model.ccuRest.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Users {

	private long id;

	private String username;

	private String password;

	private String firstName;

	private String lastName;

	private Role role;


	private UserStatus status;

	private UserProfile userProfile;

//	private LocalDateTime updatedAt;
//
//	private LocalDateTime lastLogin;


//	private LocalDateTime createdAt;

	public UserStatus getUserStatus() {
		return status;
	}

	public void setUserStatus(UserStatus status) {
		this.status = status;
	}


}