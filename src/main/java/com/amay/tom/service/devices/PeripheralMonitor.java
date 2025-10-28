package com.amay.tom.service.devices;

import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.database.RedisConnectionPool;
import com.amay.tom.enums.ConnectionStatus;
import com.amay.tom.grpc.monotoring.GrpcApiListener;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.helper.Helper;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tvm.coin.model.PollingStatusResponse;
import com.amay.tvm.ups.UPS;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;
import com.fazecast.jSerialComm.SerialPort;
import lombok.Getter;
import org.tinylog.Logger;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PeripheralMonitor implements Runnable {

    @Getter
    private boolean scu_connected;
    @Getter
    private boolean ccu_connected;
    @Getter
    private boolean reader_connected;
    @Getter
    private boolean door_closed;
    @Getter
    private boolean printer_connected;
    @Getter
    private boolean pdu_connected;
    @Getter
    private boolean  tvm_main_module_connected;
    @Getter
    private boolean bnr_connected;
    @Getter
    private boolean ups_connected;
    @Getter
    private boolean ups_on;
    @Getter
    private boolean ohd_connected;
    @Getter
    private boolean upos_connected;
    @Getter
    private int[] deviceStatus,previousDeviceStatus;
    private final GrpcApiListener ccuGrpcApiListener;
    private final GrpcApiListener grpcApiListener;

    private final List<DeviceStatusListener> listeners = new ArrayList<>();

    public PeripheralMonitor(GrpcApiListener ccuGrpcApiListener, GrpcApiListener grpcApiListener) {
        this.ccuGrpcApiListener= ccuGrpcApiListener;
        this.grpcApiListener = grpcApiListener;
    }

    public void addDeviceStatusListener(DeviceStatusListener listener) {
        Logger.tag(LoggerTag.APP).debug("Adding device status listener {} {}", listener, this);
        listeners.add(listener);

    }


    // use this function
    public void removeDeviceStatusListener(DeviceStatusListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void run() {
        deviceStatus = new int[12];
        boolean[] tvm=coinNoduleConnected();
        door_closed = tvm[1]; //door
        printer_connected = getPrinterStatus();
        tvm_main_module_connected=tvm[0];
        bnr_connected = bnrConnected();
        scu_connected = ConnectionStatus.CONNECTED.equals(this.grpcApiListener.getConnectionStatus());
        ccu_connected = ConnectionStatus.CONNECTED.equals(this.ccuGrpcApiListener.getConnectionStatus());
        pdu_connected = poleDisplayConnected();
        boolean[] upsStatus=getUps();
        ups_connected=upsStatus[0];
        ups_on=upsStatus[1]; //if IP<=0 return true UPS Providing IP
        ohd_connected=isOverHeadDisplay();
        upos_connected=isUposConnected();
        reader_connected=isReaderConnected();

        deviceStatus[0] = door_closed ? 1 : 0;
        deviceStatus[1] = printer_connected ? 1 : 0;
        deviceStatus[2] = scu_connected ? 1 : 0;
        deviceStatus[3] = ccu_connected ? 1 : 0;
        deviceStatus[4] = reader_connected ? 1 : 0;
        deviceStatus[5] = pdu_connected ? 1 : 0;
        deviceStatus[6] =  tvm_main_module_connected ? 1 : 0;
        deviceStatus[7] = bnr_connected ? 1 : 0;
        deviceStatus[8] = ohd_connected ? 1 : 0;
        deviceStatus[9] = ups_connected ? 1 : 0;
        deviceStatus[10] = ups_on ? 1 : 0;
        deviceStatus[11] = upos_connected ? 1 : 0;

        Logger.tag(LoggerTag.APP).info("Peripherals status: {}", Helper.ObjectToJson(deviceStatus));

        // notify the all subscribers/listeners
        for (DeviceStatusListener listener : listeners) {
            Logger.tag(LoggerTag.APP).debug("Pushing status to: {} {}", listeners.size(),listener);
            try {
                listener.onDeviceStatusChanged(deviceStatus);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean isReaderConnected() {
        return false;
    }

    private boolean isUposConnected() {
        return false;

    }

    private boolean isOverHeadDisplay() {
        return false;
    }

    private boolean[] getUps() {
        boolean[] upsStatus=new boolean[2];
        try {
            UPSResponse response=UPS.INTERFACE.getUPSResponseObject();
            upsStatus[0]=true;
            upsStatus[1]=response.getInputVoltage()<=0;
        } catch (UPSCommunicationException e) {
                UPS.INTERFACE.reconnect();
                return upsStatus;
        }
        return upsStatus;
    }

    private boolean bnrConnected() {
        return EnvFile.isCashSupported() && BNRIntegration.isConnected();
    }

    private static boolean PRINTER = false;
    public static boolean scannerConnected() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        for (SerialPort serialPort : serialPorts) {
            if (serialPort.getSystemPortName().equals(EnvFile.getComPort())) {
                return true;
            }
        }
        return false;
    }

    public static boolean[] coinNoduleConnected() {
        try {
            PollingStatusResponse pollingStatusResponse=CoinModuleInterface.INSTANCE.pooling();
            return new boolean[]{true,pollingStatusResponse.isMaintenanceDoorOpen()};
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error(e.getMessage());
        }
        return new boolean[]{true,true};
    }

//    CASH_IS_NOT_SUPPORTED


    /**
     * Checks if a USB HID device with given VID and PID is currently connected.
     * @param vid The Vendor ID (e.g., "1EAB")
     * @param pid The Product ID (e.g., "0003")
     * @return true if device is connected, false otherwise
     */
    public static boolean isUsbDeviceConnected(String vid, String pid) {
        try {
            // Construct the deviceId substring to match
            String deviceId = "VID_" + vid.toUpperCase() + "&PID_" + pid.toUpperCase();

            // PowerShell command to get DeviceID(s) matching the pattern
            String psCommand = String.format(
                    "Get-CimInstance -ClassName Win32_PnPEntity | " +
                            "Where-Object { $_.DeviceID -like '*%s*' } | " +
                            "Select-Object -ExpandProperty DeviceID",
                    deviceId.replace("'", "''") // Escape single quotes to avoid PowerShell errors
            );

            // Launch PowerShell process to execute the command
            Process process = Runtime.getRuntime().exec(
                    new String[] { "powershell.exe", "-NoProfile", "-Command", psCommand }
            );

            // Read output lines from PowerShell
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().toUpperCase().contains(deviceId)) {
                    found = true;
                    break;
                }
            }
            reader.close();

            // Wait for the process to exit, and check error stream for troubleshooting if needed
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorMsg = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorMsg.append(line).append(System.lineSeparator());
                }
                errorReader.close();
                System.err.println("PowerShell exited with code " + exitCode + ": " + errorMsg.toString());
                return false;
            }

            return found;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean poleDisplayConnected(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // Get an array of all screen devices (monitors)
        GraphicsDevice[] devices = ge.getScreenDevices();

        // Check the number of devices
        if (devices.length > 1) {
//            //System.out.println("A secondary display is connected.");
            return true;
        } else {
//            //System.out.println("No secondary display detected.");
            return false;
        }
    }

    public static boolean printerConnected() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().equals(EnvFile.getThermalPrinterModel())) {
                PRINTER = true;
                Logger.info("Printer connected {}", printService.getName());
                return true;
            }
        }
        return PRINTER;
    }

    public static boolean getPrinterStatus() {
       return true;// PrinterCommandDispatcher.INSTANCE.isConnected();
    }

    public static boolean getInternetStatus() {
        // Implement the logic to check internet status
        return false;
    }

    static long lastTime = 0;
    static boolean redisStatus = false;

    public static boolean getRedisStatus() {
        if (System.currentTimeMillis() - lastTime > 1000) {
            lastTime = System.currentTimeMillis();
            redisStatus = RedisConnectionPool.isRedisAlive();
        }
        return redisStatus;
    }

    public boolean isUPSUP() {
        boolean[] upsStatus=getUps();
        Logger.tag(LoggerTag.APP).debug("UPS STATUS {}", Arrays.toString(upsStatus));
        return !(upsStatus[0] || upsStatus[1]);
    }
}
