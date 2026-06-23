package com.saude.api.controller;

import com.saude.api.dto.PacienteDTO;
import com.saude.api.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gerenciamento do Cadastro de Pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo paciente")
    public ResponseEntity<PacienteDTO> create(@RequestBody PacienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um paciente")
    public ResponseEntity<PacienteDTO> update(@PathVariable Long id, @RequestBody PacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.update(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar um paciente pelo ID")
    public ResponseEntity<PacienteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar e pesquisar pacientes com paginação")
    public ResponseEntity<Page<PacienteDTO>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(pacienteService.findAll(search, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um paciente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
