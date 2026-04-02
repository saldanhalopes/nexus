package com.farma.treinamentos.controller;

import com.farma.treinamentos.dto.request.TreinamentoRequest;
import com.farma.treinamentos.dto.response.TreinamentoResponse;
import com.farma.treinamentos.service.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinamentos")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping
    public ResponseEntity<Page<TreinamentoResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(trainingService.findAll(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TreinamentoResponse>> findAll() {
        return ResponseEntity.ok(trainingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreinamentoResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(trainingService.findById(id));
    }

    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<TreinamentoResponse>> findByDepartment(@PathVariable String departamentoId) {
        return ResponseEntity.ok(trainingService.findByDepartment(departamentoId));
    }

    @GetMapping("/obrigatorios")
    public ResponseEntity<List<TreinamentoResponse>> findObrigatorios() {
        return ResponseEntity.ok(trainingService.findObrigatorios());
    }

    @PostMapping
    public ResponseEntity<TreinamentoResponse> create(
            @Valid @RequestBody TreinamentoRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(trainingService.create(request, getClientIp(httpRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreinamentoResponse> update(
            @PathVariable String id,
            @Valid @RequestBody TreinamentoRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(trainingService.update(id, request, getClientIp(httpRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, HttpServletRequest httpRequest) {
        trainingService.delete(id, getClientIp(httpRequest));
        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}