package com.amay.tom.model.faretable;

import java.util.List;

public class FaretableMapper {

    public static int[][] entityToMatrix(List<FareRowEntity> fareRowEntities, int size){
        int[][] dis = new int[size][size];
        for(FareRowEntity fareRowEntity : fareRowEntities){
            String source = fareRowEntity.getSource();
            String destination = fareRowEntity.getDestination();
            double fare = fareRowEntity.getFareAmount();

            dis[Integer.parseInt(source)][Integer.parseInt(destination)]=(int)fare;
        }
        return dis;
    }
}
