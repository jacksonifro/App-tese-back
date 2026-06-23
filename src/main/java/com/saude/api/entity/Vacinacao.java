package com.saude.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vacinacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacinacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vacinaCovid;
    
    private String primeiraDose;
    private String segundaDose;
    private String terceiraDose;
    
    @Column(nullable = false)
    private String vacinaInfluenza;
}
