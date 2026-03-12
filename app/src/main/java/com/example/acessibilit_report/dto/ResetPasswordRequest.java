package com.example.acessibilit_report.dto;

public class ResetPasswordRequest {
    public String tokenAws;
    public String novaSenha;

    public ResetPasswordRequest(String tokenAws, String novaSenha) {
        this.tokenAws = tokenAws;
        this.novaSenha = novaSenha;
    }
}
