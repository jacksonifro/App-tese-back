package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String cns;
    private LocalDate dataNascimento;
    private String sexo;
    private String gestante;
    private String puerpera;
    private String raca;
    private String estadoCivil;
    private String escolaridade;
    private String profissao;

    private EnderecoDTO endereco;
    private ContatoDTO contato;
    private VacinacaoDTO vacinacao;
    private ComorbidadeDTO comorbidade;
}
