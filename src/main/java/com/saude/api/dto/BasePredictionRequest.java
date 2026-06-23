package com.saude.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public abstract class BasePredictionRequest {

    @Schema(description = "Unidade Federativa", example = "SP")
    @NotNull(message = "Campo SG_UF_NOT e obrigatorio")
    private String SG_UF_NOT;

    @Schema(description = "Sexo", example = "Feminino")
    @NotNull(message = "Campo CS_SEXO e obrigatorio")
    private String CS_SEXO;

    @Schema(description = "Idade", example = "30")
    @NotNull(message = "Campo NU_IDADE_N e obrigatorio")
    private Integer NU_IDADE_N;

    @Schema(description = "Raca/Cor", example = "Branca")
    @NotNull(message = "Campo CS_RACA e obrigatorio")
    private String CS_RACA;

    @Schema(description = "Zona de residencia", example = "Urbana")
    @NotNull(message = "Campo CS_ZONA e obrigatorio")
    private String CS_ZONA;

    @Schema(description = "Infeccao Nosocomial", example = "Nao")
    @NotNull(message = "Campo NOSOCOMIAL e obrigatorio")
    private String NOSOCOMIAL;

    @Schema(description = "Sintoma: Febre", example = "Sim")
    @NotNull(message = "Campo FEBRE e obrigatorio")
    private String FEBRE;

    @Schema(description = "Sintoma: Tosse", example = "Nao")
    @NotNull(message = "Campo TOSSE e obrigatorio")
    private String TOSSE;

    @Schema(description = "Sintoma: Dor de Garganta", example = "Nao")
    @NotNull(message = "Campo GARGANTA e obrigatorio")
    private String GARGANTA;

    @Schema(description = "Sintoma: Dispneia", example = "Nao")
    @NotNull(message = "Campo DISPNEIA e obrigatorio")
    private String DISPNEIA;

    @Schema(description = "Sintoma: Desconforto Respiratorio", example = "Nao")
    @NotNull(message = "Campo DESC_RESP e obrigatorio")
    private String DESC_RESP;

    @Schema(description = "Sintoma: Saturacao de O2 < 95%", example = "Nao")
    @NotNull(message = "Campo SATURACAO e obrigatorio")
    private String SATURACAO;

    @Schema(description = "Sintoma: Diarreia", example = "Nao")
    @NotNull(message = "Campo DIARREIA e obrigatorio")
    private String DIARREIA;

    @Schema(description = "Sintoma: Vomito", example = "Nao")
    @NotNull(message = "Campo VOMITO e obrigatorio")
    private String VOMITO;

    @Schema(description = "Sintoma: Dor Abdominal", example = "Nao")
    @NotNull(message = "Campo DOR_ABD e obrigatorio")
    private String DOR_ABD;

    @Schema(description = "Sintoma: Fadiga", example = "Nao")
    @NotNull(message = "Campo FADIGA e obrigatorio")
    private String FADIGA;

    @Schema(description = "Sintoma: Perda de Olfato", example = "Nao")
    @NotNull(message = "Campo PERD_OLFT e obrigatorio")
    private String PERD_OLFT;

    @Schema(description = "Sintoma: Perda de Paladar", example = "Nao")
    @NotNull(message = "Campo PERD_PALA e obrigatorio")
    private String PERD_PALA;

    @Schema(description = "Possui Fator de Risco", example = "Nao")
    @NotNull(message = "Campo FATOR_RISC e obrigatorio")
    private String FATOR_RISC;

    @Schema(description = "Comorbidade: Cardiopatia", example = "Nao")
    @NotNull(message = "Campo CARDIOPATI e obrigatorio")
    private String CARDIOPATI;

    @Schema(description = "Comorbidade: Doenca Hematologica", example = "Nao")
    @NotNull(message = "Campo HEMATOLOGI e obrigatorio")
    private String HEMATOLOGI;

    @Schema(description = "Comorbidade: Sindrome de Down", example = "Nao")
    @NotNull(message = "Campo SIND_DOWN e obrigatorio")
    private String SIND_DOWN;

    @Schema(description = "Comorbidade: Doenca Hepatica", example = "Nao")
    @NotNull(message = "Campo HEPATICA e obrigatorio")
    private String HEPATICA;

    @Schema(description = "Comorbidade: Asma", example = "Nao")
    @NotNull(message = "Campo ASMA e obrigatorio")
    private String ASMA;

    @Schema(description = "Comorbidade: Diabetes", example = "Nao")
    @NotNull(message = "Campo DIABETES e obrigatorio")
    private String DIABETES;

    @Schema(description = "Comorbidade: Doenca Neurologica", example = "Nao")
    @NotNull(message = "Campo NEUROLOGIC e obrigatorio")
    private String NEUROLOGIC;

    @Schema(description = "Comorbidade: Pneumopatia", example = "Nao")
    @NotNull(message = "Campo PNEUMOPATI e obrigatorio")
    private String PNEUMOPATI;

    @Schema(description = "Comorbidade: Imunodepressao", example = "Nao")
    @NotNull(message = "Campo IMUNODEPRE e obrigatorio")
    private String IMUNODEPRE;

    @Schema(description = "Comorbidade: Doenca Renal", example = "Nao")
    @NotNull(message = "Campo RENAL e obrigatorio")
    private String RENAL;

    @Schema(description = "Comorbidade: Obesidade", example = "Nao")
    @NotNull(message = "Campo OBESIDADE e obrigatorio")
    private String OBESIDADE;

    @Schema(description = "Tomou Vacina Gripe", example = "Nao")
    @NotNull(message = "Campo VACINA_GRIPE e obrigatorio")
    private String VACINA_GRIPE;

    @Schema(description = "Houve Internacao", example = "Nao")
    @NotNull(message = "Campo INTERNACAO e obrigatorio")
    private String INTERNACAO;

    @Schema(description = "Internado em UTI", example = "Nao")
    @NotNull(message = "Campo UTI e obrigatorio")
    private String UTI;

    @Schema(description = "Dias em UTI", example = "0")
    @NotNull(message = "Campo DIAS_UTI e obrigatorio")
    private Integer DIAS_UTI;

    @Schema(description = "Suporte Ventilatorio", example = "Nao")
    @NotNull(message = "Campo SUPORT_VEN e obrigatorio")
    private String SUPORT_VEN;

    @Schema(description = "Tomou Vacina COVID", example = "Sim")
    @NotNull(message = "Campo VACINA_COV e obrigatorio")
    private String VACINA_COV;

    @Schema(description = "1a Dose Vacina COVID", example = "Sim")
    @NotNull(message = "Campo VACINA_COV_1_DOSE e obrigatorio")
    private String VACINA_COV_1_DOSE;

    @Schema(description = "2a Dose Vacina COVID", example = "Sim")
    @NotNull(message = "Campo VACINA_COV_2_DOSE e obrigatorio")
    private String VACINA_COV_2_DOSE;

    @Schema(description = "3a Dose Vacina COVID", example = "Sim")
    @NotNull(message = "Campo VACINA_COV_3_DOSE e obrigatorio")
    private String VACINA_COV_3_DOSE;
}
