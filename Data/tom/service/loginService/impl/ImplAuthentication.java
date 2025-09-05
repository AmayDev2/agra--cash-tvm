package com.amay.tom.service.loginService.impl;

import com.amay.tom.database.DatabaseConnector;
import com.amay.tom.repository.DBUserRepo;
import com.amay.tom.service.loginService.Authentication;

import java.sql.SQLException;

public class ImplAuthentication implements Authentication {
    @Override
    public boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public boolean resumeShutDownShift(String password) {
        try{

            return DBUserRepo.getInstance().checkPassword(DatabaseConnector.getConnection(),password);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
