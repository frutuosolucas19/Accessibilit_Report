package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Forum implements Serializable {

    private Usuario usuario;

    public Forum() {
    }

    public Forum(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
