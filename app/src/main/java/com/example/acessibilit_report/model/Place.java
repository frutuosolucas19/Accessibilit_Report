package com.example.acessibilit_report.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Place implements Serializable {

    private String nome;
    private GeoLocation localizacao;
    private Address endereco;

    public Place() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public GeoLocation getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(GeoLocation localizacao) {
        this.localizacao = localizacao;
    }

    public Address getEndereco() {
        return endereco;
    }

    public void setEndereco(Address endereco) {
        this.endereco = endereco;
    }
}

