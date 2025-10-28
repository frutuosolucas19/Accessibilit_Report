// com.example.acessibilit_report.dto.DenunciaResponse.java
package com.example.acessibilit_report.dto;

import com.example.acessibilit_report.model.Endereco;
import com.example.acessibilit_report.model.Imagem;
import com.example.acessibilit_report.model.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DenunciaResponse implements Serializable {
    public Long id;
    public String nomeLocal;
    public Endereco endereco;
    public String problema;
    public String sugestao;
    public String emailUsuario;
    public String criadoEm;

    public Status status;

    public List<Imagem> imagens;
}
