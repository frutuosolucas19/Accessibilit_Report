// com.example.acessibilit_report.model.Report.java
package com.example.acessibilit_report.model;

import java.io.Serializable;
import java.util.List;

public class Report implements Serializable {
    private Long id;
    private String nomeLocal;
    private Address endereco;
    private String problema;
    private String sugestao;
    private String emailUsuario;
    private String criadoEm;
    private List<Image> imagens;

    public Report() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeLocal() { return nomeLocal; }
    public void setNomeLocal(String nomeLocal) { this.nomeLocal = nomeLocal; }
    public Address getEndereco() { return endereco; }
    public void setEndereco(Address endereco) { this.endereco = endereco; }
    public String getProblema() { return problema; }
    public void setProblema(String problema) { this.problema = problema; }
    public String getSugestao() { return sugestao; }
    public void setSugestao(String sugestao) { this.sugestao = sugestao; }
    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }
    public String getCriadoEm() { return criadoEm; }
    public void setCriadoEm(String criadoEm) { this.criadoEm = criadoEm; }
    public List<Image> getImagens() { return imagens; }
    public void setImagens(List<Image> imagens) { this.imagens = imagens; }
}

