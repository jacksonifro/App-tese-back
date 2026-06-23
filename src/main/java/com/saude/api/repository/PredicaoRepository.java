package com.saude.api.repository;

import com.saude.api.entity.Predicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredicaoRepository extends JpaRepository<Predicao, Long> {
    List<Predicao> findByAtendimentoPacienteIdOrderByDataHoraDesc(Long pacienteId);
}
