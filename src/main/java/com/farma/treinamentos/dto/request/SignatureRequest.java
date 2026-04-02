package com.farma.treinamentos.dto.request;

import com.farma.treinamentos.model.enums.TipoAssinatura;

public class SignatureRequest {

    private String password;
    private String significado;
    private TipoAssinatura tipo;
    private String targetId;

    public SignatureRequest() {}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSignificado() { return significado; }
    public void setSignificado(String significado) { this.significado = significado; }

    public TipoAssinatura getTipo() { return tipo; }
    public void setTipo(TipoAssinatura tipo) { this.tipo = tipo; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
}
