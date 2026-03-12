package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Answer implements Serializable {

    private String resposta;
    private Question pergunta;

    public Answer() {
    }

    public Answer(String resposta) {
        this.resposta = resposta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Question getPergunta() {
        return pergunta;
    }

    public void setPergunta(Question pergunta) {
        this.pergunta = pergunta;
    }
}

