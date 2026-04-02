package com.farma.treinamentos.controller;

import com.farma.treinamentos.model.Competencia;
import com.farma.treinamentos.repository.CompetenciaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competencias")
public class CompetenciaController {

    private final CompetenciaRepository competenciaRepository;

    public CompetenciaController(CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Competencia>> findAll() {
        return ResponseEntity.ok(competenciaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Competencia> findById(@PathVariable Long id) {
        return competenciaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
