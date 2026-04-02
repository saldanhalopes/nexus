package com.farma.treinamentos.controller;

import com.farma.treinamentos.dto.request.MatrizCompetenciaRequest;
import com.farma.treinamentos.dto.response.MatrizCompetenciaResponse;
import com.farma.treinamentos.service.MatrixService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matrizes")
public class MatrixController {

    private final MatrixService matrixService;

    public MatrixController(MatrixService matrixService) {
        this.matrixService = matrixService;
    }

    @GetMapping
    public ResponseEntity<List<MatrizCompetenciaResponse>> findAll() {
        return ResponseEntity.ok(matrixService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatrizCompetenciaResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(matrixService.findById(id));
    }

    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<MatrizCompetenciaResponse>> findByDepartment(@PathVariable String departamentoId) {
        return ResponseEntity.ok(matrixService.findByDepartment(departamentoId));
    }

    @GetMapping("/cargo/{cargoId}")
    public ResponseEntity<List<MatrizCompetenciaResponse>> findByCargo(@PathVariable String cargoId) {
        return ResponseEntity.ok(matrixService.findByCargo(cargoId));
    }

    @PostMapping
    public ResponseEntity<MatrizCompetenciaResponse> create(
            @Valid @RequestBody MatrizCompetenciaRequest request,
            HttpServletRequest httpRequest) {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(matrixService.create(request, userId, getClientIp(httpRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatrizCompetenciaResponse> update(
            @PathVariable String id,
            @Valid @RequestBody MatrizCompetenciaRequest request,
            HttpServletRequest httpRequest) {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(matrixService.update(id, request, userId, getClientIp(httpRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestParam String senha,
            @RequestParam String motivo,
            HttpServletRequest httpRequest) {
        String userId = getCurrentUserId();
        matrixService.delete(id, senha, motivo, userId, getClientIp(httpRequest));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "anonymous";
        }
        return auth.getName();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}