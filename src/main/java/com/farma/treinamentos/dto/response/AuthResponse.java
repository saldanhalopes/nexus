package com.farma.treinamentos.dto.response;

import java.time.LocalDateTime;

public record AuthResponse(
    String token,
    String type,
    Long expiresIn,
    UtilizadorResponse usuario
) {
    public record UtilizadorResponse(
        String id,
        String nomeCompleto,
        String email,
        String nivelAcesso
    ) {}
}