package dataobject;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class ItemListView implements Serializable {

	private static final long serialVersionUID = 6L;
	
	public String titulo;
	public String subTitulo;
	public String referencia;
	public String documento;
	public int colorTitulo;
	public int icono;
	public BluetoothDevice device;
	public int state;
	
	/**
	 * permite definir si una fila 
	 * debe ir resaltada.
	 * 0 = no remarcada,
	 * 1 = remarcada.
	 */
	public int resaltado = 0;
	
	//PEDIDO VENDEDOR
    public String descripcion;
	public String codigo;
	public int cantidad;
	public int cantidadVendedor;


	public String contrasenaNueva;
	public String fechacontrasena;
}