package configuracion;

public interface PasarDatosConciliacion {


     void cambiarEstadoVariable(String codCartera, boolean estado);

     void guardarObservacion(String codCartera, String codSeleccione, String descSeleccione, boolean primeraVez);

}
