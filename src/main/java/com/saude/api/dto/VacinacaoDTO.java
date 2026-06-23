package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacinacaoDTO {
    private String vacinaCovid;
    private String primeiraDose;
    private String segundaDose;
    private String terceiraDose;
    private String vacinaInfluenza;
}
