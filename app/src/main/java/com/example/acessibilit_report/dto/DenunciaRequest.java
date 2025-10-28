// com.example.acessibilit_report.dto.DenunciaRequest.java
package com.example.acessibilit_report.dto;

import java.io.Serializable;
import java.util.List;

public class DenunciaRequest implements Serializable {
    public String nomeLocal;
    public EnderecoRequest endereco;
    public String problema;
    public String sugestao;
    public List<ImagemRequest> imagens;

    public static class EnderecoRequest implements Serializable {
        public String logradouro;
        public Integer numero;
        public String bairro;
        public String cidade;
        public String uf;
        public String cep;
        public String complemento;
    }

    public static class ImagemRequest implements Serializable {
        public String base64;
        public String contentType;
        public String filename;
        public Integer ordem;
    }
}
