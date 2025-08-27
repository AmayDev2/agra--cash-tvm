package com.amay.tom.utils.files;

import org.tinylog.Logger;

import java.io.*;

public class FileSerializeService {
    public static void saveData(String fileName,Object object, String path) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path +fileName))) {
            oos.writeObject(object);
            Logger.info("Data saved successfully.");
        } catch (IOException e) {
            Logger.error("Error saving Last updated file: {}", e.getMessage());
        }
    }

    public static Object retrieveData(String fileName, String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path+fileName))) {
            Logger.info("Data retrieved successfully.");
            return ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            Logger.error("Error retrieving data from file: {}", e.getMessage());
        }
        return null;
    }
}
