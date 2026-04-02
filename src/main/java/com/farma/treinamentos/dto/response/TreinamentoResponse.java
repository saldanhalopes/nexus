package com.farma.treinamentos.dto.response;

import java.time.LocalDateTime;

public record TreinamentoResponse(
    String id,
    String titulo,
    String descricao,
    Integer cargaHoraria,
    String tipo,
    String nomePlataforma,
    String linkPlataforma,
    Boolean obrigatorio,
    String departamentoNome,
    Boolean ativo,
    LocalDateTime createdAt
) {}