package com.amay.tom.test;

import javax.usb.*;
import java.util.List;

public class USBConnectionExample {

    public static void main(String[] args) {
        try {
            // Get the USB services
            UsbServices services = UsbHostManager.getUsbServices();
            //System.out.println("USB Service Implementation: " + services.getImpDescription());

            // Get the root USB hub
            UsbHub rootHub = services.getRootUsbHub();
            listDevices(rootHub);

        } catch (UsbException e) {
            e.printStackTrace();
        }
    }

    // Recursive method to list all connected USB devices
    private static void listDevices(UsbHub hub) {
        List<UsbDevice> devices = (List<UsbDevice>) hub.getAttachedUsbDevices();

        for (UsbDevice device : devices) {
            //System.out.println("Device: " + device);

            // Check if the device is a hub and list its devices
            if (device.isUsbHub()) {
                listDevices((UsbHub) device);
            }
        }
    }
}
