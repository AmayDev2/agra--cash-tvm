package com.amay.tvm.ohdSerial;

import java.io.*;
import java.util.*;

public class IniHelper {

    private static final String INI_FILE = "EQ2008_Dll_Set.ini";

    public static void updateBaudRate(int baudRate) {
        try {
            File file = new File(INI_FILE);
            if (!file.exists()) return;

            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.toLowerCase().startsWith("baud=")) {
                        line = "Baud=" + baudRate;
                    }
                    lines.add(line);
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            }

            System.out.println("✔ Baud rate updated to " + baudRate);

        } catch (Exception e) {
            System.out.println("❌ Could not update INI: " + e.getMessage());
        }
    }
}
