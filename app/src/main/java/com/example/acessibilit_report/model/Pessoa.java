package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Pessoa implements Serializable {

    private Long id;
    private String nome;
    private String imagem;

    public Pessoa() {
    }

    public Pessoa(String nome, String imagem) {
        this.nome=nome;
        this.imagem=imagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

}
