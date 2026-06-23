package com.saude.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteRecord {
    private String judge;
    private String vote;
    private double weight;
}
