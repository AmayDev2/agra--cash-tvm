package com.amay.tom.utils.folder;

import com.amay.tom.utils.env.EnvFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewFolder {

    // Base path relative to project root
//    private static final String BASE_PATH = "tickets";


    public static String getFolderName(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    public  static String createTodayFolder(){

        // Create the folder path
        String folderPath = EnvFile.getTicketImagesFolder() + File.separator + getFolderName();

        // Check if the folder already exists
        File folder = new File(folderPath);
        if (!folder.exists()) {
            // If the folder doesn't exist, create it
            if (folder.mkdirs()) {
                System.out.println("Folder created: " + folderPath);
            } else {
                System.out.println("Failed to create folder: " + folderPath);
            }
        } else {
            System.out.println("Folder already exists: " + folderPath);
        }

        return folderPath;
    }

    public static BufferedImage findTicket(String ticketId) {
        if (ticketId != null) {
            File ticketsDirectory = new File( EnvFile.getTicketImagesFolder());
            if (!ticketsDirectory.exists() || !ticketsDirectory.isDirectory()) {
                System.err.println("Tickets directory not found or is not a directory.");
                return null;
            }

            File[] ticketDirectories = ticketsDirectory.listFiles(File::isDirectory);
            if (ticketDirectories == null || ticketDirectories.length == 0) {
                System.err.println("No ticket directories found.");
                return null;
            }


            for (File ticketDirectory : ticketDirectories) {
                File ticketImage = new File(ticketDirectory, ticketId + ".png");
                if (ticketImage.exists() && ticketImage.isFile()) {
                    try {
                        return ImageIO.read(ticketImage);
                    } catch (IOException e) {
                        System.err.println("Error reading ticket image: " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("Ticket image not found for ticket ID: " + ticketId);
        return null;
    }
}
