// com.example.acessibilit_report.dto.ReportRequest.java
package com.example.acessibilit_report.dto;

import java.io.Serializable;
import java.util.List;

public class ReportRequest implements Serializable {
    public String nomeLocal;
    public AddressRequest endereco;
    public String problema;
    public String sugestao;
    public List<ImageRequest> imagens;

    public static class AddressRequest implements Serializable {
        public String logradouro;
        public Integer numero;
        public String bairro;
        public String cidade;
        public String uf;
        public String cep;
        public String complemento;
    }

    public static class ImageRequest implements Serializable {
        public String base64;
        public String contentType;
        public String filename;
        public Integer ordem;
    }
}

