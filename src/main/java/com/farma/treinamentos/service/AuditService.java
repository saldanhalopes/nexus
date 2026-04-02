package com.farma.treinamentos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.farma.treinamentos.model.AuditTrail;

import com.farma.treinamentos.model.enums.AcaoAudit;
import com.farma.treinamentos.repository.AuditTrailRepository;
import com.farma.treinamentos.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditTrailRepository auditTrailRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditTrailRepository auditTrailRepository, 
                         UtilizadorRepository utilizadorRepository, 
                         ObjectMapper objectMapper) {
        this.auditTrailRepository = auditTrailRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void log(String userId, String ip, String tabela, String registroId, 
                    AcaoAudit acao, Object oldData, Object newData, String motivo) {
        try {
            AuditTrail audit = new AuditTrail();
            audit.setDataHoraUtc(LocalDateTime.now(ZoneOffset.UTC));
            audit.setTabelaAfetada(tabela);
            audit.setRegistroId(registroId);
            audit.setAcao(acao);
            audit.setIpOrigem(ip);
            audit.setMotivoAlteracao(motivo);

            if (oldData != null) {
                audit.setValoresAntigos(convertToMap(oldData));
            }
            if (newData != null) {
                audit.setValoresNovos(convertToMap(newData));
            }

            if (userId != null) {
                utilizadorRepository.findById(userId).ifPresent(audit::setUtilizador);
            }

            auditTrailRepository.save(audit);
            log.info("Audit log created: {} on {}:{}", acao, tabela, registroId);
        } catch (org.springframework.dao.DataAccessException | com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error creating audit log", e);
        }
    }

    private Map<String, Object> convertToMap(Object obj) throws com.fasterxml.jackson.core.JsonProcessingException {
        if (obj instanceof String) {
            return objectMapper.readValue((String) obj, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        }
        String json = objectMapper.writeValueAsString(obj);
        return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
    }
}