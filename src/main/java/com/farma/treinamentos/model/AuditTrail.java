package com.farma.treinamentos.model;

import com.farma.treinamentos.model.enums.AcaoAudit;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "audit_trail", indexes = {
    @Index(name = "idx_audit_data_hora", columnList = "data_hora_utc"),
    @Index(name = "idx_audit_tabela", columnList = "tabela_afetada"),
    @Index(name = "idx_audit_usuario", columnList = "utilizador_id")
})
public class AuditTrail {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "data_hora_utc", nullable = false)
    private LocalDateTime dataHoraUtc = LocalDateTime.now(java.time.ZoneOffset.UTC);

    @Column(name = "tabela_afetada", nullable = false)
    private String tabelaAfetada;

    @Column(name = "registro_id", nullable = false)
    private String registroId;

    @Enumerated(EnumType.STRING)
    private AcaoAudit acao;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> valoresAntigos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> valoresNovos;

    @Column(name = "motivo_alteracao")
    private String motivoAlteracao;

    @Column(name = "ip_origem")
    private String ipOrigem;

    @Column(name = "user_agent")
    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilizador_id")
    private Utilizador utilizador;

    public AuditTrail() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getDataHoraUtc() { return dataHoraUtc; }
    public void setDataHoraUtc(LocalDateTime dataHoraUtc) { this.dataHoraUtc = dataHoraUtc; }

    public String getTabelaAfetada() { return tabelaAfetada; }
    public void setTabelaAfetada(String tabelaAfetada) { this.tabelaAfetada = tabelaAfetada; }

    public String getRegistroId() { return registroId; }
    public void setRegistroId(String registroId) { this.registroId = registroId; }

    public AcaoAudit getAcao() { return acao; }
    public void setAcao(AcaoAudit acao) { this.acao = acao; }

    public Map<String, Object> getValoresAntigos() { return valoresAntigos; }
    public void setValoresAntigos(Map<String, Object> valoresAntigos) { this.valoresAntigos = valoresAntigos; }

    public Map<String, Object> getValoresNovos() { return valoresNovos; }
    public void setValoresNovos(Map<String, Object> valoresNovos) { this.valoresNovos = valoresNovos; }

    public String getMotivoAlteracao() { return motivoAlteracao; }
    public void setMotivoAlteracao(String motivoAlteracao) { this.motivoAlteracao = motivoAlteracao; }

    public String getIpOrigem() { return ipOrigem; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Utilizador getUtilizador() { return utilizador; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }
}