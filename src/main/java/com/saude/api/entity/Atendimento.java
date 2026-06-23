package com.saude.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "atendimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

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

    // Internação
    @Column(nullable = false)
    private String internacao;

    @Column(nullable = false)
    private String uti;

    private Integer diasUTI;
    
    @Column(nullable = false)
    private String suporteVentilatorio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToOne(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Predicao predicao;
}
