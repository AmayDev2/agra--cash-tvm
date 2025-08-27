package com.amay.tom.model.faretable;

import java.util.List;
import java.util.Map;

public record FareMatrixDTO(
        List<String> stationCodes,
        List<String> stationNames,
        List<String> stationId,
        Map<String, Map<String, Integer>> matrix
) {}
