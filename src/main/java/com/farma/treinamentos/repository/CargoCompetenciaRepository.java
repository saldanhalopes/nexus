package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.CargoCompetencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CargoCompetenciaRepository extends JpaRepository<CargoCompetencia, Long> {
    List<CargoCompetencia> findByCargoId(String cargoId);
    List<CargoCompetencia> findByCompetenciaId(Long competenciaId);
    Optional<CargoCompetencia> findByCargoIdAndCompetenciaId(String cargoId, Long competenciaId);
    
    @Modifying
    @Query("DELETE FROM CargoCompetencia cc WHERE cc.cargo.id = :cargoId AND cc.competencia.id = :competenciaId")
    void deleteByCargoIdAndCompetenciaId(@Param("cargoId") String cargoId, @Param("competenciaId") Long competenciaId);
}
