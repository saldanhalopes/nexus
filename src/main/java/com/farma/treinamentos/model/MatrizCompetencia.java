package com.farma.treinamentos.model;

import com.farma.treinamentos.model.enums.StatusTreinamento;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "matrizes_competencia")
public class MatrizCompetencia {

    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "ano_referencia")
    private Integer anoReferencia;

    @Column(columnDefinition = "jsonb")
    private String competencias;

    private boolean ativa = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por")
    private Utilizador criadoPor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assinatura_elaboracao_id")
    private AssinaturaEletronica assinaturaElaboracao;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assinatura_revisao_id")
    private AssinaturaEletronica assinaturaRevisao;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assinatura_aprovacao_id")
    private AssinaturaEletronica assinaturaAprovacao;

    public MatrizCompetencia() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getAnoReferencia() { return anoReferencia; }
    public void setAnoReferencia(Integer anoReferencia) { this.anoReferencia = anoReferencia; }

    public String getCompetencias() { return competencias; }
    public void setCompetencias(String competencias) { this.competencias = competencias; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public Utilizador getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Utilizador criadoPor) { this.criadoPor = criadoPor; }

    public AssinaturaEletronica getAssinaturaElaboracao() { return assinaturaElaboracao; }
    public void setAssinaturaElaboracao(AssinaturaEletronica assinaturaElaboracao) { this.assinaturaElaboracao = assinaturaElaboracao; }

    public AssinaturaEletronica getAssinaturaRevisao() { return assinaturaRevisao; }
    public void setAssinaturaRevisao(AssinaturaEletronica assinaturaRevisao) { this.assinaturaRevisao = assinaturaRevisao; }

    public AssinaturaEletronica getAssinaturaAprovacao() { return assinaturaAprovacao; }
    public void setAssinaturaAprovacao(AssinaturaEletronica assinaturaAprovacao) { this.assinaturaAprovacao = assinaturaAprovacao; }
}