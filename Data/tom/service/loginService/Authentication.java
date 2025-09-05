package com.amay.tom.service.loginService;


// @Depricated
public interface Authentication {
    boolean authenticate(String username, String password);

    void logout();

    boolean resumeShutDownShift(String password);

}
