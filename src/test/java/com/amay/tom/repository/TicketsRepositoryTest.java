//package com.amay.tom.repository;
//
//import com.amay.tom.database.DatabaseConnector;
//import com.amay.tom.database.SQLiteConnection;
//import com.amay.tom.model.QRTicket;
//import com.amay.tom.utils.env.EnvFile;
//import org.junit.jupiter.api.Test;
//
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TicketsRepositoryTest {
//
//    @Test
//    void getTickets() throws SQLException, InterruptedException {
//        EnvFile.loadEnv();
//        QRTicket ticket = new QRTicket("000092", "2024-04-01 03:27:57", "2024-03-31 22:57:57", "CUFFE PARADE", "GRANT ROAD METRO", "SJT", "Card", "30", null);
//        ticket.setQrCodeData("MjQ2NDE6MDAwMDAyOjE3MTE5MjIyNzc6MTcxMTkyNTg3NzowMDMwOjAxOjA4OjAxOjAx");
//        TicketsRepository.getInstance().insertTicket(SQLiteConnection.INSTANCE.getConnection(), ticket);
//
//    }
//
//}