package dataobject;

public class FormaPago {

    public boolean parcial;
    public String total;
    public double valor;
    public String observacionesMotivo;
    public String observaciones;

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isParcial() {
        return parcial;
    }

    public void setParcial(boolean parcial) {
        this.parcial = parcial;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getObservacionesMotivo() {
        return observacionesMotivo;
    }

    public void setObservacionesMotivo(String observacionesMotivo) {
        this.observacionesMotivo = observacionesMotivo;
    }
}
