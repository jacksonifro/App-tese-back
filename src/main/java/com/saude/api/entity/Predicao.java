package com.saude.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "predicao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Predicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(columnDefinition = "TEXT")
    private String payloadEnviado;

    @Column(columnDefinition = "TEXT")
    private String respostaCompleta; // JSON da PredictionResponse

    private String resultado; // Ex: CURA, OBITO

    private Long tempoProcessamentoMs;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;
}
