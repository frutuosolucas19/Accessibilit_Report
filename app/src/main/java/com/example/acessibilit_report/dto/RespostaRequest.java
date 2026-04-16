package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaRequest {
    private String conteudo;

    public RespostaRequest(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getConteudo() { return conteudo; }
}
