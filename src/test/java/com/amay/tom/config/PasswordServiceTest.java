package com.amay.tom.config;

import com.amay.tom.database.SQLConnector;
import com.amay.tom.model.user.dto.UserDto;
import com.amay.tom.repository.user.UserRepository;
import com.amay.tom.repository.user.UserRepositoryImpl;
import com.amay.tom.utils.env.EnvLoader;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.repository.NoteAmountRepository;
import com.amay.tvm.backend.repository.NoteAmountRepositoryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    private final PasswordService passwordService=new PasswordService();
    private static UserRepository userRepository;


    @AfterAll
    static void cleanup() {

    }

    @BeforeAll
    static void init() {
        try {
            EnvLoader envLoader = new EnvLoader(ENVURL.CONFIG + ".env");
            String dbUrl = envLoader.getDatabaseUrl();
            String dbUsername = envLoader.getDatabaseUsername();
            String dbPassword = envLoader.getDatabasePassword2();
            int noOfConnections = envLoader.getSQLiteDatabaseConnections();

            SQLConnector sqlConnector = new SQLConnector(dbUrl, dbUsername, dbPassword, noOfConnections);
            sqlConnector.setConnection();

            userRepository= new UserRepositoryImpl(sqlConnector.getConnection());

            Logger.tag(LoggerTag.APP).info("NoteAmountRepository initialized");

        } catch (Exception e) {
            fail("Initialization failed: " + e.getMessage());
        }
    }

    @Test
    void hashPassword() {

    }

    @Test
    void verifyPassword() {
        String pass="123456";
        String hs=passwordService.hashPassword(pass);

        UserDto userDto=userRepository.findByUsername("UPMRC000005").orElse(new UserDto());
        String d=userDto.getPassword();
        System.out.println("TEST "+d+" " +hs.equals(d));

        boolean isMatched=passwordService.verifyPassword(d,pass);
        System.out.println(hs+"  "+isMatched);

    }

    @Test
    void close() {
    }
}