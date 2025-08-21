package com.amay.tom.repository;

import com.amay.tom.model.QRTicket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tinylog.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class QRDataArray {

    public static ArrayList<QRTicket> qrDataArray = new ArrayList<>();
    public static void addQRTicket(QRTicket qrTicket) {
        qrDataArray.add(qrTicket);
    }

    public static void createQRTicketFile() {


        // Convert Java object to JSON object using Jackson
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(qrDataArray);

            // Print or use the JSON object as needed
            System.out.println(json);
            // Create a new file

            File file = new File("output.json");

            if(file.exists()){
//                readQRTicketFile();
            }

            // Write the JSON object to the file
            mapper.writeValue(file, qrDataArray);
            Logger.info("QR Ticket data saved to file");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void readQRTicketFile() {
        // Convert JSON string from file to object using Jackson
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Read the JSON file
            File file = new File("output.json");
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String json = stringBuilder.toString();
            // Convert JSON string to object
            QRTicket[] qrTickets = mapper.readValue(json, QRTicket[].class);
            qrDataArray.addAll(Arrays.asList(qrTickets));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
