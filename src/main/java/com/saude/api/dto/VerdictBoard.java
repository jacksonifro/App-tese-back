package com.saude.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class VerdictBoard {
    private List<VoteRecord> votes;
    private Map<String, Double> scorePanel;
    private String finalVerdict;
}
