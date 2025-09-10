package com.amay.tom.repository;

import com.amay.tom.model.station.Station;
import com.amay.tom.model.station.StationEntity;
import com.amay.tom.repository.station.StationRepository;

import java.util.List;

public class StationData {

    private static StationData INSTANCE;

    public static StationData getInstance(){
        if(INSTANCE==null){
            INSTANCE=new StationData();
        }
        return INSTANCE;
    }
    private Station[] stations;

    private StationData(StationRepository stationRepository){
            getStationsArray(stationRepository);
    }

    private StationData(){

    }
    public void getStationsArray(StationRepository stationRepository) {
            List<StationEntity> stationList = stationRepository.findAll();
            stations=new Station[stationList.size()];
            // Print station details
            for (int i=0;i<stationList.size();i++) {
                String stationId = stationList.get(i).getStationId();
                String stationName = stationList.get(i).getStationName();
                //System.out.println("Station code: " + stationId);
                //System.out.println("Station name: " + stationName);
                //System.out.println();
                stations[i]=new Station(stationId,stationName);
            }
    }

    public Station[] getStationArray(){
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

    public Station getStationByName(String stationName){
        Station[] stations = getStationArray();
        for (Station station : stations) {
            if(station.getStationName().equals(stationName)){
                return station;
            }
        }
        return null;
    }

    public int getPlatform(String sourceStation,String destination){
        int sourceStationSeqNo=Integer.parseInt(getStationByName(sourceStation).getStationId());
        int destinationStationSeqNo=Integer.parseInt(getStationByName(destination).getStationId());
        return sourceStationSeqNo>destinationStationSeqNo?1:sourceStationSeqNo<destinationStationSeqNo?2:0;

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

