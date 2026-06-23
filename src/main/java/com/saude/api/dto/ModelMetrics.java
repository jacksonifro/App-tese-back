package com.saude.api.dto;

import lombok.Builder;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelMetrics {
    private double accuracy;
    private double precision;
    private double sensitivity; // Recall / True Positive Rate
    private double specificity; // True Negative Rate
    private double f1Score;
    private double aucRoc;
}
