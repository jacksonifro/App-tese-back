package com.saude.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Dados para predição de Gestante")
public class GestanteRequest extends BasePredictionRequest {
}
