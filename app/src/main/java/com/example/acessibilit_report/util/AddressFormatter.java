package com.example.acessibilit_report.util;

import com.example.acessibilit_report.model.Address;

public class AddressFormatter {

    private AddressFormatter() {}

    public static String format(Address e) {
        if (e == null) return "";
        StringBuilder sb = new StringBuilder();
        if (!isBlank(e.getLogradouro())) sb.append(e.getLogradouro());
        if (e.getNumero() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getNumero());
        }
        if (!isBlank(e.getBairro())) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(e.getBairro());
        }
        if (!isBlank(e.getCidade())) {
            if (sb.length() > 0) sb.append(" \u2022 ");
            sb.append(e.getCidade());
        }
        if (!isBlank(e.getUf())) {
            if (!isBlank(e.getCidade())) sb.append("/");
            sb.append(e.getUf());
        }
        return sb.toString();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
