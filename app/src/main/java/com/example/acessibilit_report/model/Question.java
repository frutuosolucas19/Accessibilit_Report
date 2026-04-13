package com.example.acessibilit_report.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

    private String pergunta;
    private Forum forum;

    public Question() {
    }

    private List<Answer> respostas = new ArrayList<>();

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

    public List<Answer> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<Answer> respostas) {
        this.respostas = respostas;
    }

    public String getNomeAutor() {
        if (forum != null && forum.getUsuario() != null) {
            String nome = forum.getUsuario().getNome();
            return nome != null ? nome : "Anônimo";
        }
        return "Anônimo";
    }

    public String getFotoAutor() {
        if (forum != null && forum.getUsuario() != null) {
            return forum.getUsuario().getFotoPerfil();
        }
        return null;
    }

    public Question(String pergunta, Forum forum) {
        this.pergunta = pergunta;
        this.forum = forum;
    }

    public void adicionarResposta(Answer resposta) {
        resposta.setPergunta(this);
        respostas.add(resposta);
    }
}

