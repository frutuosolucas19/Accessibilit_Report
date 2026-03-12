package com.example.acessibilit_report.model;

import java.io.Serializable;

public class AccessibilityRight implements Serializable {
    private String titulo;
    private String descricao;

    public AccessibilityRight(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }
}

