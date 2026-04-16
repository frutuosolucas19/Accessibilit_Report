package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PerguntaResponse implements Serializable {
    public Long id;
    public String titulo;
    public String conteudo;
    public Long forumId;
    public Long usuarioId;
    public int totalRespostas;
    public String criadoEm;
    public String atualizadoEm;
}
