package co.com.celuweb.carterabaldomero;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.comm.*;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;


import java.sql.Connection;
import java.util.UUID;

import Adapters.ListViewAdapter;
import Adapters.ReporstPrinter;
import businessObject.PrinterBO;
import dataobject.ItemListView;
import dataobject.Lenguaje;
import okhttp3.internal.Util;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.Utilidades;

public class FormConfigurarImpresora extends AppCompatActivity {

    String mensaje;
    ProgressDialog progressDialog;
    private static Activity context;
    private Lenguaje lenguajeElegido;
    ImageView btnAtras;
    ImageView btnPlusMas;
    ImageView btnSearch;
    LinearLayout lybtnAtras;
    TextView Acoplar,Imprimir,Regresar,Definir,textImpresora,impresora,mac;

    /**
     * Referencia para acceder a la confguracion de la impresora sewoo LK-P20
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_configurar_impresora);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Definir = findViewById(R.id.DefinirImp);
        Acoplar = findViewById(R.id.Acoplar);
        Regresar = findViewById(R.id.RegresarImp);
        Imprimir = findViewById(R.id.Imprimir);
        impresora = findViewById(R.id.Impresora);
        textImpresora = findViewById(R.id.textoImpresora);


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(FormConfigurarImpresora.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                mTitle.setText("Printer Setup");
                textImpresora.setText("Printer Setup");
                impresora.setText("Printer");
                Imprimir.setText("Print test");
                Acoplar.setText("Dock device");
                Definir.setText("Define printer");
                Regresar.setText("Back");
            }else if (lenguajeElegido.lenguaje.equals("ESP")) {
                mTitle.setText("Configurar Impresora");

            }
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnAtras = (ImageView) toolbar.findViewById(R.id.btnAtras);
        btnPlusMas = (ImageView) toolbar.findViewById(R.id.btnPlusMas);
        btnSearch = (ImageView) toolbar.findViewById(R.id.btnSearch);
        lybtnAtras = (LinearLayout) toolbar.findViewById(R.id.lybtnAtras);
        btnAtras.setOnClickListener(botonAtrasListener);
        btnPlusMas.setOnClickListener(botonPlusAddListener);
        btnSearch.setOnClickListener(botonesSearchListener);
        lybtnAtras.setOnClickListener(lybotonAtrasListener);
        btnPlusMas.setVisibility(ImageView.GONE);
        btnSearch.setVisibility(ImageView.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        settingsPrinter();

    }

    public void cargarOpciones() {

        ItemListView[] items = new ItemListView[4];

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

        items[3] = new ItemListView();
        items[3].titulo = "Regresar";
        items[3].subTitulo = "";
        items[3].icono = R.drawable.imprimir_prueba;

        ListViewAdapter adapter = new ListViewAdapter(this, items, R.drawable.op_printer, 0x1B3C6D,
                R.layout.list_item_style);
        ListView listaOpciones = (ListView) findViewById(R.id.listaOpciones);
        listaOpciones.setAdapter(adapter);
    }

    public void settingsPrinter() {

        TextView lblNombreImpresora = (TextView) findViewById(R.id.lblNombreImpresora);
        TextView lblMacImpresora = (TextView) findViewById(R.id.lblMacImpresora);

        SharedPreferences settings = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
        String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");
        String labelImpresora = settings.getString(Constantes.LABEL_IMPRESORA, "-");

        lblNombreImpresora.setText(labelImpresora);
        lblMacImpresora.setText(macImpresora);
    }


    public void confirmarColocarVisibleBluetooth() {

        Intent i = new Intent();
        i.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, Constantes.REQUEST_DISCOVERABLE_CODE);
    }

  public void setListenerListView() {

        ListView listaOpciones = (ListView) findViewById(R.id.listaOpciones);
        listaOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {

                    confirmarColocarVisibleBluetooth();

                } else if (position == 1) {

                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    intent.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    intent.putExtra("isFullScreen", true);
                    startActivity(intent);

                } else if (position == 2) {

                    SharedPreferences settings = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                    String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");

                    if (macImpresora.equals("-")) {


                        Alert.nutresaShow(FormConfigurarImpresora.this, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

                                        Alert.dialogo.cancel();
                                    }

                                }, null);

                    } else {

                        progressDialog = ProgressDialog.show(FormConfigurarImpresora.this, "",
                                "Por Favor Espere...\n\nProcesando Informacion!", true);
                        progressDialog.show();

                        SharedPreferences set = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                        String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

                        if (!tipoImpresora.equals("Intermec")) {
                            ImprimirPrueba(macImpresora);

                        } else {
                            ImprimirPrueba(macImpresora);
                        }
                    }
                } else if (position == 3) {
                    finish();
                }
            }
        });
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
                    ZebraPrinter zebraPrinter = ZebraPrinterFactory.getInstance(printerConnection);
                    PrinterLanguage printerLanguage = zebraPrinter.getPrinterControlLanguage();
                    Log.w("FormEstadisticaPedidos", "ImprimirTirilla -> Lenguaje de la Impresora " + printerLanguage);
                    // Envia los datos a la Impresora como un Arreglo de bytes.
                    String cpclData = PrinterBO.formatoPrueba();
                    printerConnection.write(cpclData.getBytes());

                    // Se asegura que los datos son enviados a la Impresora antes de cerrar la
                    // conexion.
                    Thread.sleep(500);

                    Looper.myLooper().quit();
                    handlerFinish.sendEmptyMessage(0);

                } catch (Exception e) {

                    Log.e("FormConfigPrinter", "ImprimirTirilla -> " + e.getMessage(), e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(FormConfigurarImpresora.this,"No se pudo Imprimir: " + e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

                    Looper.myLooper().quit();
                    handlerFinish.sendEmptyMessage(0);

                } finally {

                    // Cierra la conexion para liberar Recursos
                    Looper.myLooper().quit();
                    handlerFinish.sendEmptyMessage(0);

                    try {

                        if (printerConnection != null)
                            printerConnection.close();
                        Looper.myLooper().quit();
                        handlerFinish.sendEmptyMessage(0);
                        Toast.makeText(FormConfigurarImpresora.this,"Proceso Finalizado",Toast.LENGTH_SHORT).show();

                    } catch (ZebraPrinterConnectionException e) {

                        Log.e("FormConfigPrinter", "ImprimirTirilla", e);
                        handlerFinish.sendEmptyMessage(0);
                        Toast.makeText(FormConfigurarImpresora.this,"Error al Imprimir: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }).start();
    }

    private void imprimirTirillaGeneral(final String macAddress) {



        new Thread(new Runnable() {

            public void run() {



                mensaje = "";
                BluetoothSocket socket = null;

                try {

                    Looper.prepare();

                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    if (bluetoothAdapter == null) {

                        mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";

                    } else if (!bluetoothAdapter.isEnabled()) {

                        mensaje = "No hubo conexion con la impresora.\n\nPor Favor intente de nuevo.";

                    } else {

                        BluetoothDevice printer = null;

                        printer = bluetoothAdapter.getRemoteDevice(macAddress);

                        if (printer == null) {

                            mensaje = "No se pudo establecer la conexion con la Impresora.";

                        } else {

                            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                            SharedPreferences settings = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                            String tipoImpresora = settings.getString(Constantes.TIPO_IMPRESORA, "otro");

                            if (tipoImpresora.equals("Intermec")) {

                                socket = printer.createInsecureRfcommSocketToServiceRecord(uuid);
                            } else {
                                socket = printer.createRfcommSocketToServiceRecord(uuid);
                            }


                            if (socket != null) {

                                if (!tipoImpresora.equals("Intermec")) {

                                    socket.connect();
                                    Thread.sleep(3500);

                                    ReporstPrinter.ImprimiendoPrinter(socket, PrinterBO.formatoPrueba());


                                    handlerFinish.sendEmptyMessage(0);

                                } else {

                                    socket.connect();
                                    Thread.sleep(3500);

                                    ReporstPrinter.ImprimiendoPrinter(socket, PrinterBO.formatoPrueba());

                                    handlerFinish.sendEmptyMessage(0);
                                }

                            } else {

                                mensaje = "No se pudo abrir la conexion con la Impresora.\n\nPor Favor intente de nuevo.";
                            }

                            // } else {

                            // mensaje = "La Impresora: "
                            // + macAddress
                            // + " Actualmente no esta Acoplada con el
                            // Dispositivo Movil.\n\nPor Favor configure primero
                            // la impresora.";
                            // }
                        }
                    }

                    if (!mensaje.equals("")) {

                        context = FormConfigurarImpresora.this;
                        handlerFinish.sendEmptyMessage(0);
                    }

                    Looper.myLooper().quit();

                } catch (Exception e) {

                    String motivo = e.getMessage();

                    mensaje = "No se pudo ejecutar la Impresion.";

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(FormConfigurarImpresora.this,mensaje + e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

                    Looper.myLooper().quit();

                    if (motivo != null) {
                        mensaje += "\n\n" + motivo;
                    }

                    context = FormConfigurarImpresora.this;
                    handlerFinish.sendEmptyMessage(0);

                } finally {

                    try {

                        /*
                         * Thread.sleep(3500);
                         *
                         * if (socket != null) { socket.close(); }
                         */

