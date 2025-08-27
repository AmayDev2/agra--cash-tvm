package com.amay.tom.model.siftdata;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


//@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class User {


    private int uId;
    private String userId;
    private String password;
    private String role;

    public User(String userId, String password, String role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

}
