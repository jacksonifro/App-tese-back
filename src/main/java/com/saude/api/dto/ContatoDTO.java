package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContatoDTO {
    private String telefone;
    private String celular;
    private String email;
    private String contatoEmergencia;
    private String telefoneEmergencia;
}
