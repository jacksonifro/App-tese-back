package com.saude.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PredictionResponse {
    private ModelPredictionResult randomForest;
    private ModelPredictionResult knn;
    private ModelPredictionResult logisticRegression;
    private ModelPredictionResult svm;
    private ModelPredictionResult gradientBoosting;
    private String llmAnalysis; // Gemini
    private String groqAnalysis;
    private VerdictBoard verdictBoard;
    private java.util.List<FeatureWeight> topFeatures;
    private long processingTimeMs;
}
