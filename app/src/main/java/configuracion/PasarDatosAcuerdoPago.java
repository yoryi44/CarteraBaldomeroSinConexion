package configuracion;

public interface PasarDatosAcuerdoPago {

    void valorTotalAcuerdoTotal(float valor, int cuota,boolean ultimaCuota, boolean edicion);

    /**
     * Metodo para gurdar en la lista de cuotas, la fecha modificada de una cuota
     */
    void guardarFechaCuota(int cuota, String fecha, boolean ultimaCuota);


}
