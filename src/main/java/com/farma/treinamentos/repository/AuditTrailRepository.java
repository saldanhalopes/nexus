package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.AuditTrail;
import com.farma.treinamentos.model.enums.AcaoAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, String> {
    
    @Query("SELECT a FROM AuditTrail a WHERE a.utilizador.id = :utilizadorId ORDER BY a.dataHoraUtc DESC")
    List<AuditTrail> findByUtilizadorId(@Param("utilizadorId") String utilizadorId);
    
    @Query("SELECT a FROM AuditTrail a WHERE a.tabelaAfetada = :tabela ORDER BY a.dataHoraUtc DESC")
    List<AuditTrail> findByTabela(@Param("tabela") String tabela);
    
    @Query("SELECT a FROM AuditTrail a WHERE a.registroId = :registroId ORDER BY a.dataHoraUtc DESC")
    List<AuditTrail> findByRegistroId(@Param("registroId") String registroId);
    
    @Query("SELECT a FROM AuditTrail a WHERE a.dataHoraUtc BETWEEN :inicio AND :fim ORDER BY a.dataHoraUtc DESC")
    List<AuditTrail> findByPeriodo(
            @Param("inicio") LocalDateTime inicio, 
            @Param("fim") LocalDateTime fim);
    
    @Query("SELECT a FROM AuditTrail a WHERE a.acao = :acao ORDER BY a.dataHoraUtc DESC")
    Page<AuditTrail> findByAcao(@Param("acao") AcaoAudit acao, Pageable pageable);
    
    @Query("SELECT a FROM AuditTrail a ORDER BY a.dataHoraUtc DESC")
    Page<AuditTrail> findAllOrdered(Pageable pageable);
}