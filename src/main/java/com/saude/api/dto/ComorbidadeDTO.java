package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComorbidadeDTO {
    private String fatorRisco;
    private String diabetes;
    private String cardiopatia;
    private String asma;
    private String obesidade;
    private String renal;
    private String hepatica;
    private String pneumopatia;
    private String hematologica;
    private String neurologica;
    private String imunodepressao;
    private String sindromeDown;
}
