package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.model.enums.StatusUtilizador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilizadorRepository extends JpaRepository<Utilizador, String> {
    Optional<Utilizador> findByEmail(String email);
    Optional<Utilizador> findByMatricula(String matricula);
    boolean existsByMatriculaOrEmail(String matricula, String email);
    boolean existsByEmail(String email);
    boolean existsByMatricula(String matricula);
    List<Utilizador> findByCargoId(String cargoId);
    List<Utilizador> findByDepartamentoId(String departamentoId);
    List<Utilizador> findByStatus(StatusUtilizador status);
    
    @Query("SELECT u FROM Utilizador u WHERE u.status = :status")
    Page<Utilizador> findByStatus(@Param("status") StatusUtilizador status, Pageable pageable);
    
    @Query("SELECT u FROM Utilizador u WHERE u.ultimoLogin < :data")
    List<Utilizador> findInativosPorTempo(@Param("data") LocalDateTime data);
    
    @Query("SELECT u FROM Utilizador u WHERE u.cargo.departamento.id = :departamentoId AND u.status = 'ATIVO'")
    List<Utilizador> findAtivosByDepartamento(@Param("departamentoId") String departamentoId);
}