package com.saude.api.controller;

import com.saude.api.config.WekaModelLoader;
import com.saude.api.enums.PatientGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Endpoints para verificacao de saude e status da API")
public class HealthController {

    private final WekaModelLoader wekaModelLoader;

    @GetMapping
    @Operation(summary = "Verifica se a API esta rodando e os modelos Weka estao carregados")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        boolean allModelsLoaded = wekaModelLoader.getModels().size() == PatientGroup.values().length;

        Map<String, Object> response = Map.of(
                "status", "UP",
                "modelsLoaded", allModelsLoaded,
                "loadedGroups", wekaModelLoader.getModels().keySet()
        );

        return ResponseEntity.ok(response);
    }
}
