package com.amay.tvm.ups;

import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.ups.command.UPSCommand;
import com.amay.tvm.ups.communication.UPSCommunicationInterface;
import com.amay.tvm.ups.communication.UPSSerialCommunication;
import com.amay.tvm.ups.exception.UPSCommunicationException;
import com.amay.tvm.ups.model.UPSResponse;
import org.tinylog.Logger;

public enum UPS implements UPSInterface {
    INTERFACE;
    private UPSCommunicationInterface upsCommunicationInterface;

    public boolean setupUPS(String comPort){
        try {
            if(upsCommunicationInterface==null) {
                upsCommunicationInterface = new UPSSerialCommunication(comPort);
            }
        } catch (UPSCommunicationException e) {
            Logger.tag(LoggerTag.APP).error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean isConnected() {
        return null!=upsCommunicationInterface && upsCommunicationInterface.isConnected();
    }

    @Override
    public boolean reconnect() {
        try {
            return null!=upsCommunicationInterface && upsCommunicationInterface.reconnect();
        } catch (UPSCommunicationException e) {
            Logger.tag(LoggerTag.APP).info(e.getMessage());
            return false;
        }
    }

    @Override
    public void disconnect() {
        upsCommunicationInterface.disconnect();
    }

    @Override
    public double getIPVoltage() throws UPSCommunicationException {
        if (upsCommunicationInterface == null) {
            throw new UPSCommunicationException("UPS communication interface is null.");
        }
        return upsCommunicationInterface.getIPVoltage();
    }

    @Override
    public UPSResponse getUPSResponseObject() throws UPSCommunicationException {
        if (upsCommunicationInterface == null) {
            throw new UPSCommunicationException("UPS communication interface is null.");
        }
        return upsCommunicationInterface.getUPSResponseObject();
    }

    @Override
    public String fireCommand(UPSCommand command) throws UPSCommunicationException {
        if (upsCommunicationInterface == null) {
            throw new UPSCommunicationException("UPS communication interface is null.");
        }
        return upsCommunicationInterface.fireCommand(command);
    }

    @Override
    public String fireCommand(String commandString) throws UPSCommunicationException {
        if (upsCommunicationInterface == null) {
            throw new UPSCommunicationException("UPS communication interface is null.");
        }
        return upsCommunicationInterface.fireCommand(commandString);
    }
}
