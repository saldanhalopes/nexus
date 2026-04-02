package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.RegistroTreinamento;
import com.farma.treinamentos.model.enums.StatusTreinamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroTreinamentoRepository extends JpaRepository<RegistroTreinamento, String> {
    List<RegistroTreinamento> findByUtilizadorId(String utilizadorId);
    List<RegistroTreinamento> findByTreinamentoId(String treinamentoId);
    List<RegistroTreinamento> findByStatus(StatusTreinamento status);
    
    @Query("SELECT r FROM RegistroTreinamento r WHERE r.utilizador.id = :utilizadorId AND r.status = :status")
    List<RegistroTreinamento> findByUtilizadorAndStatus(
            @Param("utilizadorId") String utilizadorId, 
            @Param("status") StatusTreinamento status);
    
    @Query("SELECT r FROM RegistroTreinamento r WHERE r.utilizador.cargo.departamento.id = :departamentoId")
    List<RegistroTreinamento> findByDepartamento(@Param("departamentoId") String departamentoId);
    
    @Query("SELECT r FROM RegistroTreinamento r WHERE r.dataConclusao < :data AND r.status = 'CONCLUIDO'")
    List<RegistroTreinamento> findExpirando(@Param("data") LocalDate data);
    
    Optional<RegistroTreinamento> findByUtilizadorIdAndTreinamentoId(String utilizadorId, String treinamentoId);
    
    @Query("SELECT r FROM RegistroTreinamento r WHERE r.utilizador.id = :utilizadorId")
    Page<RegistroTreinamento> findByUtilizadorId(@Param("utilizadorId") String utilizadorId, Pageable pageable);
}