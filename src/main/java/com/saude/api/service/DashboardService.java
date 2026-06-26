package com.saude.api.service;

import com.saude.api.dto.DashboardDTO;
import com.saude.api.dto.DashboardDTO.ChartData;
import com.saude.api.dto.DashboardDTO.TimelineData;
import com.saude.api.entity.Paciente;
import com.saude.api.entity.Predicao;
import com.saude.api.repository.PacienteRepository;
import com.saude.api.repository.PredicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PacienteRepository pacienteRepository;
    private final PredicaoRepository predicaoRepository;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardStats() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        List<Predicao> predicoes = predicaoRepository.findAll();

        long totalPatients = pacientes.size();
        long totalPredictions = predicoes.size();

        long criticalPatients = predicoes.stream()
                .filter(p -> "ÓBITO".equalsIgnoreCase(p.getResultado()) || "OBITO".equalsIgnoreCase(p.getResultado()))
                .count();

        // Calculate Average Age
        double averageAge = pacientes.stream()
                .map(Paciente::getDataNascimento)
                .filter(Objects::nonNull)
                .mapToInt(dt -> Period.between(dt, LocalDate.now()).getYears())
                .average()
                .orElse(0.0);

        // Verdict Data
        long obito = criticalPatients;
        long cura = totalPredictions - obito;
        List<ChartData> verdictData = Arrays.asList(
                new ChartData("Cura", cura, "#12B76A"),
                new ChartData("Óbito", obito, "#D92D20")
        );

        // Patient Type Data
        Map<String, Long> typesCount = pacientes.stream()
                .map(this::getPatientType)
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

        List<ChartData> patientTypeData = typesCount.entrySet().stream()
                .map(e -> {
                    String shortName = e.getKey().split(" ")[0];
                    if ("Adulto / Idoso".equals(e.getKey()) || "Adulto".equals(e.getKey())) {
                        shortName = "Adulto"; // Sync with simplified frontend names
                    }
                    return new ChartData(shortName, e.getValue(), null);
                })
                .sorted((a, b) -> Long.compare(b.getValue().longValue(), a.getValue().longValue()))
                .collect(Collectors.toList());

        // Timeline Data
        Map<String, Long> timelineObj = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (Predicao p : predicoes) {
            if (p.getDataHora() != null) {
                String key = p.getDataHora().format(formatter);
                timelineObj.put(key, timelineObj.getOrDefault(key, 0L) + 1);
            }
        }

        List<TimelineData> timelineData = timelineObj.entrySet().stream()
                .sorted((a, b) -> {
                    String[] partsA = a.getKey().split("/");
                    String[] partsB = b.getKey().split("/");
                    int valA = Integer.parseInt(partsA[1]) * 100 + Integer.parseInt(partsA[0]);
                    int valB = Integer.parseInt(partsB[1]) * 100 + Integer.parseInt(partsB[0]);
                    return Integer.compare(valA, valB);
                })
                .map(e -> new TimelineData(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalPatients(totalPatients)
                .totalPredictions(totalPredictions)
                .criticalPatients(criticalPatients)
                .averageAge((int) Math.round(averageAge))
                .verdictData(verdictData)
                .patientTypeData(patientTypeData)
                .timelineData(timelineData)
                .build();
    }

    private String getPatientType(Paciente patient) {
        if ("Sim".equalsIgnoreCase(patient.getPuerpera())) {
            return "Puérpera";
        }
        if ("Sim".equalsIgnoreCase(patient.getGestante())) {
            return "Gestante";
        }
        if (patient.getDataNascimento() != null) {
            int age = Period.between(patient.getDataNascimento(), LocalDate.now()).getYears();
            if (age >= 0 && age <= 3) {
                return "Criança";
            }
        }
        return "Adulto";
    }
}
