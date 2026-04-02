package com.farma.treinamentos.controller;

import com.farma.treinamentos.dto.response.AuditTrailResponse;
import com.farma.treinamentos.model.AuditTrail;
import com.farma.treinamentos.model.enums.AcaoAudit;
import com.farma.treinamentos.repository.AuditTrailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditTrailRepository auditTrailRepository;

    public AuditController(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }

    @GetMapping
    public ResponseEntity<Page<AuditTrailResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(auditTrailRepository.findAllOrdered(pageable).map(this::toResponse));
    }

    @GetMapping("/utilizador/{utilizadorId}")
    public ResponseEntity<List<AuditTrailResponse>> findByUtilizador(@PathVariable String utilizadorId) {
        return ResponseEntity.ok(auditTrailRepository.findByUtilizadorId(utilizadorId).stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/tabela/{tabela}")
    public ResponseEntity<List<AuditTrailResponse>> findByTabela(@PathVariable String tabela) {
        return ResponseEntity.ok(auditTrailRepository.findByTabela(tabela).stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/registro/{registroId}")
    public ResponseEntity<List<AuditTrailResponse>> findByRegistro(@PathVariable String registroId) {
        return ResponseEntity.ok(auditTrailRepository.findByRegistroId(registroId).stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<AuditTrailResponse>> findByPeriodo(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim) {
        return ResponseEntity.ok(auditTrailRepository.findByPeriodo(inicio, fim).stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/acao/{acao}")
    public ResponseEntity<Page<AuditTrailResponse>> findByAcao(
            @PathVariable AcaoAudit acao, 
            Pageable pageable) {
        return ResponseEntity.ok(auditTrailRepository.findByAcao(acao, pageable).map(this::toResponse));
    }

    private AuditTrailResponse toResponse(AuditTrail audit) {
        return new AuditTrailResponse(
            audit.getId(),
            audit.getDataHoraUtc(),
            audit.getTabelaAfetada(),
            audit.getRegistroId(),
            audit.getAcao().name(),
            audit.getValoresAntigos() != null ? audit.getValoresAntigos().toString() : null,
            audit.getValoresNovos() != null ? audit.getValoresNovos().toString() : null,
            audit.getMotivoAlteracao(),
            audit.getIpOrigem(),
            audit.getUtilizador() != null ? audit.getUtilizador().getNomeCompleto() : null
        );
    }
}