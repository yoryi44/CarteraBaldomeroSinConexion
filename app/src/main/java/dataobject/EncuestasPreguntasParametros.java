package dataobject;

import java.io.Serializable;

public class EncuestasPreguntasParametros implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int codParametro;
	public int codPregunta;
	public int valor;
	public String etiqueta;
	public String modoParametro;
	public String pregDestino;
	public boolean seleccionar = false;

	public int tipo;
}
