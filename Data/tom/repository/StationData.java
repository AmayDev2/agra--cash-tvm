package com.amay.tom.repository;

import com.amay.tom.model.Station;
import com.amay.tom.utils.env.EnvFile;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;

import java.io.FileReader;
import com.amay.tom.model.Station;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class StationData {

    private static StationData INSTANCE;

    public static StationData getInstance(){
        if(INSTANCE==null){
            INSTANCE=new StationData();

        }
        return INSTANCE;

    }
    private Station[] stations;

    private StationData(){
            getStationsArray();
    }

    public void getStationsArray() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String st=EnvFile.getStationsFile();
            // Parse JSON to Java object
            stations = objectMapper.readValue(new File(st), Station[].class);

            // Print station details
            for (Station station : stations) {
                System.out.println("Station code: " + station.getStationId());
                System.out.println("Station name: " + station.getStationName());
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public Station[] getStationArray(){
//        getStationsArray();
//        Station[] stations = {
//                new Station("st01", "Taj East Gate"),
//                new Station("st02", "Basai"),
//                new Station("st03", "Fatehabad Road"),
//                new Station("st04", "Taj Mahal"),
//                new Station("st05", "Agra Fort"),
//                new Station("st06", "JamaMasjid"),
//                new Station("st07", "MedicalCollege"),
//                new Station("st08", "Agra College"),
//                new Station("st09", "Raja Ki Mandi"),
//                new Station("st10", "RBS College"),
//                new Station("st11", "ISBT"),
//                new Station("st12", "Guru KaTaal"),
//                new Station("st13", "Sikandra"),
//                new Station("st14", "Agra Cantt"),
//                new Station("st15", "Sadar Bazar"),
//                new Station("st16", "Collectorate"),
//                new Station("st17", "SubhashPark"),
//                new Station("st18", "Agra College"),
//                new Station("st19", "Hariparvat Chauraha"),
//                new Station("st20", "Sanjay Place"),
//                new Station("st21", "M.G.Road"),
//                new Station("st22", "Sultan Ganj Crossing"),
//                new Station("st23", "Kamla Nagar"),
//                new Station("st24", "Ram Bagh"),
//                new Station("st25", "Foundary Nagar"),
//                new Station("st26", "Agra Mandi"),
//                new Station("st27", "Kalindi Vihar")
//        };
        return this.stations;

    }

    public Station getStation(String stationId){
        Station[] stations = getStationArray();
        for (Station station : stations) {
            if(station.getStationId().equals(stationId)){
                return station;
            }
        }
        return null;
    }

    public boolean checkIsEqual(String to) {
        Station[] stations = getStationArray();
        for (Station station : stations) {
            if(station.getStationName().equals(to)){
                return true;
            }
        }
        return false;
    }
}

