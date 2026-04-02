package com.farma.treinamentos.dto.response;

import java.time.LocalDateTime;

public record AuditTrailResponse(
    String id,
    LocalDateTime dataHoraUtc,
    String tabelaAfetada,
    String registroId,
    String acao,
    String valoresAntigos,
    String valoresNovos,
    String motivoAlteracao,
    String ipOrigem,
    String utilizadorNome
) {}