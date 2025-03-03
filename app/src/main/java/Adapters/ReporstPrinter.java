package Adapters;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;

import utilidades.Utilidades;


/**
 * Clase encargada de contener los metodos los cuales generan las tirillas de
 * impresion
 * 
 * @author Juan Carlos Hidalgo <Celuweb>
 * 
 */
public class ReporstPrinter {

	// atribuctos necesarios para la impresion
	ProgressDialog progressDialog;
	protected static int _splashTime = 1000;
	protected static int _splashTime2 = 4000;

	static Thread splashTread;

	public static float valorRecogido = 0;
	public static float valorNoRecogido = 0;
	public static float valorDevolucionParcial = 0;



	// constructor de la clase
	public ReporstPrinter() {
	}



	/**
	 * Metodo encargado de generar la tirilla de prueba (Establecer Impresora)
	 * 
	 * @return
	 */
	public static String formatoPrueba() {

		char ret1 = 13;
		char ret2 = 10;

		String ret = String.valueOf(ret1) + String.valueOf(ret2);
		String ESC = "\u001B";
		String fontSmall = ESC + "w" + "\u0025";// font char small block MF226
												// %(25H)
		String formato = fontSmall;
		formato += "";
		formato += fontSmall + Utilidades.CentrarLinea("Pepsico Rutas Directas Autoventa", 42) + ret;
		formato += Utilidades.CentrarLinea("Fecha: " + Utilidades.fechaActual("yyyy-MM-dd HH:mm:ss"), 42) + ret + ret;
		formato += Utilidades.CentrarLinea("TIRILLA DE PRUEBAS", 42) + ret + ret;
		formato += Utilidades.lpad("", 42, "-") + ret + ret;
		
		return formato;
	}

	/**
	 * Metodo encargado de generar la tirilla de entrega(Establecer Impresora)
	 *
	 * @return
	 * @throws WriterException
	 */
//	public static String formatoTirillaEntrega1(Encabezado encabezado, Cliente cliente, Vendedor vendedor, boolean isCopia) throws WriterException {
//
//		String strPrint;
//
//		char ret1 = 13;
//		char ret2 = 10;
//
//		int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
//
//		spaceLine1 = 25;
//		spaceLine2 = spaceLine1 * 2;
//		spaceLine3 = spaceLine1 * 3;
//		spaceLine4 = spaceLine1 * 4;
//
//		font = 0;
//		size = 2;
//		posX = 0;
//		posY = 10;
//		cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
//
//		String enter = String.valueOf(ret1) + ret2;
//
//		strPrint = "";
//
//
//		String formato ="";
//
//		String[] arreglo=Utilidades.split("", "38");
//		for(int i=0;i<arreglo.length;i++){
//			formato =  Utilidades.CentrarLinea(arreglo[i], 38);
//			strPrint += formato + enter;
//			if (i+1==arreglo.length) {
//				posY += spaceLine2;
//			}else {
//				posY += spaceLine1;
//			}
//		}
//
//		String c1 = "";
//		String c2 = "";
//
//
//
//		String codigoPuntoventa = cliente.puntoVentaId;
//		String infoTirilla = DataBaseBO.infoTirlla(codigoPuntoventa);
//		String infoTirilla2 = DataBaseBO.infoTirlla2();
//		InfoAdicional infoAdicional = DataBaseBO.infoTirllaAdicional();
//
//
//
//		formato =  "Certificado de Recoleccion de Aceite Vegetal Usado ";
//		strPrint +=  formato + enter;
//		posY += spaceLine2;
//
//
//		formato =  Util.rpad("", 30,"-");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//
//		formato =  infoTirilla;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  infoTirilla2;
//		strPrint +=  formato + enter;
//		posY += spaceLine2;
//
////		formato =  Util.CentrarLinea("MANOS VERDES", 30);
////		strPrint += formato + enter;
////		posY += spaceLine3;
//
//		formato =  Util.lpad("", 42, "");
//		strPrint +=  formato + enter;
//		posY += spaceLine2;
//
//
//		/*********************************************INFORMACION CLIENTE************************************************************/
//
//
//		formato =  Util.rpad("", 30,"-");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  Util.CentrarLinea("Informacion del cliente", 30);
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  Util.rpad("", 30,"-");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Codigo PDV: " + cliente.puntoVentaId;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato = "Nombre Sucursal: " + cliente.nombreSucursal;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Marca: " + cliente.MarcaSucursal;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Nombre Cliente: " + cliente.nombreManosVerdes;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Ciudad: " + cliente.ciudad;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Direccion PDV: " + cliente.direccion;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		/*formato =  "Codigo Proveedor: " + cliente.ProveedorId;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;*/
//
//		/*formato =  "Secuencia: " + cliente.secuencia;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;*/
//
//		/*formato =  "Ciudad: " + cliente.ciudad;
//		strPrint += formato + enter;
//		posY += spaceLine1;*/
//
//		/*formato =  "Direccion: " + cliente.direccion;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;*/
//
//		/*formato =  "Centro Comercial: " + cliente.centroComercial;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;*/
//
//		formato =  "Telefono: " + cliente.telefono;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  Util.lpad("", 42, "");
//		strPrint +=  formato + enter;
//		posY += spaceLine2;
//
//
//		/*********************************************INORMACION DATOS DE RECOLECCION************************************************************/
//
//		formato =  Util.rpad("", 30,"-");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  Util.CentrarLinea("Datos de recoleccion", 30);
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  Util.rpad("", 30,"-");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Recoleccion N.: " + encabezado.RecoleccionId;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Entrego: " + encabezado.NombreEncargado;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Recogio: " + vendedor.nombre;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//
//		formato =  "Placa: " + cliente.Placa;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//
//
////		formato =  Util.lpad("", 42, "");
////		strPrint +=  formato + enter;
////		posY += spaceLine1;
//
//		formato =  "Fecha Recogida: " + Util.FechaActual("yyyy-MM-dd");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Hora: " + Util.FechaActual("HH:mm:ss");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  "Bidones ACU Recolectados: " + encabezado.CantidadBidones;
//		strPrint +=  formato + enter;
//		posY += spaceLine2;
//
//		formato =  "Kilos Recolectados: " + Util.Redondear(encabezado.PesoBidones,2);
//		strPrint +=  formato + enter;
//		posY += spaceLine2;
//
//		formato =  "Bidones Vacios: " + encabezado.CantidadVacios;
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =  Util.lpad("", 42, "");
//		strPrint +=  formato + enter;
//		posY += spaceLine2;
//
//		formato =  "Observaciones: " + encabezado.Observacion;
//		strPrint +=  formato + enter;
//		posY += spaceLine3;
//
//		formato =  Util.rpad("", 30,"-");
//		strPrint +=  formato + enter;
//		posY += spaceLine1;
//
//		formato =    infoAdicional.campo1;
//		strPrint +=  formato + enter;
//		posY += spaceLine4;
//
//		formato =    infoAdicional.campo2;
//		strPrint +=  formato + enter;
//		posY += spaceLine4;
//
//		formato =    infoAdicional.campo3;
//		strPrint +=  formato + enter;
//		posY += spaceLine4;
//
//		formato =    infoAdicional.campo4;
//		strPrint +=  formato + enter;
//		posY += spaceLine4;
//
//		formato =    infoAdicional.campo5;
//		strPrint +=  formato + enter;
//		posY += spaceLine4;
//
//		formato =    infoAdicional.campo6;
//		strPrint +=  formato + enter;
//		posY += spaceLine4;
//
//		formato =    infoAdicional.campo7;
//		strPrint +=  formato + enter;
//		posY += spaceLine4;
//
//
//
////		formato =  Util.lpad("", 42, "");
////		strPrint +=  formato + enter;
////		posY += spaceLine1;
//
//		if (isCopia) {
//			formato = Util.CentrarLinea("COPIA", 30);
//			strPrint += formato + enter;
//			posY += spaceLine1;
//
//		}else{
//			formato = Util.CentrarLinea("ORIGINAL", 30);
//			strPrint += formato + enter;
//			posY += spaceLine1;
//		}
//
//
//		formato =  Util.lpad("", 42, "");
//		strPrint +=  formato + enter + enter + enter + enter;
//		posY += spaceLine2;
//
//		return strPrint;
//	}



