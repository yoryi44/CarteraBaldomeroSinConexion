package dataobject;

import java.io.Serializable;

public class Encuestas implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String codigo;
	public String titulo;
	public int obligatoria;
	public int tipoEncuesta;
}
