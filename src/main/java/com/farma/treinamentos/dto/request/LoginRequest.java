package com.farma.treinamentos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "Email é obrigatório")
    String email,
    
    @NotBlank(message = "Senha é obrigatória")
    String senha
) {}