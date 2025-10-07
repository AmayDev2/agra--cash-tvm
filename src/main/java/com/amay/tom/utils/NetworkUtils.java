package com.amay.tom.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkUtils {
    public static String getLocalIpAddress(){
        try{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()){
                NetworkInterface iface = interfaces.nextElement();
                if(iface.isLoopback() || !iface.isUp() || iface.isVirtual() ) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress addr = addresses.nextElement();
                    if(!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':')==-1){
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "Unknown";
    }

}

