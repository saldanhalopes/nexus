package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, String> {
    Optional<Cargo> findByNome(String nome);
    List<Cargo> findByDepartamentoId(String departamentoId);
    boolean existsByNomeAndDepartamentoId(String nome, String departamentoId);
    
    @Query("SELECT c FROM Cargo c WHERE c.departamento.id = :departamentoId AND c.status = true")
    List<Cargo> findAtivosByDepartamento(@Param("departamentoId") String departamentoId);
}