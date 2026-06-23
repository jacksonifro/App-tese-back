package com.saude.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comorbidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comorbidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
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
