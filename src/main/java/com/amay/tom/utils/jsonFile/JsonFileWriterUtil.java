package com.amay.tom.utils.jsonFile;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class JsonFileWriterUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // pretty print

    /**
     * Writes any object to a JSON file.
     *
     * @param object the object to be serialized
     * @param filePath the destination file path (e.g., "output/users.json")
     */
    public static void writeToJsonFile(Object object, String filePath) {
        try {
            File file = new File(filePath);
            // Create parent directories if they don't exist
            file.getParentFile().mkdirs();
            objectMapper.writeValue(file, object);
            //System.out.println("JSON saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write JSON to file: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Writes the current station data to a JSON file.
     */
    public static Object readFileToJsonObject(String filePath,Class<?> className) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return objectMapper.readValue(file, className);
            } else {
                System.err.println("File not found: " + filePath);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Failed to read JSON from file: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
}
