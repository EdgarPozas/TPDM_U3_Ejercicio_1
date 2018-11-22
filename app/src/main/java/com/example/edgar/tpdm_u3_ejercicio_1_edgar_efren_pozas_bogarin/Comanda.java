package com.example.edgar.tpdm_u3_ejercicio_1_edgar_efren_pozas_bogarin;

import java.util.ArrayList;

public class Comanda {
    private String id_comanda;
    private String fecha;
    private String estatus;
    private String total;
    private String no_mesa;

    public String getId_comanda() {
        return id_comanda;
    }

    public void setId_comanda(String id_comanda) {
        this.id_comanda = id_comanda;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNo_mesa() {
        return no_mesa;
    }

    public void setNo_mesa(String no_mesa) {
        this.no_mesa = no_mesa;
    }
}
