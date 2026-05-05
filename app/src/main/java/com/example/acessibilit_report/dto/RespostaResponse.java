package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaResponse implements Serializable {
    public Long id;
    public String conteudo;
    public Long perguntaId;
    public Long usuarioId;
    public String criadoEm;
    public String atualizadoEm;
}
