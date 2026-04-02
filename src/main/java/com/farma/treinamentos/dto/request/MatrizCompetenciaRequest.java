package com.farma.treinamentos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MatrizCompetenciaRequest(
    @NotBlank(message = "Nome é obrigatório")
    String nome,
    
    String descricao,
    
    @NotNull(message = "Ano de referência é obrigatório")
    Integer anoReferencia,
    
    String competencias,
    
    String departamentoId,
    
    String cargoId,

    @NotBlank(message = "Senha de assinatura é obrigatória")
    String senha,
    
    @NotBlank(message = "Motivo da alteração é obrigatório")
    String motivo
) {}