package com.saude.api.service;

import com.saude.api.dto.PacienteDTO;
import com.saude.api.entity.Paciente;
import com.saude.api.repository.PacienteRepository;
import com.saude.api.util.PacienteMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Transactional
    public PacienteDTO create(PacienteDTO dto) {
        if (pacienteRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }
        Paciente entity = pacienteMapper.toEntity(dto);
        Paciente saved = pacienteRepository.save(entity);
        return pacienteMapper.toDTO(saved);
    }

    @Transactional
    public PacienteDTO update(Long id, PacienteDTO dto) {
        Paciente entity = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado com o ID: " + id));

        if (!entity.getCpf().equals(dto.getCpf()) && pacienteRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }

        pacienteMapper.updateEntity(entity, dto);
        Paciente updated = pacienteRepository.save(entity);
        return pacienteMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public PacienteDTO findById(Long id) {
        Paciente entity = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado com o ID: " + id));
        return pacienteMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<PacienteDTO> findAll(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return pacienteRepository.findByNomeContainingIgnoreCaseOrCpfContaining(search, search, pageable)
                    .map(pacienteMapper::toDTO);
        }
        return pacienteRepository.findAll(pageable)
                .map(pacienteMapper::toDTO);
    }

    @Transactional
    public void delete(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Paciente não encontrado com o ID: " + id);
        }
        pacienteRepository.deleteById(id);
    }
}
