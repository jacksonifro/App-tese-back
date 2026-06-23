package com.saude.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Dados para predição de Crianças (0 a 3 anos)")
public class CriancaRequest extends BasePredictionRequest {
}
