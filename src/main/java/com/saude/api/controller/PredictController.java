package com.saude.api.controller;

import com.saude.api.config.WekaModelLoader;
import com.saude.api.config.WekaModelWrapper;
import com.saude.api.dto.CriancaRequest;
import com.saude.api.dto.GestanteRequest;
import com.saude.api.dto.PredictionResponse;
import com.saude.api.dto.PuerperaRequest;
import com.saude.api.enums.PatientGroup;
import com.saude.api.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Predição", description = "Endpoints de inferencia do Weka")
public class PredictController {

    private final PredictionService predictionService;
    private final WekaModelLoader wekaModelLoader;

    @PostMapping("/predict/episode")
    @Operation(summary = "Realiza predicao clinica baseada em um Paciente cadastrado e nos dados do Episódio atual")
    public ResponseEntity<PredictionResponse> predictClinicalEpisode(@Valid @RequestBody com.saude.api.dto.NovaPredicaoRequest request) {
        return ResponseEntity.ok(predictionService.predict(request));
    }

    @PostMapping("/predict/gestante")
    @Operation(summary = "Realiza predicao para o grupo Gestante")
    public ResponseEntity<PredictionResponse> predictGestante(@Valid @RequestBody GestanteRequest request) {
        return ResponseEntity.ok(predictionService.predict(PatientGroup.GESTANTE, request));
    }

    @PostMapping("/predict/crianca")
    @Operation(summary = "Realiza predicao para o grupo Criança (0 a 3 anos)")
    public ResponseEntity<PredictionResponse> predictCrianca(@Valid @RequestBody CriancaRequest request) {
        return ResponseEntity.ok(predictionService.predict(PatientGroup.CRIANCA, request));
    }

    @PostMapping("/predict/puerpera")
    @Operation(summary = "Realiza predicao para o grupo Puerpera")
    public ResponseEntity<PredictionResponse> predictPuerpera(@Valid @RequestBody PuerperaRequest request) {
        return ResponseEntity.ok(predictionService.predict(PatientGroup.PUERPERA, request));
    }

    @GetMapping("/model")
    @Operation(summary = "Retorna informacoes dos modelos carregados em memoria")
    public ResponseEntity<Map<String, Object>> getModelInfo() {
        List<Map<String, Object>> modelsInfo = new ArrayList<>();

        wekaModelLoader.getModels().forEach((group, wrapper) -> {
            Map<String, Object> info = new HashMap<>();
            info.put("grupo", group.name());
            info.put("algoritmo", "Random Forest"); // Fixo pois sabemos do WEKA
            info.put("quantidadeAtributos", wrapper.getHeader().numAttributes());
            
            List<String> classes = new ArrayList<>();
            for (int i = 0; i < wrapper.getHeader().classAttribute().numValues(); i++) {
                classes.add(wrapper.getHeader().classAttribute().value(i));
            }
            info.put("nomesDasClasses", classes);
            modelsInfo.add(info);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("dataInicializacaoAPI", wekaModelLoader.getInitializationTime());
        response.put("modelos", modelsInfo);

        return ResponseEntity.ok(response);
    }
}