	/**
	 * Metodo encargado de armar el formato de devolucion
	 * 
	 * @return
	 */
	public static String formatoDevolucion(boolean isCheckIn) {
		return "";
	}



	/**
	 * Metodo encargado de calcular el tamano maximo de un vector para cargar
	 * los espacios
	 * 
	 * @param cadenas
	 * @return
	 */
	public static int calcularEspacios(Vector<String> cadenas) {
		int espacio = 0;
		int auxiliar = 0;
		for (String cadena : cadenas) {
			auxiliar = cadena.length();
			if (espacio < auxiliar)
				espacio = auxiliar;
		}
		return espacio;
	}



	/**
	 * Metodo encargado de agregar espacios a las una cadena
	 * 
	 * @param cadena
	 * @param espacio
	 * @return
	 */
	public static String agregarEspacioCadena(String cadena, int espacio) {
		String cadenaEspacio = "";
		if (espacio > cadena.length()) {
			int numeroEspacios = espacio - cadena.length();
			for (int i = 0; i < numeroEspacios; i++) {
				cadenaEspacio += " ";
			}
		}
		return (cadenaEspacio + cadena);
	}



	/**
	 * Metodo encargado de imprimir las tirillas de la impresora Apex
	 * 
	 * @param socket
	 * @param textoImprimir
	 */
	public static void ImprimiendoPrinter(final BluetoothSocket socket, final String textoImprimir) {

		splashTread = new Thread() {
			@Override
			public void run() {

				try {
					synchronized (this) {
						wait(_splashTime);
					}

				} catch (InterruptedException e) {
				} finally {
					OutputStream salida = null;
					try {
						
						
						char ret1 = 13;
						char ret2 = 10;
						String enter = String.valueOf(ret1) + String.valueOf(ret2);
						
						String textoImprimirfinal = textoImprimir + enter+enter+enter;
						
						salida = socket.getOutputStream();
						
						synchronized (this) {

							wait(_splashTime);
						}
						
						salida.write(textoImprimirfinal.getBytes());

						synchronized (this) {

							wait(_splashTime2);
						}

						salida.flush();
						
						

					} catch (Exception e2) {

//						String e = e2.getMessage();
						e2.printStackTrace();

					} finally {

						try {

							if (socket != null) {
								socket.close();
							}
							salida.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		splashTread.start();
	}
}