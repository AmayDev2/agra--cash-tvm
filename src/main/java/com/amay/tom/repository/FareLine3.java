package com.amay.tom.repository;

import com.amay.tom.agent.Agent;
import com.amay.tom.model.faretable.FareRowEntity;
import com.amay.tom.model.faretable.FaretableMapper;
import com.amay.tom.repository.fareTable.FareTableRepository;
import com.amay.tom.utils.env.EnvFile;

import java.io.*;
import java.util.List;

public class FareLine3 {
    public static int[][] distanceMatrix ;

//    public static void saveData(String fileName) {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EnvFile.getDistanceMatrixFilePath()+fileName))) {
//            oos.writeObject(distanceMatrix);
//            //System.out.println("Data saved successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void saveData(String fileName,int[][] distanceMatrix) {
//        FareLine3.distanceMatrix=distanceMatrix;
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EnvFile.getDistanceMatrixFilePath() +fileName))) {
//            oos.writeObject(distanceMatrix);
//            //System.out.println("Data saved successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void retrieveData(Agent agent) {
//        try
//               (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EnvFile.getDistanceMatrixFilePath()+fileName)))
//        {
//            distanceMatrix = (int[][]) ois.readObject();
            FareTableRepository fareTableRepository = agent.getFareTableRepository();
            List<FareRowEntity> fareRowEntities = fareTableRepository.findAll();
            distanceMatrix =  FaretableMapper.entityToMatrix(fareRowEntities,agent.getFareTableRepository().getUniqueSourceCount());
            //System.out.println("Data retrieved successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}