package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.MatrizCompetencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatrizCompetenciaRepository extends JpaRepository<MatrizCompetencia, String> {
    List<MatrizCompetencia> findByDepartamentoId(String departamentoId);
    List<MatrizCompetencia> findByCargoId(String cargoId);
    List<MatrizCompetencia> findByAtivaTrue();
    
    @Query("SELECT m FROM MatrizCompetencia m WHERE m.departamento.id = :departamentoId AND m.cargo.id = :cargoId AND m.ativa = true")
    Optional<MatrizCompetencia> findAtivaByDepartamentoAndCargo(
            @Param("departamentoId") String departamentoId, 
            @Param("cargoId") String cargoId);
    
    @Query("SELECT m FROM MatrizCompetencia m WHERE m.anoReferencia = :ano AND m.ativa = true")
    List<MatrizCompetencia> findByAnoReferencia(@Param("ano") Integer ano);
}