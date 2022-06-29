package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Resposta implements Serializable {

    private String resposta;
    private Pergunta pergunta;

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public void setPergunta(Pergunta pergunta) {
        this.pergunta = pergunta;
    }
}
