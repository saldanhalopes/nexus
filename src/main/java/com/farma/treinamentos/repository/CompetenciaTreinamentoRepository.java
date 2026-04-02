package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.CompetenciaTreinamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenciaTreinamentoRepository extends JpaRepository<CompetenciaTreinamento, Long> {
    List<CompetenciaTreinamento> findByCompetenciaId(Long competenciaId);
    List<CompetenciaTreinamento> findByTreinamentoId(String treinamentoId);
    boolean existsByCompetenciaIdAndTreinamentoId(Long competenciaId, String treinamentoId);
    void deleteByCompetenciaIdAndTreinamentoId(Long competenciaId, String treinamentoId);
}
