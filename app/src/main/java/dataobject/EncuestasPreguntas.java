package dataobject;

import java.io.Serializable;

public class EncuestasPreguntas implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int codPregunta;
	public int codEncuesta;
	public int tipoPregunta;
	public String titulo;
	public int verificable;
	public String paramNumerico;
	public int obligatoria;
}
