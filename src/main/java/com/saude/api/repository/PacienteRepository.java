package com.saude.api.repository;

import com.saude.api.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf);
    Page<Paciente> findByNomeContainingIgnoreCaseOrCpfContaining(String nome, String cpf, Pageable pageable);
}
