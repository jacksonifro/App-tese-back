package com.saude.api.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import weka.classifiers.Classifier;
import weka.core.Instances;

@Getter
@AllArgsConstructor
public class WekaModelWrapper {
    private final Classifier randomForest;
    private final Classifier knn;
    private final Classifier logisticRegression;
    private final Classifier svm;
    private final Classifier gradientBoosting;
    private final Instances header;
    private final java.util.Map<String, com.saude.api.dto.ModelMetrics> metrics;
}
