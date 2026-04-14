package com.example.acessibilit_report.dto;

import com.example.acessibilit_report.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    public String accessToken;
    public String refreshToken;
    public long expiresIn;
    public User usuario;

    public LoginResponse() {}

    public String getAccessToken() {
        return accessToken;
    }

    public String getNome() {
        return usuario != null ? usuario.getNome() : null;
    }

    public String getEmail() {
        return usuario != null ? usuario.getEmail() : null;
    }

    public String getTipoUsuario() {
        return usuario != null ? usuario.getTipo() : null;
    }
}
