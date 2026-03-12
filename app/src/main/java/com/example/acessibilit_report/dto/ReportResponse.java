// com.example.acessibilit_report.dto.ReportResponse.java
package com.example.acessibilit_report.dto;

import com.example.acessibilit_report.model.Address;
import com.example.acessibilit_report.model.Image;
import com.example.acessibilit_report.model.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportResponse implements Serializable {
    public Long id;
    public String nomeLocal;
    public Address endereco;
    public String problema;
    public String sugestao;
    public String emailUsuario;
    public String criadoEm;

    public Status status;

    public List<Image> imagens;
}

