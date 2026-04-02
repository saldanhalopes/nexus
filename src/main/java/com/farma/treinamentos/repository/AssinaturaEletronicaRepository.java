package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.AssinaturaEletronica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssinaturaEletronicaRepository extends JpaRepository<AssinaturaEletronica, String> {
}
