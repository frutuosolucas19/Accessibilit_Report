package com.example.acessibilit_report.dto;

import com.example.acessibilit_report.model.Local;
import com.example.acessibilit_report.model.Status;

public class DenunciaRequest {

    private Local local;
    private String problema;
    private String sugestao;
    private String imagem;
    private Status status;
    private String emailUsuario;

    public DenunciaRequest(Local local, String problema, String sugestao, String imagem, Status status, String emailUsuario) {
        this.local = local;
        this.problema = problema;
        this.sugestao = sugestao;
        this.imagem = imagem;
        this.status = status;
        this.emailUsuario = emailUsuario;
    }

    // Getters e Setters
    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getSugestao() {
        return sugestao;
    }

    public void setSugestao(String sugestao) {
        this.sugestao = sugestao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
}
