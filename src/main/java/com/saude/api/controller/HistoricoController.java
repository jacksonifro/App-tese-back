package com.saude.api.controller;

import com.saude.api.entity.Predicao;
import com.saude.api.repository.PredicaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/historico")
@RequiredArgsConstructor
@Tag(name = "Histórico", description = "Gerenciamento de predições passadas")
public class HistoricoController {

    private final PredicaoRepository predicaoRepository;

    @GetMapping
    @Operation(summary = "Listar todo o histórico de predições")
    public ResponseEntity<List<Predicao>> getHistoricoAll() {
        return ResponseEntity.ok(predicaoRepository.findAll());
    }

    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Listar o histórico de predições de um paciente")
    public ResponseEntity<List<Predicao>> getHistoricoByPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(predicaoRepository.findByAtendimentoPacienteIdOrderByDataHoraDesc(pacienteId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma predição específica")
    public ResponseEntity<Predicao> getHistoricoById(@PathVariable Long id) {
        return predicaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
