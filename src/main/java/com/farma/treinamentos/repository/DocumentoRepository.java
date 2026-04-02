package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.Documento;
import com.farma.treinamentos.model.enums.StatusDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, String> {
    List<Documento> findByDepartamentoId(String departamentoId);
    List<Documento> findByStatus(StatusDocumento status);
    Page<Documento> findByStatus(StatusDocumento status, Pageable pageable);
    
    @Query("SELECT d FROM Documento d WHERE d.dataValidade < :data AND d.status = com.farma.treinamentos.model.enums.StatusDocumento.ATIVO")
    List<Documento> findExpirando(@Param("data") LocalDate data);
    
    @Query("SELECT d FROM Documento d WHERE d.departamento.id = :departamentoId AND d.status = :status")
    List<Documento> findByDepartamentoAndStatus(@Param("departamentoId") String departamentoId, @Param("status") StatusDocumento status);
}