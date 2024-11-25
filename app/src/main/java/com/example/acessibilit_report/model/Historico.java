package com.example.acessibilit_report.model;

import java.io.Serializable;
import java.util.List;

public class Historico implements Serializable {

    private List<StatusHistoricoDenuncia> statusHistoricoDenuncias;

    public List<StatusHistoricoDenuncia> getStatusHistoricoDenuncias() {
        return statusHistoricoDenuncias;
    }

    public void setStatusHistoricoDenuncias(List<StatusHistoricoDenuncia> statusHistoricoDenuncias) {
        this.statusHistoricoDenuncias = statusHistoricoDenuncias;
    }

    public Historico(List<StatusHistoricoDenuncia> statusHistoricoDenuncias) {
        this.statusHistoricoDenuncias = statusHistoricoDenuncias;
    }
}
