package com.example.acessibilit_report.model;

import java.io.Serializable;
import java.util.Date;

public class StatusHistoricoDenuncia implements Serializable {

    private Status status;
    private Date horario;
    private Date data;
    private Historico historico;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }
}
