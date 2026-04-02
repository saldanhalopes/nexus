package com.farma.treinamentos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TreinamentoRequest(
    @NotBlank(message = "Título é obrigatório")
    String titulo,
    
    String descricao,
    Integer cargaHoraria,
    String tipo,
    String nomePlataforma,
    String linkPlataforma,
    String conteudoProgramatico,
    Boolean obrigatorio,
    String departamentoId
) {}