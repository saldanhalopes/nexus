package com.farma.treinamentos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroTreinamentoRequest(
    @NotBlank(message = "ID do utilizador é obrigatório")
    String utilizadorId,
    
    @NotBlank(message = "ID do treinamento é obrigatório")
    String treinamentoId,
    
    Double notaObtida,
    String observacoes
) {}