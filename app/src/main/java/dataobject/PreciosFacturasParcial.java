package dataobject;

import androidx.annotation.NonNull;

public class PreciosFacturasParcial {
    public double valor;
    public double valorobtenido;


    public PreciosFacturasParcial() {
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValorobtenido() {
        return valorobtenido;
    }

    public void setValorobtenido(double valorobtenido) {
        this.valorobtenido = valorobtenido;
    }
}
