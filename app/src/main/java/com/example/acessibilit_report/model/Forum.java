package com.example.acessibilit_report.model;

import java.io.Serializable;

public class Forum implements Serializable {

    private User usuario;

    public Forum() {
    }

    public Forum(User usuario) {
        this.usuario = usuario;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }
}

