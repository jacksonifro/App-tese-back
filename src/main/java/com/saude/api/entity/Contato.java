package com.saude.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telefone;
    private String celular;
    private String email;
    private String contatoEmergencia;
    private String telefoneEmergencia;
}
