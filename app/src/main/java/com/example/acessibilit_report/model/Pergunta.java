package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Pergunta implements Serializable {

    private String pergunta;
    private Forum forum;

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
}
