package com.farma.treinamentos.model;

import com.farma.treinamentos.model.enums.TipoAssinatura;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "assinaturas_eletronicas")
public class AssinaturaEletronica {

    @Id
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    @Column(nullable = false)
    private String nomeSignatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAssinatura tipo;

    @Column(name = "data_hora", nullable = false)
    @CreationTimestamp
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String significado;

    @Column(name = "conteudo_hash", nullable = false)
    private String conteudoHash;

    @Column(name = "ip_origem")
    private String ipOrigem;

    public AssinaturaEletronica() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Utilizador getUtilizador() { return utilizador; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }

    public String getNomeSignatario() { return nomeSignatario; }
    public void setNomeSignatario(String nomeSignatario) { this.nomeSignatario = nomeSignatario; }

    public TipoAssinatura getTipo() { return tipo; }
    public void setTipo(TipoAssinatura tipo) { this.tipo = tipo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getSignificado() { return significado; }
    public void setSignificado(String significado) { this.significado = significado; }

    public String getConteudoHash() { return conteudoHash; }
    public void setConteudoHash(String conteudoHash) { this.conteudoHash = conteudoHash; }

    public String getIpOrigem() { return ipOrigem; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }
}
