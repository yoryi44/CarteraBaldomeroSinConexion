package dataobject;

public class CuentasBanco {

    public String NombreCuenta;
    public String DescripcionCuenta;
    public String empresaBanco;

    public String getNombreCuenta() {
        return NombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        NombreCuenta = nombreCuenta;
    }

    public String getDescripcionCuenta() {
        return DescripcionCuenta;
    }

    public void setDescripcionCuenta(String descripcionCuenta) {
        DescripcionCuenta = descripcionCuenta;
    }

    public String getEmpresaBanco() {
        return empresaBanco;
    }

    public void setEmpresaBanco(String empresaBanco) {
        this.empresaBanco = empresaBanco;
    }
}
