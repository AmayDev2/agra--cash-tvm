package com.amay.tom.service.usb;

import javax.usb.*;

public class USBPortDetector {
    public static void detectUSB() throws UsbException {
        // Get the USB services
        UsbServices services = null;
        try {
            services = UsbHostManager.getUsbServices();
        } catch (UsbException e) {
            e.printStackTrace();
            return;
        }

        // List all USB devices
        UsbHub rootHub = services.getRootUsbHub();
        listDevices(rootHub);
    }

    private static void listDevices(UsbHub hub) {
        @SuppressWarnings("unchecked")
        UsbDevice device;
        for (UsbDevice usbDevice : (Iterable<UsbDevice>) hub.getAttachedUsbDevices()) {
            device = usbDevice;
            if (device.isUsbHub()) {
                // If this is a hub, recursively list devices
                listDevices((UsbHub) device);
            } else {
                // If this is a device, print its information
                //System.out.println("Device found: " + device);
            }
        }
    }
}

