package com.amay.tom.utils.faretable;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StationMatrixUtil {

    private final String[] stationCodes;
    private final String[] stationNames;
    private final int[][] distanceMatrix;
    private final Map<String, Integer> stationIndexMap = new HashMap<>();

    /**
     * Constructs a StationMatrixUtil from a JSON object containing station codes, names, and a distance matrix.
     *
     * @param inputJson JSON object with keys "stationCodes", "stationNames", and "matrix".
     */

    public StationMatrixUtil(JSONObject inputJson) {
        JSONArray codesArray = inputJson.getJSONArray("stationCodes");
        JSONArray namesArray = inputJson.getJSONArray("stationNames");
        JSONObject matrixObject = inputJson.getJSONObject("matrix");

        int size = codesArray.length();
        stationCodes = new String[size];
        stationNames = new String[size];
        distanceMatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            stationCodes[i] = codesArray.getString(i);
            stationNames[i] = namesArray.getString(i);
            stationIndexMap.put(stationCodes[i], i);
        }

        for (int i = 0; i < size; i++) {
            String fromCode = stationCodes[i];
            JSONObject distances = matrixObject.getJSONObject(fromCode);
            for (int j = 0; j < size; j++) {
                String toCode = stationCodes[j];
                distanceMatrix[i][j] = distances.optInt(toCode, 0);
            }
        }
    }

    public int getDistance(String fromStationCode, String toStationCode) {
        Integer i = stationIndexMap.get(fromStationCode);
        Integer j = stationIndexMap.get(toStationCode);
        if (i == null || j == null) {
            throw new IllegalArgumentException("Invalid station code(s)");
        }
        return distanceMatrix[i][j];
    }

    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public String[] getStationCodes() {
        return stationCodes;
    }

    public String[] getStationNames() {
        return stationNames;
    }

    public String getStationNameByCode(String code) {
        Integer idx = stationIndexMap.get(code);
        return (idx != null) ? stationNames[idx] : null;
    }

    public void printMatrix() {
        System.out.println("Distance Matrix:");


        for (int[] row : distanceMatrix) {
            for (int val : row) {
                System.out.printf("%4d", val);
            }
            System.out.println();
        }
    }
}
