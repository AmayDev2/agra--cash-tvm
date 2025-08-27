package com.amay.tom.service.print;

import com.fazecast.jSerialComm.SerialPort;
public class Portchecker
{
   public static void checkPort()
  {
    SerialPort [] AvailablePorts = SerialPort.getCommPorts();
      //      Open the first Available port

      // use the for loop to print the available serial ports
        for(SerialPort S : AvailablePorts) {
            System.out.println("\n  " + S.toString());
        }

//      SerialPort MySerialPort = AvailablePorts[1];
//
//      MySerialPort.openPort();    //open the port
//
//
//
//        if (MySerialPort.isOpen()) {
//        	System.out.println("OPEN "+ MySerialPort.getSystemPortName());        	//Check whether port open/not
////            ImplPrintTicket PrintHelper = null;
////        ImplPrintTicket.printHelloWorld();
//        }else {
//        	System.out.println(" Port not open ");
//        }
//        MySerialPort.closePort(); //Close the port
//
//        if (MySerialPort.isOpen()) {
//        	System.out.println(" is Open ");
//        }else {
//        	System.out.println("\n Port not open ");
//        }
  }
}