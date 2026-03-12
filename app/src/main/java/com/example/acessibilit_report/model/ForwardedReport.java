package com.example.acessibilit_report.model;

import java.io.Serializable;

public class ForwardedReport implements Serializable {

    private int id;
    private Report denuncia;
    private String destinatario;
    private String assunto;
    private String mensagem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Report getDenuncia() {
        return denuncia;
    }

    public void setDenuncia(Report denuncia) {
        this.denuncia = denuncia;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}

