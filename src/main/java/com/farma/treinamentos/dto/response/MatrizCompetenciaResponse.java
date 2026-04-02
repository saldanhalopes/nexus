package com.farma.treinamentos.dto.response;

import java.time.LocalDateTime;

public record MatrizCompetenciaResponse(
    String id,
    String nome,
    String descricao,
    Integer anoReferencia,
    String competencias,
    String departamentoNome,
    String cargoNome,
    Boolean ativa,
    LocalDateTime createdAt,
    AssinaturaEletronicaResponse assinaturaElaboracao,
    AssinaturaEletronicaResponse assinaturaRevisao,
    AssinaturaEletronicaResponse assinaturaAprovacao
) {}