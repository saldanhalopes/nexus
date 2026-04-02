package com.farma.treinamentos.dto.response;

import java.time.LocalDateTime;

public record AssinaturaEletronicaResponse(
    String id,
    String nomeSignatario,
    String tipo,
    LocalDateTime dataHora,
    String significado
) {}
