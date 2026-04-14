package com.example.acessibilit_report.dto;

public class ResetPasswordRequest {
    public String email;
    public String codigo;
    public String novaSenha;
    public String confirmacaoNovaSenha;

    public ResetPasswordRequest(String email, String codigo, String novaSenha, String confirmacaoNovaSenha) {
        this.email = email;
        this.codigo = codigo;
        this.novaSenha = novaSenha;
        this.confirmacaoNovaSenha = confirmacaoNovaSenha;
    }
}
