package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmAnalysisResult {
    private String parecer;
    private String veredito; // CURA ou OBITO
    @Builder.Default
    private List<FeatureWeight> topFeatures = new ArrayList<>();
}
