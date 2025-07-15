package com.example.acessibilit_report.dto;

public class LoginResponse {
    public String nome;
    public String email;
    public String tipoUsuario;

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
}