package com.example.acessibilit_report.model;

public class Usuario {

    private Pessoa pessoa;
    private String tipoUsuario;
    private String login;
    private String senha;

    public Usuario(Pessoa pessoa, String tipoUsuario, String login, String senha) {
        this.pessoa = pessoa;
        this.tipoUsuario = tipoUsuario;
        this.login = login;
        this.senha = senha;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
