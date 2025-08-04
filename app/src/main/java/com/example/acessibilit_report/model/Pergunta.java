package com.example.acessibilit_report.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pergunta implements Serializable {

    private String pergunta;
    private Forum forum;

    public Pergunta() {
    }

    private List<Resposta> respostas = new ArrayList<>();

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

    public List<Resposta> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<Resposta> respostas) {
        this.respostas = respostas;
    }

    public String getNomeAutor() {
        if (forum != null && forum.getUsuario() != null && forum.getUsuario().getPessoa() != null) {
            return forum.getUsuario().getPessoa().getNome();
        }
        return "Anônimo";
    }

    public String getFotoAutor() {
        if (forum != null && forum.getUsuario() != null && forum.getUsuario().getPessoa() != null) {
            return forum.getUsuario().getPessoa().getImagem();
        }
        return null;
    }

    public Pergunta(String pergunta, Forum forum) {
        this.pergunta = pergunta;
        this.forum = forum;
    }

    public void adicionarResposta(Resposta resposta) {
        resposta.setPergunta(this);
        respostas.add(resposta);
    }
}
