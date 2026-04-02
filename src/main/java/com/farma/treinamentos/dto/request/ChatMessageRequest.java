package com.farma.treinamentos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
    @NotBlank(message = "Mensagem é obrigatória")
    String message
) {}
