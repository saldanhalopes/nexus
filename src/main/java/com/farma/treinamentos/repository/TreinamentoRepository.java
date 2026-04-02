package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.Treinamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreinamentoRepository extends JpaRepository<Treinamento, String> {
    List<Treinamento> findByDepartamentoId(String departamentoId);
    List<Treinamento> findByAtivoTrue();
    Page<Treinamento> findByAtivoTrue(Pageable pageable);
    
    @Query("SELECT t FROM Treinamento t WHERE t.obrigatorio = true AND t.ativo = true")
    List<Treinamento> findObrigatorios();
    
    @Query("SELECT t FROM Treinamento t WHERE t.departamento.id = :departamentoId AND t.ativo = true")
    List<Treinamento> findAtivosByDepartamento(@Param("departamentoId") String departamentoId);
}