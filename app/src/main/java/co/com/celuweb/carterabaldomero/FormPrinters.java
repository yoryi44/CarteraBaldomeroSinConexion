package co.com.celuweb.carterabaldomero;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.discovery.BluetoothDiscoverer;
import com.zebra.android.discovery.DiscoveredPrinter;
import com.zebra.android.discovery.DiscoveredPrinterBluetooth;
import com.zebra.android.discovery.DiscoveryHandler;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import Adapters.ListViewPrinterAdapter;
import dataobject.ItemListView;
import dataobject.Lenguaje;
import sharedpreferences.PreferencesLenguaje;

public class FormPrinters extends ListActivity {
    
	String mensaje = "";
	private ListViewPrinterAdapter adapter;
	
    private ArrayList<ItemListView> listDevices = null;
    private static DiscoveryHandler discoveryHandler = null;


	public final static String CONFIG_IMPRESORA = "PRINTER";
	public final static String MAC_IMPRESORA    = "MAC";
	public final static String LABEL_IMPRESORA  = "LABEL";

	public static final String TIPO_IMPRESORA = "TIPO";

	private Lenguaje lenguajeElegido;
	boolean cancelar = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); /*Se implementa log de errores */
		Gson gson2 = new Gson();
		String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(FormPrinters.this);
		lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);



        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.form_printers);
        
      IniciarHandler();
    }
    
 public void IniciarHandler() {
    	
    	discoveryHandler = new DiscoveryHandler() {
    		
    		public void discoveryError(String message) {
    			
    			//new UIHelper(FormPrinters.this).showErrorDialogOnGuiThread(message);
    		}
    		
    		public void discoveryFinished() {
    			
    			runOnUiThread(new Runnable() {
    				
    				public void run() {
    					
    					if (!cancelar) {

							if (lenguajeElegido == null) {

							}else if (lenguajeElegido != null) {
								if (lenguajeElegido.lenguaje.equals("USA")) {

									Toast.makeText(FormPrinters.this, "Total Devices Found: " + listDevices.size(), Toast.LENGTH_SHORT).show();
									setProgressBarIndeterminateVisibility(false);

								}else if (lenguajeElegido.lenguaje.equals("ESP")) {

									Toast.makeText(FormPrinters.this, "Total Dispositivos Encontrados: " + listDevices.size(), Toast.LENGTH_SHORT).show();
									setProgressBarIndeterminateVisibility(false);

								}
							}


    					}
    				}
    			});
    		}
    		
    		public void foundPrinter(final DiscoveredPrinter printer) {
    			
    			runOnUiThread(new Runnable() {
    				
    				public void run() {
    					
    					DiscoveredPrinterBluetooth p = (DiscoveredPrinterBluetooth) printer;
    					
    					ItemListView item = new ItemListView();
    					item.titulo = p.address;
    					item.subTitulo = p.friendlyName != null ? p.friendlyName : "";
    					
    					listDevices.add(item);
    					adapter.notifyDataSetChanged();
    				}
    			});
    		}
    	};
    	
    	setProgressBarIndeterminateVisibility(true);
    	listDevices = new ArrayList<ItemListView>();
    	SetListAdapter();
    	
    	new Thread(new Runnable() {
    		
    		public void run() {
    			
    			Looper.prepare();
    			
    			try {
    				
    				BluetoothDiscoverer.findPrinters(FormPrinters.this, discoveryHandler);
    				
    			} catch (ZebraPrinterConnectionException e) {
    				
    			//	new UIHelper(FormPrinters.this).showErrorDialogOnGuiThread(e.getMessage());
    				
    			} catch (InterruptedException e) {
    				
    			//	new UIHelper(FormPrinters.this).showErrorDialogOnGuiThread(e.getMessage());
    				
    			} finally {
    				
    				Looper.myLooper().quit();
    			}
    		}
    		
    	}).start();
    }
    
    public void CancelDiscovery() {
    	
    	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	if (bluetoothAdapter.isDiscovering())
    		bluetoothAdapter.cancelDiscovery();
    }

    private void SetListAdapter() {
    	
    	adapter = new ListViewPrinterAdapter(this, listDevices, R.drawable.op_bluetooth, 0x2E65AD);
    	setListAdapter(adapter);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	
	    	cancelar = true;
	    	CancelDiscovery();
	    	setProgressBarIndeterminateVisibility(false);	    	
	    	
	    	finish();
	    	return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
    
    protected void onListItemClick(ListView listView, View view, int position, long id) {
    	
        super.onListItemClick(listView, view, position, id);
        
        AlertDialog alertDialog;
        final ItemListView item = listDevices.get(position);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				
				ValidarImpresora(item);
				dialog.cancel();
			}
			
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				
				dialog.cancel();
			}
			
		});

		alertDialog = builder.create();

		if (lenguajeElegido == null) {

		}else if (lenguajeElegido != null) {
			if (lenguajeElegido.lenguaje.equals("USA")) {
				alertDialog.setMessage("Are you sure to set the device as Printer: " + (item.subTitulo.equals("") ? item.titulo : item.subTitulo  ) );
				alertDialog.show();
			}else if (lenguajeElegido.lenguaje.equals("ESP")) {
				alertDialog.setMessage("Esta seguro de establecer como Impresora el Dispositivo: " + (item.subTitulo.equals("") ? item.titulo : item.subTitulo  ) );
				alertDialog.show();

			}
		}

    }
    
    private void ValidarImpresora(final ItemListView item) {
    	
    	new Thread(new Runnable() {
    		
    		public void run() {
    			
    			ZebraPrinterConnection printerConnection = null;
    			
    			try {
    				
    				printerConnection = new BluetoothPrinterConnection(item.titulo);
    				
    				Looper.prepare();
    				
    				//Abre la Conexion, la conexion Fisica es establecida aqui.
    				printerConnection.open();
    				
    				ZebraPrinter zebraPrinter = ZebraPrinterFactory.getInstance(printerConnection);
    				PrinterLanguage printerLanguage = zebraPrinter.getPrinterControlLanguage();
    				Log.w("FormPrinters", "ValidarImpresora -> Lenguaje de la Impresora " + printerLanguage);
    				
    				SharedPreferences settings = getSharedPreferences(CONFIG_IMPRESORA, MODE_PRIVATE);
    		        SharedPreferences.Editor editor = settings.edit();
    		        
    		        editor.putString(MAC_IMPRESORA, item.titulo);
    		        editor.putString(LABEL_IMPRESORA, item.subTitulo);
    		        editor.commit();
    				
    		        handlerConfigOK.sendEmptyMessage(0);
    		        
    				Looper.myLooper().quit();
    				
    			} catch (Exception e) {
    				
    				Log.e("FormPrinters", "ValidarImpresora -> " + e.getMessage(), e);
					if (lenguajeElegido == null) {

					}else if (lenguajeElegido != null) {
						if (lenguajeElegido.lenguaje.equals("USA")) {
							mensaje = "Failed to Set Selected Device.\n\n" + e.getMessage();

						}else if (lenguajeElegido.lenguaje.equals("ESP")) {

							mensaje = "No se pudo Establecer el Dispositivo Seleccionado.\n\n" + e.getMessage();

						}
					}
    				
    				handlerError.sendEmptyMessage(0);
    				
    			} finally {
    				
    				// Cierra la conexion para liberar Recursos
    				
    				try {
    					
    					if (printerConnection != null)
    						printerConnection.close();
    					
    				} catch (ZebraPrinterConnectionException e) {
    					
    					Log.e("FormPrinters", "ValidarImpresora", e);
    				}
    			}
    		}
    		
    	 }).start();
    }
    
    private Handler handlerError = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(FormPrinters.this);
			builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
					dialog.cancel();
				}
			});

			AlertDialog alertDialog = builder.create();
	    	alertDialog.setMessage(mensaje);
	    	alertDialog.show();
		}
    };
    
    private Handler handlerConfigOK = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(FormPrinters.this);
			builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
					dialog.cancel();
					finish();
				}
			});

			AlertDialog alertDialog = builder.create();
			if (lenguajeElegido == null) {

			}else if (lenguajeElegido != null) {
				if (lenguajeElegido.lenguaje.equals("USA")) {
					alertDialog.setMessage("Printer Established with success");
					alertDialog.show();
				}else if (lenguajeElegido.lenguaje.equals("ESP")) {
					alertDialog.setMessage("Impresora Establecida con Exito");
					alertDialog.show();

				}
			}

		}
    };   
}
