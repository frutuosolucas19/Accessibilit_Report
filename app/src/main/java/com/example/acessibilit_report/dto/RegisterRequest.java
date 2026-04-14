package com.example.acessibilit_report.dto;

public class RegisterRequest {
    public String nome;
    public String email;
    public String senha;
    public String confirmacaoSenha;
    public String tipoUsuario = "CLIENTE";

    public RegisterRequest(String nome, String email, String senha, String confirmacaoSenha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmacaoSenha = confirmacaoSenha;
    }
}
