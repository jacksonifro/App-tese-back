package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureWeight {
    private String feature;
    private String impact; // e.g., "Alto", "Médio", "Crítico", "Baixo"
}
