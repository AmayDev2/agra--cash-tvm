package com.amay.tom.service.devices.device;

import com.amay.tom.utils.env.EnvFile;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrinterStatus {



    public static boolean getPrinterStatus() {
        boolean printerStatusBool = false;
        try {
            final String expectedStatus = "3"; // 3 = Idle

            String printerName = EnvFile.getThermalPrinterModel();
            // Example: printerName = "Posiflex PP8802 Printer";

            // Build PowerShell command to get PrinterStatus of the printer with given name
            String psCommand = String.format(
                    "Get-CimInstance -ClassName Win32_Printer | Where-Object { $_.Name -eq '%s' } | Select-Object -ExpandProperty PrinterStatus",
                    printerName.replace("'", "''") // escape single quotes
            );

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-Command",
                    psCommand
            );

            Process process = processBuilder.start();

            // Read standard output from PowerShell command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.matches("\\d+")) {
                    // If output is a number, consider as printer status code
                    printerStatusBool = expectedStatus.equals(line);
                    break;
                }
            }

            // Check for errors (stderr)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append(System.lineSeparator());
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Logger.error("PowerShell command exited with code " + exitCode + ". Errors: " + errorOutput);
            } else if (errorOutput.length() > 0) {
                Logger.error("PowerShell command error output: " + errorOutput);
            }
        } catch (IOException | InterruptedException e) {
            Logger.error("Error executing PowerShell command: " + e.getMessage());
        }

        return printerStatusBool;
    }



}
