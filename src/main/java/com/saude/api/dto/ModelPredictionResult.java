package com.saude.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ModelPredictionResult {
    private String predictedClass;
    private Map<String, Double> probabilities;
    private ModelMetrics metrics;
}
