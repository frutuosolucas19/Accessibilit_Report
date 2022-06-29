package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Pessoa implements Serializable {

    private String nome;
    private String usuario;
    private String email;
    private String imagem;


    public Pessoa(String nome, String usuario, String email, String imagem) {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

}
