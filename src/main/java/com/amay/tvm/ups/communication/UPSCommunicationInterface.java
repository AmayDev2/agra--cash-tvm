package com.amay.tvm.ups.communication;

import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;

/**
 * UPS Communication Interface
 * Defines the contract for UPS serial communication
 */
public interface UPSCommunicationInterface {

    /**
     * Connect to UPS on specified serial port
     * @param portNumber Serial port identifier (e.g., "COM3" or "/dev/ttyUSB0")
     * @return true if connection successful
     * @throws UPSCommunicationException if connection fails
     */
    boolean connect(String portNumber) throws UPSCommunicationException;

    /**
     * Check if UPS is currently connected
     * @return true if connected and operational
     */
    boolean isConnected();

    /**
     * Reconnect to UPS using last known port
     * @return true if reconnection successful
     * @throws UPSCommunicationException if reconnection fails
     */
    boolean reconnect() throws UPSCommunicationException;

    /**
     * Disconnect from UPS and close serial port
     */
    void disconnect();

    /**
     * Get input voltage from UPS
     * @return Input voltage in volts
     * @throws UPSCommunicationException if query fails
     */
    double getIPVoltage() throws UPSCommunicationException;

    /**
     * Get complete UPS response object with all status fields
     * @return UPSResponse object containing all parsed data
     * @throws UPSCommunicationException if query fails
     */
    UPSResponse getUPSResponseObject() throws UPSCommunicationException;

    /**
     * Fire a command to UPS
     * @param command UPSCommand to execute
     * @return Response string from UPS
     * @throws UPSCommunicationException if command fails
     */
    String fireCommand(UPSCommand command) throws UPSCommunicationException;

    /**
     * Fire a custom command string to UPS
     * @param commandString Raw command string (e.g., "T05", "S02R0100")
     * @return Response string from UPS
     * @throws UPSCommunicationException if command fails
     */
    String fireCommand(String commandString) throws UPSCommunicationException;
}
