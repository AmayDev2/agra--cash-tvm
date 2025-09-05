package com.amay.tom.systemcontrole;

import javafx.application.Platform;

import java.io.IOException;

public class SystemControl {

    /**
     * Restarts the system.
     *
     * @return
     */
    public boolean restartSystem() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // For Windows
                Runtime.getRuntime().exec("shutdown -r -t 2");
            } else if (os.contains("mac")) {
                // For macOS
                Runtime.getRuntime().exec("sudo shutdown -r now");
            } else if (os.contains("nix") || os.contains("nux")) {
                // For Unix/Linux
                Runtime.getRuntime().exec("shutdown -r now");
            } else {
                throw new UnsupportedOperationException("Unsupported operating system.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Shuts down the system.
     *
     * @return
     */
    public boolean shutdownSystem() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // For Windows
                Runtime.getRuntime().exec("shutdown -s -t 2");
            } else if (os.contains("mac")) {
                // For macOS
                Runtime.getRuntime().exec("sudo shutdown -h now");
            } else if (os.contains("nix") || os.contains("nux")) {
                // For Unix/Linux
                Runtime.getRuntime().exec("shutdown -h now");
            } else {
                throw new UnsupportedOperationException("Unsupported operating system.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void closeApplication(){
        Platform.exit();
        System.exit(111); // Forcefully terminates the application
    }

}
