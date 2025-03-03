package co.com.celuweb.carterabaldomero;

import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import Adapters.ListViewAdapter;
import businessObject.PrinterBO;
import dataobject.ItemListView;
import okhttp3.internal.Util;
import utilidades.Alert;

public class FormConfigPrinter extends Activity {

	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA = "MAC";
	public final static String LABEL_IMPRESORA = "LABEL";

	public static final String TIPO_IMPRESORA = "TIPO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		/* Se implementa log de errores */
		setContentView(R.layout.form_config_printer);

		CargarOpciones();
		SetListenerListView();
	}

	@Override
	protected void onResume() {

		super.onResume();
		SettingsPrinter();
	}

	public void SettingsPrinter() {

		TextView lblNombreImpresora = (TextView) findViewById(R.id.lblNombreImpresora);
		TextView lblMacImpresora = (TextView) findViewById(R.id.lblMacImpresora);

		SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
		String macImpresora = settings.getString(MAC_IMPRESORA, "-");
		String labelImpresora = settings.getString(LABEL_IMPRESORA, "-");

		lblNombreImpresora.setText(labelImpresora);
		lblMacImpresora.setText(macImpresora);
	}

	public void CargarOpciones() {

		ItemListView[] items = new ItemListView[3];

		items[0] = new ItemListView();
		items[0].titulo = "Definir Impresora";
		items[0].subTitulo = "Establece la Impresora Predeterminada";
		items[0].icono = R.drawable.definir_impresora;

		items[1] = new ItemListView();
		items[1].titulo = "Acoplar Dispositivo";
		items[1].subTitulo = "Enlaza la Impresora al Dispositivo";
		items[1].icono = R.drawable.acoplar_dispositivo;

		items[2] = new ItemListView();
		items[2].titulo = "Imprimir Prueba";
		items[2].subTitulo = "Imprime Tirilla de Prueba!";
		items[2].icono = R.drawable.imprimir_prueba;

		ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.op_printer, 0x2E65AD);
		ListView listaOpciones = (ListView) findViewById(R.id.listaOpciones);
		listaOpciones.setAdapter(adapter);
	}

	private void ImprimirPrueba(final String macAddress) {

		new Thread(new Runnable() {

			public void run() {

				ZebraPrinterConnection printerConnection = null;

				try {

					printerConnection = new BluetoothPrinterConnection(macAddress);

					Looper.prepare();

					// Abre la Conexion, la conexion Fisica es establecida aqui.
					printerConnection.open();

					// Envia los datos a la Impresora como un Arreglo de bytes.
					String cpclData = PrinterBO.formatoPrueba();
					printerConnection.write(cpclData.getBytes());

					// Se asegura que los datos son enviados a la Impresora antes de cerrar la
					// conexion.
					Thread.sleep(500);

					Looper.myLooper().quit();

				} catch (Exception e) {

					Log.e("FormConfigPrinter", "ImprimirTirilla -> " + e.getMessage(), e);
					Toast.makeText(FormConfigPrinter.this, "No se pudo Imprimir. Motivo: " + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					Looper.myLooper().quit();

				} finally {

					// Cierra la conexion para liberar Recursos

					try {

						if (printerConnection != null)
							printerConnection.close();
						Looper.myLooper().quit();

					} catch (ZebraPrinterConnectionException e) {

						Log.e("FormConfigPrinter", "ImprimirTirilla", e);
					}
				}
			}

		}).start();
	}

	public void SetListenerListView() {

		ListView listaOpciones = (ListView) findViewById(R.id.listaOpciones);
		listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == 0) {

					Intent intent = new Intent(FormConfigPrinter.this, FormPrinters.class);
					startActivity(intent);

				} else if (position == 1) {

					Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
					startActivity(intent);

				} else if (position == 2) {

					SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
					String macImpresora = settings.getString(MAC_IMPRESORA, "-");

					if (macImpresora.equals("-")) {

						Alert.nutresaShow(FormConfigPrinter.this, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

								new View.OnClickListener() {

									@Override
									public void onClick(View view) {

										Alert.dialogo.cancel();
									}

								}, null);

					} else {

						ImprimirPrueba(macImpresora);
					}
				}
			}
		});
	}
}
