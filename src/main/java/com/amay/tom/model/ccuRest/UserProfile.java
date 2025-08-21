package com.amay.tom.model.ccuRest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Serializable {

	private String profileId;
	private String profileName;
	private String description;
	private TOMPermission tomPermission;
//	private AGPermission agPermission;
//	private TVMPermission tvmPermission;
//	private TRPermission trPermission;
//	private SCPermission scPermission;
//	private CCPermission ccPermission;

	
}
