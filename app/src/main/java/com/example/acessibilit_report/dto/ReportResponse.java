// com.example.acessibilit_report.dto.ReportResponse.java
package com.example.acessibilit_report.dto;

import com.example.acessibilit_report.model.Address;
import com.example.acessibilit_report.model.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportResponse implements Serializable {
    public Long id;
    public String titulo;
    public String descricao;
    public String sugestao;
    public String tipo;
    public String status;
    public Long usuarioId;
    public Address endereco;
    public Double latitude;
    public Double longitude;
    public List<Image> imagens;
    public String criadoEm;
    public String atualizadoEm;
}
