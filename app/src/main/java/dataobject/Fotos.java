package dataobject;

import java.io.Serializable;

public class Fotos implements Serializable {

    public String idFoto;
    public String nombreFoto;
    public String idPagoFoto;
    public String empresa;
    public boolean estadoFoto;
    public String idenFoto;

    public Fotos() {
    }

    public Fotos(String idFoto, String nombreFoto, String idPagoFoto, String empresa, boolean estadoFoto, String idenFoto) {
        this.idFoto = idFoto;
        this.nombreFoto = nombreFoto;
        this.idPagoFoto = idPagoFoto;
        this.empresa = empresa;
        this.estadoFoto = estadoFoto;
        this.idenFoto = idenFoto;
    }

    public String getIdenFoto() {
        return idenFoto;
    }

    public void setIdenFoto(String idenFoto) {
        this.idenFoto = idenFoto;
    }

    public boolean isEstadoFoto() {
        return estadoFoto;
    }

    public void setEstadoFoto(boolean estadoFoto) {
        this.estadoFoto = estadoFoto;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getNombreFoto() {
        return nombreFoto;
    }

    public void setNombreFoto(String nombreFoto) {
        this.nombreFoto = nombreFoto;
    }

    public String getIdPagoFoto() {
        return idPagoFoto;
    }

    public void setIdPagoFoto(String idPagoFoto) {
        this.idPagoFoto = idPagoFoto;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
