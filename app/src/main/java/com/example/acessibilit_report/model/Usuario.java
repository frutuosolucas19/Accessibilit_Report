package com.example.acessibilit_report.model;

public class Usuario {

    private Pessoa pessoa;
    private String tipoUsuario;
    private String email;
    private String senha;

    public Usuario(Pessoa pessoa, String tipoUsuario, String email, String senha) {
        this.pessoa = pessoa;
        this.tipoUsuario = tipoUsuario;
        this.email = email;
        this.senha = senha;
    }

    public Usuario() {
    }

    public Usuario(String email) {
        this.email = email;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