                        Looper.myLooper().quit();
                        handlerFinish.sendEmptyMessage(0);

                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(FormConfigurarImpresora.this,"Error al imprimir: " + e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });
                        Looper.myLooper().quit();
                        handlerFinish.sendEmptyMessage(0);

                    }
                }
            }

        }).start();

    }

    private Handler handlerFinish = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (progressDialog != null)
                progressDialog.cancel();
        }
    };
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constantes.REQUEST_DISCOVERABLE_CODE) {

            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(FormConfigurarImpresora.this);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


            if (lenguajeElegido == null) {

            }else if (lenguajeElegido != null) {

                if (lenguajeElegido.lenguaje.equals("USA")) {

                    progressDialog = ProgressDialog.show(FormConfigurarImpresora.this, "", "Activating Bluetooth...", true);
                    progressDialog.show();

                    new Thread() {

                        public void run() {

                            try {

                                Thread.sleep(4000);
                            } catch (Exception e) {

                            }

                            FormConfigurarImpresora.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub

                                    if (progressDialog != null)
                                        progressDialog.dismiss();

                                    Intent intent = new Intent(FormConfigurarImpresora.this, FormPrinters.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    }.start();

                }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    progressDialog = ProgressDialog.show(FormConfigurarImpresora.this, "", "Activando Bluetooth...", true);
                    progressDialog.show();

                    new Thread() {

                        public void run() {

                            try {

                                Thread.sleep(4000);
                            } catch (Exception e) {

                            }

                            FormConfigurarImpresora.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub

                                    if (progressDialog != null)
                                        progressDialog.dismiss();

                                    Intent intent = new Intent(FormConfigurarImpresora.this, FormPrinters.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    }.start();

                }
            }

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {

            //	Util.turnOffBluetooth();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void OnClickDefinirImpresora(View view) {

        confirmarColocarVisibleBluetooth();

    }

    public void OnClickAcoplarDispositivo(View view) {

        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        intent.putExtra("isFullScreen", true);
        startActivity(intent);

    }

    public void OnClickImprimirPrueba(View view) {

        SharedPreferences settings = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
        String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");



        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(FormConfigurarImpresora.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                if (macImpresora.equals("-")) {

                    Alert.nutresaShow(FormConfigurarImpresora.this, "Information", "No printer set. Please first configure the printer!", "OK", null,

                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

                } else {

                    progressDialog = ProgressDialog.show(FormConfigurarImpresora.this, "",
                            "Please wait..\n\nProcessing information!", true);
                    progressDialog.show();

                    SharedPreferences set = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                    String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

                    if (!tipoImpresora.equals("Intermec")) {
                        ImprimirPrueba(macImpresora);
				/*sewooLKP20 = new SewooLKP20(FormConfigPrinterNuevo.this);
				imprimirSewooLKP20(macImpresora);*/
                    } else {
                        ImprimirPrueba(macImpresora);
                    }
                }
            }else if (lenguajeElegido.lenguaje.equals("ESP")) {
                if (macImpresora.equals("-")) {

                    Alert.nutresaShow(FormConfigurarImpresora.this, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

                } else {

                    progressDialog = ProgressDialog.show(FormConfigurarImpresora.this, "",
                            "Por Favor Espere...\n\nProcesando Informacion!", true);
                    progressDialog.show();

                    SharedPreferences set = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                    String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

                    if (!tipoImpresora.equals("Intermec")) {
                        ImprimirPrueba(macImpresora);
				/*sewooLKP20 = new SewooLKP20(FormConfigPrinterNuevo.this);
				imprimirSewooLKP20(macImpresora);*/
                    } else {
                        ImprimirPrueba(macImpresora);
                    }
                }
            }
        }





    }

    public void onClickRegresarImpresora(View view) {

        finish();

    }


    View.OnClickListener botonAtrasListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            finish();

        }
    };


    View.OnClickListener botonPlusAddListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            Alert.nutresaShow(FormConfigurarImpresora.this, "Informacion", "Evento De Boton Mas", "Aceptar", null,

                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }

                    }, null);

        }
    };


    View.OnClickListener botonesSearchListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub


            Alert.nutresaShow(FormConfigurarImpresora.this, "Informacion", "Evento De Boton Search", "Aceptar", null,

                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }

                    }, null);

        }
    };


    View.OnClickListener lybotonAtrasListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            finish();

        }
    };
}