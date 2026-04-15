package com.example.acessibilit_report.dto;

import java.io.Serializable;

public class ReportRequest implements Serializable {
    public String titulo;
    public String descricao;
    public String tipo;
    public String sugestao;
    public AddressRequest endereco;

    public static class AddressRequest implements Serializable {
        public String logradouro;
        public Integer numero;
        public String bairro;
        public String cidade;
        public String uf;
        public String cep;
        public String complemento;
    }
}
