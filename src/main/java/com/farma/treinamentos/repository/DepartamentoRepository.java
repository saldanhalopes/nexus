package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, String> {
    Optional<Departamento> findByNome(String nome);
    boolean existsByNome(String nome);
}