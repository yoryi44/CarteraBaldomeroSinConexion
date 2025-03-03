package dataobject;

public class AcuerdoPago {

    public String idAcuerdo;
    public String codCliente;
    public String usuario;
    public int tipoAcuerdo;
    public float valor;
    public String fechaCierre;
    public String fecha;
    public int estado;
    public String estadoDescripcion;
    public String observacion;
    public String total;
    public String documento;
    public int cuotas;
    public String nombre;
    public int Validado;
    public int gestion;
    public int estadoCuota;

    @Override
    public String toString() {
        return idAcuerdo ;
    }
}
