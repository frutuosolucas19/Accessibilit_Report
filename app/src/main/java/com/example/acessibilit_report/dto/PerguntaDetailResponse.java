package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PerguntaDetailResponse implements Serializable {
    public Long id;
    public String titulo;
    public String conteudo;
    public Long forumId;
    public Long usuarioId;
    public List<RespostaResponse> respostas = new ArrayList<>();
    public String criadoEm;
    public String atualizadoEm;
}
