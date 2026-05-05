package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumResponse implements Serializable {
    public Long id;
    public String titulo;
    public String descricao;
    public Long usuarioId;
    public int totalPerguntas;
    public String criadoEm;
    public String atualizadoEm;
}
