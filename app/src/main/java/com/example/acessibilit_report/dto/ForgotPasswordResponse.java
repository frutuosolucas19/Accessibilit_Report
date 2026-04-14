package com.example.acessibilit_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotPasswordResponse {
    public String mensagem;
}
