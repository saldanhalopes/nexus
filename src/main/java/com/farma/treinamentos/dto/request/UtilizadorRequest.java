package com.farma.treinamentos.dto.request;

import com.farma.treinamentos.model.enums.NivelAcesso;
import com.farma.treinamentos.model.enums.StatusUtilizador;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UtilizadorRequest(
    @NotBlank(message = "Matrícula é obrigatória")
    String matricula,
    
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    String nomeCompleto,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    String email,
    
    String cargoId,
    Long cargoLongId,
    String departamentoId,
    
    NivelAcesso nivelAcesso,
    StatusUtilizador status
) {}