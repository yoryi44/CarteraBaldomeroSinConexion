package dataobject;

/**
 * Clase para representar un cuota de un acuerdo de pago
 * @author Mateo Cano Alfonso
 */
public class Cuota {

    /**
     * Variable para guardar el identificador del acuerdo de pago al que pertenece
     * la cuota
     */
    private String idAcuerdo;

    /**
     * Variable que guarda el documento al que pertence la factura
     */
    private String documento;

    /**
     * Cadena para guardar una cadena con el valor de la cuota
     */
    private String totalCuota;

    /**
     * valor de la cuota
     */
    private float valor;

    /**
     * Fecha de pago de la cuota
     */
    private String fecha;

    /**
     * Variable booleana para verificar que la cuota se guardo en la lista temporal que contiene
     * las cuotas del acuerdo de pago para evitar que se repitan en el recyclerView donde se
     * pintan la cuotas
     */
    private boolean guardadoTMP;


    private int numeroCuota;


    public String getIdAcuerdo() {
        return idAcuerdo;
    }

    public void setIdAcuerdo(String idAcuerdo) {
        this.idAcuerdo = idAcuerdo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTotalCuota() {
        return totalCuota;
    }

    public void setTotalCuota(String totalCuota) {
        this.totalCuota = totalCuota;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isGuardadoTMP() {
        return guardadoTMP;
    }

    public void setGuardadoTMP(boolean guardadoTMP) {
        this.guardadoTMP = guardadoTMP;
    }

    public int getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(int numeroCuota) {
        this.numeroCuota = numeroCuota;
    }
}
