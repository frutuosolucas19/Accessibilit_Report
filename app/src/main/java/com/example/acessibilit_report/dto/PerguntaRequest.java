package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PerguntaRequest {
    private String titulo;
    private String conteudo;

    public PerguntaRequest(String titulo, String conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    public String getTitulo() { return titulo; }
    public String getConteudo() { return conteudo; }
}
