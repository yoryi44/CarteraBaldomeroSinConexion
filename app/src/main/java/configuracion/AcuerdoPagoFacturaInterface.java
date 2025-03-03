package configuracion;

import dataobject.Cuota;

public interface AcuerdoPagoFacturaInterface {

    /**
     * Metodo para guardar en la lista de cuotas
     */
    void guardarValorCuota(String documento, int cuota, float valorCuota, int posicionLayoutPadre);

    /**
     * Metodo para gurdar en la lista de cuotas, la fecha modificada de una cuota
     */
    void guardarFechaCuota(String documento, int cuota, String fecha, int posicionLayoutPadre);

}
