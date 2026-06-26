package com.saude.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private long totalPatients;
    private long totalPredictions;
    private long criticalPatients;
    private int averageAge;
    
    private List<ChartData> verdictData;
    private List<ChartData> patientTypeData;
    private List<TimelineData> timelineData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private String name;
        private Number value;
        private String color; // Optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineData {
        private String name;
        @com.fasterxml.jackson.annotation.JsonProperty("Predições")
        private Number predicoes;
    }
}
