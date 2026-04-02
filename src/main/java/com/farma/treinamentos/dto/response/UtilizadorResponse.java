package com.farma.treinamentos.dto.response;

import com.farma.treinamentos.model.enums.NivelAcesso;
import com.farma.treinamentos.model.enums.StatusUtilizador;

import java.time.LocalDateTime;

public record UtilizadorResponse(
    String id,
    String matricula,
    String nomeCompleto,
    String email,
    StatusUtilizador status,
    NivelAcesso nivelAcesso,
    String cargoNome,
    String departamentoNome,
    LocalDateTime createdAt,
    LocalDateTime ultimoLogin
) {}