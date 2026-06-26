package com.saude.api.controller;

import com.saude.api.dto.DashboardDTO;
import com.saude.api.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Métricas e estatísticas do dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Obter estatísticas consolidadas para o dashboard")
    public ResponseEntity<DashboardDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}
