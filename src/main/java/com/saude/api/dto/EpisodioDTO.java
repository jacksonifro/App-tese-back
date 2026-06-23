package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodioDTO {
    // Sintomas
    private String febre;
    private String tosse;
    private String dispneia;
    private String fadiga;
    private String dorAbdominal;
    private String dorGarganta;
    private String saturacao;
    private String desconfortoRespiratorio;
    private String diarreia;
    private String vomito;
    private String perdaOlfato;
    private String perdaPaladar;
    private String nosocomial;

    // Internação
    private String internacao;
    private String uti;
    private Integer diasUTI;
    private String suporteVentilatorio;
}
