package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    public String nome;
    public String email;
    @JsonProperty("tipo")
    public String tipoUsuario;
    public String accessToken;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LoginResponse() {
    }

    public LoginResponse(String nome, String email, String tipoUsuario, String accessToken) {
        this.nome = nome;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.accessToken = accessToken;
    }
}