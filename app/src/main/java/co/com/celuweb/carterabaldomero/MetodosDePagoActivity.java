package co.com.celuweb.carterabaldomero;

import static co.com.celuweb.carterabaldomero.LoginActivity.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import org.json.JSONException;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.AdapterFacturas;
import Adapters.ReporstPrinter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import businessObject.PrinterBO;
import configuracion.Synchronizer;
import dataobject.Anticipo;
import dataobject.Cartera;
import dataobject.ClienteSincronizado;
import dataobject.Facturas;
import dataobject.FormaPago;
import dataobject.Lenguaje;
import dataobject.ReciboDinero;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesCarteraFactura;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesFotos;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesParcial;
import sharedpreferences.PreferencesReciboDinero;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import metodosPago.AlertPagos;
import metodosPago.MetodoDePagoBitcoin;
import metodosPago.MetodoDePagoCheque;
import metodosPago.MetodoDePagoEfectivo;
import metodosPago.MetodoDePagoTarjeta;
import metodosPago.MetodoDePagoTransferencia;
import utilidades.Constantes;
import utilidades.ProgressView;
import utilidades.Utilidades;


public class MetodosDePagoActivity extends AppCompatActivity implements AdapterFacturas.facturaCartera, Synchronizer, Runnable, DialogFirmaFragment.OnSignatureDialogListener,DialogResumenFirmaFragment.OnSuccesSignatureDialogListener {

    private TextView tvMontoFactura, tvDiferenciaMetodosPago, tvTotalFormasPago;
    private List<Facturas> listaFacturas;
    private List<Facturas> listaFacturasPend;
    private List<Facturas> listaFacturasJuntas;
    private List<Facturas> listaFacturas2;
    private List<Facturas> listaFacturas3;
    private List<Facturas> listaFacturas4;
    private FormaPago formaPago;
    private Usuario usuarioApp;
    private Anticipo anticipo;
    private ClienteSincronizado clienteSel;
    private boolean envioInformacion = false;
    ProgressDialog progressDialog;
    BluetoothAdapter bluetooth;
    boolean primerEjecucion = true;
    private String macImpresora;
    String mensaje;
    private static Activity context;
    private boolean guardarTotalFinal = true;
    String documentoFinanciero = "";
    double precioTotal = 0;
    double DiferenciaFormasPago;
    double TotalFormasPago = 0;
    String nroRecibo = "";
    double DiferenciaFormasPagoE = 0;
    double TotalFormasPagoE = 0;
    double DiferenciaFormasPagoPEN = 0;
    double TotalFormasPagoPEn = 0;
    double DiferenciaFormasPagoPEND;
    String respuesta = "";
    boolean estadoEnviadoRespuesta = false;
    String str = "";
    String acert = "";
    ProgressDialog progressDoalog;
    private Lenguaje lenguajeElegido;
    Boolean esPreventa;
    private int METODO_PAGO = 0;

    private FusedLocationProviderClient fusedLocationClient;

    double TotalFormasPagoPEND = 0;
    final List<String> documentosFinanciero = new ArrayList<>();
    final Vector<String> listaItems = new Vector<>();

    DialogResumenFirmaFragment dialogResumenFirmaActivity = null;
    final Collection<Cartera> facCollection = new Collection<Cartera>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Cartera> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return null;
        }

        @Override
        public boolean add(Cartera cartera) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Cartera> collection) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        @Override
        public void clear() {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.metodos_pago_activity);

        listaFacturas = new ArrayList<>();
        listaFacturasJuntas = new ArrayList<>();
        listaFacturas3 = new ArrayList<>();
        context = MetodosDePagoActivity.this;

        tvMontoFactura = findViewById(R.id.tvMontoCarteraFP);
        tvTotalFormasPago = findViewById(R.id.tvTotalFormasPago);
        tvDiferenciaMetodosPago = findViewById(R.id.tvDiferenciaMetodosPago);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final RadioButton rbEfectivo = findViewById(R.id.rbEfectivo);
        final RadioButton rbCheque = findViewById(R.id.rbCheque);
        final RadioButton rbTrasnferencia = findViewById(R.id.rbTrasnferencia);
        final RadioButton rbTarjetaCredito = findViewById(R.id.rbTarjetaCredito);
        final RadioButton rbBitcoin = findViewById(R.id.rbBitcoin);

        TextView tituloMetodosPagoGeneral = findViewById(R.id.tituloMetodosPagoGeneral);
        TextView tituloTotalRecaudoGeneral = findViewById(R.id.tituloTotalRecaudoGeneral);
        TextView tituloTotalFormasPagoGeneral = findViewById(R.id.tituloTotalFormasPagoGeneral);
        TextView tituloDiferenciaGeneral = findViewById(R.id.tituloDiferenciaGeneral);
        TextView tituloFormasPagoRealizadasGeneral = findViewById(R.id.tituloFormasPagoRealizadasGeneral);

        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);


        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesUsuario.obtenerUsuario(this);
        usuarioApp = gson2.fromJson(stringJsonObject2, Usuario.class);

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(this);
        lenguajeElegido = gson2.fromJson(stringJsonObject223, Lenguaje.class);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(context);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        Gson gson22 = new Gson();
        String stringJsonObject22 = PreferencesFormaPago.obteneFacturaSeleccionada(context);
        formaPago = gson22.fromJson(stringJsonObject22, FormaPago.class);

        Gson gson12 = new Gson();
        String stringJsonObject12 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(context);
        clienteSel = gson12.fromJson(stringJsonObject12, ClienteSincronizado.class);


        if (formaPago == null) {
            guardarTotalFinal = false;

        } else if (formaPago != null) {
            guardarTotalFinal = true;

        }


        nroRecibo = clienteSel.consecutivo;

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, context);

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                rbEfectivo.setText("Cash");
                rbCheque.setText("Check");
                rbTrasnferencia.setText("Transfer");
                rbTarjetaCredito.setText("Credit Card");

                tituloMetodosPagoGeneral.setText("Payment Method");
                tituloTotalRecaudoGeneral.setText("Total Amount");
                tituloTotalFormasPagoGeneral.setText("Total Forms of Payment");
                tituloDiferenciaGeneral.setText("Difference");
                tituloFormasPagoRealizadasGeneral.setText("Payments");
                button.setText("Send Receipt");
                button2.setText("Print");


            } else if (lenguajeElegido.lenguaje.equals("ESP")) {


            }
        }


        metodosPago();
        metodosSeleccionados();
        formasPago();
        formasPagoAnticipRecibosLe();
        configurarVista();

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        esPreventa = settings.getBoolean("esPreventa", false);
        System.out.println("estado a eliminar.................... ----> " + settings.getAll());

        DataBaseBO.eliminarFotosSinDocumentosAsociados(context);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {


            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {

            case R.id.menu_forward:

                //MOSTRAR PROGRES DIALOG
                progressDialog = new ProgressDialog(this);
                if (lenguajeElegido == null) {
                    progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {
                        progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                        progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                    }
                }
                progressDialog.setCancelable(false);
                progressDialog.show();

                ExecutorService executor = Executors.newFixedThreadPool(2);

                //VALIDAR EL PROCESO A REALIZAR
                if (guardarTotalFinal == true) {

                    item.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            item.setEnabled(true);
                        }
                    }, 600);
                    //EJECUTAR LA TAREA EN UN HILO
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            guardarTotalFinal();
                        }
                    });

                    executor.shutdown();


                } else if (guardarTotalFinal == false) {


                    item.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            item.setEnabled(true);
                        }
                    }, 600);
                    //EJECUTAR LA TAREA EN UN HILO
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            guardarTotalFinalAnticipo();
                        }
                    });

                    executor.shutdown();

                } else {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fw, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Alert.vistaDialogoCerrarSesion(context, "¿If you return to cancel the collection, are you sure you want to cancel the collection?", "Cancel Collection", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);
                        final List<String> documentosFinanciero = new ArrayList<>();

                        Gson gson3 = new Gson();
                        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(context);
                        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

                        Gson gson1 = new Gson();
                        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(context);
                        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

                        String documentoFinanciero = "";

                        if (facCollection != null) {

                            documentoFinanciero = clienteSel.consecutivo;
                            documentosFinanciero.add(documentoFinanciero);


                        }

                        if (anticipo != null) {

                            String nroRecibo = clienteSel.consecutivo;
                            documentosFinanciero.add(nroRecibo);

                        }

                        if (facCollection == null) {

                            if (anticipo != null) {

                                if (anticipo.estado == true) {

                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);

                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_ReciboDinero", true);
                                    editor1.remove("estado_ReciboDinero");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");
                                    editor1.commit();

                                    finish();
                                    Alert.dialogo.cancel();

                                }

                                if (anticipo.estado == false) {


                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);

                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_ReciboDinero", true);
                                    editor1.remove("estado_ReciboDinero");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");
                                    editor1.commit();

                                    finish();
                                    Alert.dialogo.cancel();

                                }
                            }

                        }

                        if (facCollection != null) {


                            if (formaPago.parcial == true) {


                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);


                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                SharedPreferences.Editor editor1 = settings.edit();

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");
                                editor1.commit();
                                finish();

                                Alert.dialogo.cancel();

                            }

                            if (formaPago.parcial == false) {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);


                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = settings.edit();

                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");

                                editor1.commit();

                                finish();
                                Alert.dialogo.cancel();

                            }
                        }

                        finish();
                        Alert.dialogo.cancel();


                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alert.dialogo.cancel();

                    }
                });


            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Alert.vistaDialogoCerrarSesion(context, "¿Si regresa cancelara el recaudo,esta seguro que desea cancelar el recaudo?", "Cancelar Recaudo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);
                        final List<String> documentosFinanciero = new ArrayList<>();

                        Gson gson3 = new Gson();
                        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(context);
                        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

                        Gson gson1 = new Gson();
                        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(context);
                        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

                        String documentoFinanciero = "";

                        if (facCollection != null) {

                            documentoFinanciero = clienteSel.consecutivo;
                            documentosFinanciero.add(documentoFinanciero);


                        }

                        if (anticipo != null) {

                            String nroRecibo = clienteSel.consecutivo;
                            documentosFinanciero.add(nroRecibo);

                        }

                        if (facCollection == null) {

                            if (anticipo != null) {

                                if (anticipo.estado == true) {

                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);

                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                                    PreferencesParcial.vaciarPreferencesParcialSeleccionada(getApplicationContext());

                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");

                                    editor1.commit();
                                    Intent vistaClienteActivity = new Intent(getApplicationContext(), ReciboDineroActivity.class);
                                    startActivity(vistaClienteActivity);
                                    finish();
                                    Alert.dialogo.cancel();

                                }

                                if (anticipo.estado == false) {

                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);

                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                                    PreferencesParcial.vaciarPreferencesParcialSeleccionada(getApplicationContext());

                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");

                                    editor1.commit();
                                    Intent vistaClienteActivity = new Intent(getApplicationContext(), ReciboDineroActivity.class);
                                    startActivity(vistaClienteActivity);
                                    finish();
                                    Alert.dialogo.cancel();

                                }
                            }

                        }

                        if (facCollection != null) {


                            if (formaPago.parcial == true) {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);

                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                SharedPreferences.Editor editor1 = settings.edit();

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");

                                editor1.commit();
                                finish();
                                Alert.dialogo.cancel();

                            }

                            if (formaPago.parcial == false) {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = settings.edit();

                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");

                                editor1.commit();

                                finish();
                                Alert.dialogo.cancel();

                            }
                        }


                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alert.dialogo.cancel();

                    }
                });


            }
        }


    }

    private void configurarVista() {

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);
        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(context);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        Gson gson34 = new Gson();
        String stringJsonObject34 = PreferencesReciboDinero.obteneAnticipoSeleccionada(context);
        ReciboDinero reciboDinero = gson34.fromJson(stringJsonObject34, ReciboDinero.class);


        String codigoU = "";
        if (facCollection != null) {

            for (Cartera cartera1 : facCollection) {
                codigoU = cartera1.getCodigo();
            }

        }


        String codigo = codigoU;
        tvMontoFactura = findViewById(R.id.tvMontoCarteraFP);
        tvTotalFormasPago = findViewById(R.id.tvTotalFormasPago);
        tvDiferenciaMetodosPago = findViewById(R.id.tvDiferenciaMetodosPago);

        double precioTotal = 0;

        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                precioTotal += cartera1.getSaldo();
            }
        }

        if (anticipo != null) {

            if (reciboDinero == null) {


                if (anticipo.estado == true) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            AlertPagos.vistaDialMontoAnticipo(context, "¿Are you sure you want to cancel the collection?", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertPagos.dialogo.cancel();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertPagos.dialogo.cancel();

                                }
                            });


                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Methods of Payment (ADVANCE)"));


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            AlertPagos.vistaDialMontoAnticipo(context, "¿Esta seguro que desea cancelar el recaudo?", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertPagos.dialogo.cancel();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertPagos.dialogo.cancel();

                                }
                            });


                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Metodos de Pago (ANTICIPO)"));

                        }
                    }


                    tvMontoFactura = findViewById(R.id.tvMontoCarteraFP);
                    tvTotalFormasPago = findViewById(R.id.tvTotalFormasPago);
                    tvDiferenciaMetodosPago = findViewById(R.id.tvDiferenciaMetodosPago);

                    if (anticipo != null) {
                        precioTotal = anticipo.getValor();

                    }

                }


                if (anticipo.estado == false) {


                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            AlertPagos.vistaDialRecibolegalizar(context, "¿Are you sure you want to cancel the collection?", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertPagos.dialogo.cancel();

                                }
                            });

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Payment Method (LEGALIZE RECEIPT)"));


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            AlertPagos.vistaDialRecibolegalizar(context, "¿Esta seguro que desea cancelar el recaudo?", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertPagos.dialogo.cancel();

                                }
                            });

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Metodos de Pago (RECIBO LEGALIZAR)"));

                        }
                    }


                    tvMontoFactura = findViewById(R.id.tvMontoCarteraFP);
                    tvTotalFormasPago = findViewById(R.id.tvTotalFormasPago);
                    tvDiferenciaMetodosPago = findViewById(R.id.tvDiferenciaMetodosPago);

                    if (anticipo != null) {
                        precioTotal = anticipo.getValor();

                    }
                }

            }
        }

        String empresas = DataBaseBO.cargarEmpresa(context);

        if (!empresas.equals("AGUC")) {
            ((TextView) findViewById(R.id.tvSimboloDolarDiferenciaG)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvSimboloDolarTotalFormasPagoG)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvSimboloDolarTotalRecudo)).setVisibility(View.GONE);
        }

        //////metodos de pago


        //// formas de pago anticipo recibos le


    }

    private void metodosSeleccionados() {

        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa(context);

        String busquedaViaE = "A";
        String busquedaViaC = "B";
        String busquedaViaT = "O";
        String busquedaViaF = "6";
        String busquedaViaB = "4";
        String viaE = DataBaseBO.cargarViaFormasdePagoEmpresa(busquedaViaE, context);
        String viaC = DataBaseBO.cargarViaFormasdePagoEmpresa(busquedaViaC, context);
        String viaT = DataBaseBO.cargarViaFormasdePagoEmpresa(busquedaViaT, context);
        String viaF = DataBaseBO.cargarViaFormasdePagoEmpresa(busquedaViaF, context);
        String viaB = DataBaseBO.cargarViaFormasdePagoEmpresa(busquedaViaB, context);
        RadioButton rbEfectvo = findViewById(R.id.rbEfectivo);
        RadioButton rbCheq = findViewById(R.id.rbCheque);
        RadioButton rbTrans = findViewById(R.id.rbTrasnferencia);
        RadioButton rbTarjeta = findViewById(R.id.rbTarjetaCredito);
        RadioButton rbBitcoin = findViewById(R.id.rbBitcoin);

        if (viaE.equals("A")) {
            if (empresa.equals("AGCO")) {

                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        rbEfectvo.setText("Cash / Appropriation");

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                        rbEfectvo.setText("Efectivo / Consignación");
                    }
                }


            }
            rbEfectvo.setVisibility(View.VISIBLE);

        } else {
            rbEfectvo.setVisibility(View.GONE);
            rbEfectvo.invalidate();
            rbEfectvo.setChecked(false);
            rbEfectvo.setEnabled(false);
            rbEfectvo.setClickable(false);
        }
        if (viaC.equals("B")) {

            rbCheq.setVisibility(View.VISIBLE);


        } else {
            rbCheq.setVisibility(View.GONE);
            rbCheq.invalidate();
            rbCheq.setChecked(false);
            rbCheq.setEnabled(false);
            rbCheq.setClickable(false);
        }
        if (viaT.equals("O")) {
            if (empresa.equals("AGCO")) {

                rbTarjeta.setVisibility(View.GONE);


            } else {
                rbTarjeta.setVisibility(View.VISIBLE);
            }


        } else {
            rbTarjeta.setVisibility(View.GONE);
            rbTarjeta.invalidate();
            rbTarjeta.setChecked(false);
            rbTarjeta.setEnabled(false);
            rbTarjeta.setClickable(false);
        }
        if (viaF.equals("6")) {

            rbTrans.setVisibility(View.VISIBLE);


        } else {
            rbTrans.setVisibility(View.GONE);
            rbTrans.invalidate();
            rbTrans.setButtonDrawable(null);

            rbTrans.setChecked(false);
            rbTrans.setEnabled(false);
            rbTrans.setClickable(false);

        }
        if (viaB.equals("4")) {

            rbBitcoin.setVisibility(View.VISIBLE);

        } else {
            rbBitcoin.setChecked(false);
            rbBitcoin.setEnabled(false);
            rbBitcoin.setClickable(false);
            rbBitcoin.setVisibility(View.GONE);
            rbBitcoin.invalidate();
        }

    }

    ///METODO PARA ANTICPO y RECIBO POR LEGALIZAR CALCULA LAS FORMAS DE PAGO
    private void formasPagoAnticipRecibosLe() {


        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        listaFacturas3 = new ArrayList<>();
        final Vector<String> listaItems = new Vector<>();

        String claseDocument = "";
        String documentoFinanciero = "";
        double precioTotal = 0;
        String document = "";
        String nroRecibo = "";
        String empresa = "";
        String acert = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        listaFacturas3 = new ArrayList<>();

        if (facCollection == null) {

            if (anticipo != null) {

                if (anticipo.estado == true) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Payment Method (ADVANCE)"));

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Metodos Pago (ANTICIPO)"));

                        }
                    }

                }

                if (anticipo.estado == false) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Payment Method (RECEIPT LEGALIZE)"));

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Metodos Pago (RECIBO LEGALIZAR)"));

                        }
                    }

                }

            }

            tvMontoFactura = findViewById(R.id.tvMontoCarteraFP);
            tvTotalFormasPago = findViewById(R.id.tvTotalFormasPago);
            tvDiferenciaMetodosPago = findViewById(R.id.tvDiferenciaMetodosPago);
            claseDocumento.add("DZ");

            if (anticipo != null) {

                nroRecibo = clienteSel.consecutivo;
                documentosFinanciero.add(nroRecibo);
            }

            double DiferenciaFormasPago;
            double TotalFormasPago = 0;
            double DiferenciaFormasPagoPEND;
            double TotalFormasPagoPEND = 0;
            double DiferenciaFormasPagoE = 0;
            double TotalFormasPagoE = 0;
            double DiferenciaFormasPagoPEN = 0;
            double TotalFormasPagoPEn = 0;


            empresa = DataBaseBO.cargarEmpresa(context);
            final String finalEmpresa = empresa;
            //Actualizada 2

            /***       if (facCollection == null) {

             if (anticipo.estado == true) {
             nroRecibo = clienteSel.consecutivo;

             listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo);
             listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo);


             if (anticipo != null) {
             for (Facturas fac : listaFacturas2) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             } else if (anticipo == null) {
             for (Facturas fac : listaFacturas2) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             }

             if (anticipo != null) {
             for (Facturas fac : listaFacturas4) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             } else if (anticipo == null) {
             for (Facturas fac : listaFacturas4) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             }

             String str = "";

             for (int i = 0; i < listaFacturas3.size(); i++) {
             for (Facturas fruit : listaFacturas3) {
             str += "\'" + fruit.idPago + "\',";

             TotalFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
             DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
             TotalFormasPagoPEn = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);
             DiferenciaFormasPagoPEN = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);

             }
             }
             }

             if (anticipo.estado == false) {

             nroRecibo = clienteSel.consecutivo;

             listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo);
             listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo);


             if (anticipo != null) {
             for (Facturas fac : listaFacturas2) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             } else if (anticipo == null) {
             for (Facturas fac : listaFacturas2) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             }

             if (anticipo != null) {
             for (Facturas fac : listaFacturas4) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             } else if (anticipo == null) {
             for (Facturas fac : listaFacturas4) {
             acert = fac.idPago;
             listaFacturas3.add(fac);
             }
             }

             String str = "";

             for (int i = 0; i < listaFacturas3.size(); i++) {
             for (Facturas fruit : listaFacturas3) {
             str += "\'" + fruit.idPago + "\',";

             TotalFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
             DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
             TotalFormasPagoPEn = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);
             DiferenciaFormasPagoPEN = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);

             }
             }
             }

             }**/

            //  DiferenciaFormasPago = (Utilidades.formatearDecimales(DiferenciaFormasPagoE, 2) + Utilidades.formatearDecimales(DiferenciaFormasPagoPEN, 2));
            //  TotalFormasPago = (Utilidades.formatearDecimales(TotalFormasPagoE, 2) + Utilidades.formatearDecimales(TotalFormasPagoPEn, 2));

            DiferenciaFormasPago = (Utilidades.formatearDecimales(Utilidades.totalFormasPago(this), 2));
            TotalFormasPago = (Utilidades.formatearDecimales(Utilidades.totalFormasPago(this), 2));


            try {
                if (anticipo.estado == true) {

                    if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                            || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("es"));

                        if (TotalFormasPago == 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                        } else if (anticipo.valor < -1) {
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                        } else if (anticipo.valor > 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (DiferenciaFormasPago)));
                        }
                        tvMontoFactura.setText(formating.format(anticipo.valor));

                    } else {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("en"));

                        if (TotalFormasPago == 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                        } else if (anticipo.valor < -1) {
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                        } else if (anticipo.valor > 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (DiferenciaFormasPago)));
                        }
                        tvMontoFactura.setText(formating.format(anticipo.valor));

                    }

                }

                if (anticipo.estado == false) {

                    if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                            || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("es"));

                        if (TotalFormasPago == 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                        } else if (anticipo.valor < -1) {
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                        } else if (anticipo.valor > 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (DiferenciaFormasPago)));
                        }
                        tvMontoFactura.setText(formating.format(anticipo.valor));

                    } else {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("en"));

                        if (TotalFormasPago == 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                        } else if (anticipo.valor < -1) {
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(anticipo.valor - (-(DiferenciaFormasPago))));
                        } else if (anticipo.valor > 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(anticipo.valor - (DiferenciaFormasPago)));
                        }
                        tvMontoFactura.setText(formating.format(anticipo.valor));

                    }

                }

            } catch (Exception exception) {
                System.out.println("Error en la forma de pago parcial " + exception);
            }

        }
    }

    ///METODO PARA PARCIAL Y TOTAL
    private void formasPago() {


        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        listaFacturas3 = new ArrayList<>();
        final Vector<String> listaItems = new Vector<>();

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        String claseDocument = "";
        String documentoFinanciero = "";
        double precioTotal = 0;
        double precioTo = 0;
        String document = "";
        String nroRecibo = "";
        String empresa = "";
        String acert = "";
        empresa = DataBaseBO.cargarEmpresa(context);
        final String finalEmpresa = empresa;

        if (facCollection != null) {

            for (Cartera cartera1 : facCollection) {
                document = cartera1.getDocumento();
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                precioTotal += cartera1.getSaldo();

                claseDocument = cartera1.getConcepto();
                claseDocumento.add(claseDocument);
                documentt.add(document);
                documentosFinanciero.add(documentoFinanciero);
            }
            precioTo = Utilidades.formatearDecimales(precioTotal, 2);


            double DiferenciaFormasPago;

            double TotalFormasPago = 0;
            double DiferenciaFormasPagoPEND;
            double TotalFormasPagoPEND = 0;
            double DiferenciaFormasPagoE = 0;
            double TotalFormasPagoE = 0;
            double DiferenciaFormasPagoPEN = 0;
            double TotalFormasPagoPEn = 0;

            DiferenciaFormasPago = (Utilidades.formatearDecimales(Utilidades.totalFormasPago(this), 2));

            TotalFormasPago = (Utilidades.formatearDecimales(Utilidades.totalFormasPago(this), 2));
            double sumaXDOC = (Utilidades.formatearDecimales(Utilidades.SUMAVALORDOC(this), 2));
            double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsig(this), 2));

            double salfoAFA;
            double rfv = 0;
            salfoAFA = (Utilidades.formatearDecimales(Utilidades.totalDifereFAv(this), 2)) * -1;


            try {
                if (formaPago.parcial == true) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Methods of payment (PARTIAL)"));

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Metodos de Pago (PARCIAL)"));

                        }
                    }


                    if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                            || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("es"));
                        rfv = (Utilidades.formatearDecimales(formaPago.valor - (precioTo), 2));


                        if (TotalFormasPago == 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                        } else if (formaPago.valor < -1) {
                            tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(formaPago.valor - (-(DiferenciaFormasPago))));
                        } else if (formaPago.valor > 0) {


                            tvMontoFactura.setText(formating.format(formaPago.valor));


                            if (salfoAFA != 0) {

                                if (precioTo + salfoAFA - TotalFormasPago == formaPago.valor - (TotalFormasPago)) {
                                    if (precioTo + salfoAFA - TotalFormasPago + salfoAFA == formaPago.valor - TotalFormasPago + salfoAFA) {
                                        if (TotalFormasPago + salfoAFA == formaPago.valor) {
                                            tvTotalFormasPago.setText(formating.format(precioTo + salfoAFA));
                                            tvDiferenciaMetodosPago.setText(formating.format(precioTo + salfoAFA - formaPago.valor));
                                        } else {
                                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                                            tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - TotalFormasPago));
                                        }

                                    } else {
                                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                                        tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago)));
                                    }


                                } else {
                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago + salfoAFA));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago) - salfoAFA));

                                }

                            } else {
                                if (TotalFormasPago == sumaXValorConsignado) {

                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago)));

                                } else if (TotalFormasPago + rfv == sumaXValorConsignado) {
                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago + rfv));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago) - rfv));
                                } else {
                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago + rfv));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago) - rfv));
                                }

                            }

                        }
                        tvMontoFactura.setText(formating.format(formaPago.valor));


                    } else {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("en"));
                        rfv = (Utilidades.formatearDecimales(formaPago.valor - (precioTo), 2));

                        if (TotalFormasPago == 0) {

                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));

                        } else if (formaPago.valor < -1) {

                            tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(formaPago.valor - (-(DiferenciaFormasPago))));

                        } else if (formaPago.valor > 0) {


                            tvMontoFactura.setText(formating.format(formaPago.valor));


                            if (salfoAFA != 0) {

                                if (precioTo + salfoAFA - TotalFormasPago == formaPago.valor - (TotalFormasPago)) {
                                    if (precioTo + salfoAFA - TotalFormasPago + salfoAFA == formaPago.valor - TotalFormasPago + salfoAFA) {
                                        if (TotalFormasPago + salfoAFA == formaPago.valor) {
                                            tvTotalFormasPago.setText(formating.format(precioTo + salfoAFA));
                                            tvDiferenciaMetodosPago.setText(formating.format(precioTo + salfoAFA - formaPago.valor));
                                        } else {
                                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                                            tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - TotalFormasPago));
                                        }

                                    } else {
                                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                                        tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago)));
                                    }

                                } else {
                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago + salfoAFA));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago) - salfoAFA));

                                }

                            } else {
                                if (TotalFormasPago == sumaXValorConsignado) {

                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago)));

                                } else if (TotalFormasPago + rfv == sumaXValorConsignado) {
                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago + rfv));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago) - rfv));
                                } else {
                                    tvTotalFormasPago.setText(formating.format(TotalFormasPago + rfv));
                                    tvDiferenciaMetodosPago.setText(formating.format(formaPago.valor - (TotalFormasPago) - rfv));
                                }


                            }

                        }
                        tvMontoFactura.setText(formating.format(formaPago.valor));

                    }


                }

                if (formaPago.parcial == false) {


                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Methods of payment (TOTAL)"));

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            ActionBar barVista = getSupportActionBar();
                            Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                            barVista.setTitle(Utilidades.tituloFormato(this, "Metodos de Pago (TOTAL)"));

                        }
                    }


                    if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                            || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("es"));

                        if (TotalFormasPago == 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                        } else if (precioTo < -1) {
                            tvDiferenciaMetodosPago.setText(formating.format(precioTo - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(precioTo - (-(DiferenciaFormasPago))));
                        } else if (precioTo > 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(precioTo - (DiferenciaFormasPago)));
                        }
                        tvMontoFactura.setText(formating.format(precioTo));


                    } else {

                        NumberFormat formating = NumberFormat.getInstance(new Locale("en"));

                        if (TotalFormasPago == 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                        } else if (precioTo < -1) {
                            tvDiferenciaMetodosPago.setText(formating.format(precioTo - (-(DiferenciaFormasPago))));
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvMontoFactura.setText(formating.format(precioTo - (-(DiferenciaFormasPago))));
                        } else if (precioTo > 0) {
                            tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                            tvDiferenciaMetodosPago.setText(formating.format(precioTo - (DiferenciaFormasPago)));
                        }
                        tvMontoFactura.setText(formating.format(precioTo));

                    }

                }

            } catch (Exception exception) {
                System.out.println("Error en la forma de pago parcial " + exception);
            }


        }
    }

    private void metodosPago() {

        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);
        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        final List<String> IDS = new ArrayList<>();
        List<String> codigoUs = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        listaFacturas3 = new ArrayList<>();
        String claseDocument = "";

        String document = "";

        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                document = cartera1.getDocumento();
                precioTotal += cartera1.getSaldo();
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                claseDocument = cartera1.getConcepto();
                claseDocumento.add(claseDocument);
                documentt.add(document);
                documentosFinanciero.add(documentoFinanciero);

            }
        }

        claseDocumento.add("DZ");
        String nroRecibo = "";


        nroRecibo = clienteSel.consecutivo;
        String acert = "";
        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, context);
        listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo, context);


        if (anticipo != null) {
            for (Facturas fac : listaFacturas2) {
                acert = fac.idPago;
                listaFacturas3.add(fac);
            }
        } else if (anticipo == null) {
            for (Facturas fac : listaFacturas2) {
                acert = fac.idPago;
                listaFacturas3.add(fac);
            }
        }

        if (anticipo != null) {
            for (Facturas fac : listaFacturas4) {
                acert = fac.idPago;
                listaFacturas3.add(fac);
            }
        } else if (anticipo == null) {
            for (Facturas fac : listaFacturas4) {
                acert = fac.idPago;
                listaFacturas3.add(fac);
            }
        }


        listaFacturasPend = DataBaseBO.cargarFacturasParametroReciboPendientes(clienteSel.codigo, listaFacturas3, listaItems, clienteSel.consecutivo, context);
        listaFacturas = DataBaseBO.cargarFacturasParametro(listaFacturas3, listaItems, clienteSel.consecutivo, context);

        for (Facturas facturas123 : listaFacturas) {

            listaFacturasJuntas.add(facturas123);

        }

        for (Facturas facturas123 : listaFacturasPend) {

            listaFacturasJuntas.add(facturas123);

        }


        RecyclerView rvListaCarteraFactura = findViewById(R.id.rvListaFacturas);
        rvListaCarteraFactura.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        final AdapterFacturas adapter = new AdapterFacturas(listaFacturasJuntas, context);
        rvListaCarteraFactura.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void onRadioButtonClicked(View view) {

        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        final List<String> claseDocumento = new ArrayList<>();
        List<String> codigoUs = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        String claseDocument = "";
        String codigoU = "";
        String nombreU = "";
        double precioTotal = 0;
        String document = "";
        for (Cartera cartera1 : facCollection) {
            document = cartera1.getDocumento();
            precioTotal += cartera1.getSaldo();

            claseDocument = cartera1.getConcepto();
            claseDocumento.add(claseDocument);
            documentt.add(document);

        }


        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rbEfectivo:

                break;
            case R.id.rbCheque:

                break;
            case R.id.rbTrasnferencia:

                break;
            case R.id.rbTarjetaCredito:

                break;
            case R.id.rbBitcoin:

                break;
        }
    }

    public void guardarTotalFinalAnticipo() {

        guardarTotalFinal = false;
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);
        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                precioTotal += cartera1.getSaldo();
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        if (anticipo != null) {
            String parametroCme = "A";

            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);

        }

        if (facCollection == null) {

            if (anticipo.estado == true) {
                nroRecibo = clienteSel.consecutivo;
                String acert = "";
                listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, context);
                listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo, context);


                if (anticipo != null) {
                    for (Facturas fac : listaFacturas2) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                } else if (anticipo == null) {
                    for (Facturas fac : listaFacturas2) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                }

                if (anticipo != null) {
                    for (Facturas fac : listaFacturas4) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                } else if (anticipo == null) {
                    for (Facturas fac : listaFacturas4) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                }

                String str = "";

                for (int i = 0; i < listaFacturas3.size(); i++) {
                    for (Facturas fruit : listaFacturas3) {
                        str += "\'" + fruit.idPago + "\',";
                        TotalFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str, clienteSel.consecutivo, context);
                        DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str, clienteSel.consecutivo, context);
                        TotalFormasPagoPEn = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str, context);
                        DiferenciaFormasPagoPEN = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str, context);

                    }
                }
            } else if (anticipo.estado == false) {

                nroRecibo = clienteSel.consecutivo;
                String acert = "";
                listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, context);
                listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo, context);


                if (anticipo != null) {
                    for (Facturas fac : listaFacturas2) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                } else if (anticipo == null) {
                    for (Facturas fac : listaFacturas2) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                }

                if (anticipo != null) {
                    for (Facturas fac : listaFacturas4) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                } else if (anticipo == null) {
                    for (Facturas fac : listaFacturas4) {
                        acert = fac.idPago;
                        listaFacturas3.add(fac);
                    }
                }

                String str = "";

                for (int i = 0; i < listaFacturas3.size(); i++) {
                    for (Facturas fruit : listaFacturas3) {
                        str += "\'" + fruit.idPago + "\',";
                        TotalFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str, clienteSel.consecutivo, context);
                        //  DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str);
                        //   TotalFormasPagoPEn = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);
                        //   DiferenciaFormasPagoPEN = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str);

                    }
                }
            }
        }

        DiferenciaFormasPago = (TotalFormasPagoE);
        TotalFormasPago = (Utilidades.formatearDecimales(TotalFormasPagoE, 2));

        if (facCollection == null) {

            if (anticipo.estado == true) {


                if (TotalFormasPago != anticipo.valor) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "Total forms of payment must be equal to the amount collected").show();
                                }
                            });


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "El total de formas de pago tiene que ser igual al monto del recaudo").show();
                                }
                            });

                        }
                    }


                }

                if (TotalFormasPago == anticipo.valor) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Alert.vistaDialogoCerrarSesion(context, "¿Are you sure you want to finish the collection? ", "Collect",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    METODO_PAGO = Constantes.PAGO_ANTICIPO_USA;
                                                    mostrarDialogFragmentFirma(listaFacturas3.isEmpty() ? "0" : listaFacturas3.get(0).idPago);
                                                    Alert.dialogo.cancel();
                                                }
                                            }, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Alert.dialogo.cancel();
                                                }
                                            });
                                }
                            });

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Alert.vistaDialogoCerrarSesion(context, "¿Esta seguro que desea terminar el recaudo? ", "Terminar Recaudo",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    METODO_PAGO = Constantes.PAGO_ANTICIPO_ESP;
                                                    mostrarDialogFragmentFirma(listaFacturas3.isEmpty() ? "0" : listaFacturas3.get(0).idPago);
                                                    Alert.dialogo.cancel();
                                                }
                                            }, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Alert.dialogo.cancel();
                                                }
                                            });
                                }
                            });

                        }
                    }


                }

                if (anticipo.valor - Utilidades.formatearDecimales(DiferenciaFormasPago, 2) != 0) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "The difference has to be 0 to finish the collection").show();
                                }
                            });


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "La diferencia tiene que ser 0 para finalizar el recaudo").show();
                                }
                            });


                        }
                    }
                }

            }

            if (anticipo.estado == false) {


                if (Utilidades.formatearDecimales(TotalFormasPago, 2) != anticipo.valor) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "Total forms of payment must be equal to the amount collected").show();
                                }
                            });


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "El total de formas de pago tiene que ser igual al monto del recaudo").show();
                                }
                            });

                        }
                    }

                }

                if (TotalFormasPago == anticipo.valor) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Alert.vistaDialogoCerrarSesion(context, "¿Are you sure you want to finish the collection? ", "Collect",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    METODO_PAGO = Constantes.PAGO_RECIBO_POR_LEGALIZAR_USA;
                                                    mostrarDialogFragmentFirma(listaFacturas3.isEmpty() ? "0" : listaFacturas3.get(0).idPago);
                                                    Alert.dialogo.cancel();
                                                }
                                            }, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Alert.dialogo.cancel();
                                                }
                                            });
                                }
                            });


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();

                                    Alert.vistaDialogoCerrarSesion(context, "¿Esta seguro que desea terminar el recaudo? ", "Terminar Recaudo",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    METODO_PAGO = Constantes.PAGO_RECIBO_POR_LEGALIZAR_ESP;
                                                    mostrarDialogFragmentFirma(listaFacturas3.isEmpty() ? "0" : listaFacturas3.get(0).idPago);
                                                    Alert.dialogo.cancel();
                                                }
                                            }, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Alert.dialogo.cancel();
                                                }
                                            });
                                }
                            });

                        }
                    }


                }

                if (anticipo.valor - DiferenciaFormasPago != 0) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "La diferencia tiene que ser 0 para finalizar el recaudo").show();
                                }
                            });

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toasty.warning(getApplicationContext(), "La diferencia tiene que ser 0 para finalizar el recaudo").show();
                                }
                            });

                        }
                    }

                }


            }
        }
    }

    public void dialogTerminarRecaudoAnticipoESP() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {

            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {

                Toasty.warning(getApplicationContext(), "No tiene informacion por enviar....", Toasty.LENGTH_SHORT).show();


            }


        } else {
            Toasty.warning(getApplicationContext(), "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoAnticipoUSA() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {

            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;

            } else {
                Toasty.warning(getApplicationContext(), "You have no information to send....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No internet connection.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoReciboPorLegalizarESP() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {

            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "No tiene informacion por enviar....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoReciboPorLegalizarUSA() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {

            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "You have no information to send....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No internet connection.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void guardarTotalFinal() {


        guardarTotalFinal = true;
        double precio = 0;

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);
        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                precio += cartera1.getSaldo();
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        double precioTo = Utilidades.formatearDecimales(precio, 2);

        if (formaPago != null) {


            // listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientes(nroRecibo);


            //      for (Facturas fac : listaFacturas4) {
            //         acert = fac.idPago;
            //        listaFacturas3.add(fac);
            //    }


            for (int i = 0; i < listaFacturas2.size(); i++) {
                for (Facturas fruit : listaFacturas2) {
                    str += "\'" + fruit.idPago + "\',";
                    Utilidades.formatearDecimales(TotalFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str, clienteSel.consecutivo, context), 2);
                    //    Utilidades.formatearDecimales(DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str), 2);
                    //   Utilidades.formatearDecimales(TotalFormasPagoPEn = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str), 2);
                    //    Utilidades.formatearDecimales(DiferenciaFormasPagoPEN = DataBaseBO.TotalFormasPagoAnticipoRROGPendientes(str), 2);

                }
            }

        }

        DiferenciaFormasPago = (Utilidades.formatearDecimales(TotalFormasPagoE, 2) /**+ Utilidades.formatearDecimales(DiferenciaFormasPagoPEN, 2)**/);
        TotalFormasPago = (Utilidades.formatearDecimales(TotalFormasPagoE, 2) /**+ Utilidades.formatearDecimales(TotalFormasPagoPEn, 2)**/);
        double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsig(this), 2));


        if (formaPago.parcial == true) {
            double salfoAFA;
            salfoAFA = (Utilidades.formatearDecimales(Utilidades.totalDifereFAv(this), 2)) * -1;


            if ((formaPago.valor == precioTo + salfoAFA) && ((TextView) findViewById(R.id.tvDiferenciaMetodosPago)).toString().equals("0")) {


                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(context, "¿Are you sure you want to finish the collection? ", "Collect",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                METODO_PAGO = Constantes.PAGO_PARCIAL_USA;
                                                mostrarDialogFragmentFirma(listaFacturas2.isEmpty() ? "0" : listaFacturas2.get(0).idPago);
                                                Alert.dialogo.cancel();
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });


                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(context, "¿Esta seguro que desea terminar el recaudo? ", "Terminar Recaudo",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                METODO_PAGO = Constantes.PAGO_PARCIAL_ESP;
                                                mostrarDialogFragmentFirma(listaFacturas2.isEmpty() ? "0" : listaFacturas2.get(0).idPago);
                                                Alert.dialogo.cancel();
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });

                    }
                }


            } else if (formaPago.valor == Utilidades.formatearDecimales(TotalFormasPago, 2)) {

                if ((Utilidades.formatearDecimales(formaPago.valor, 2)) == TotalFormasPago) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            if (progressDialog != null)
                                progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    {
                                        Alert.vistaDialogoCerrarSesion(context, "¿Are you sure you want to finish the collection? ", "Collect",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        METODO_PAGO = Constantes.PAGO_TOTAL_PARCIAL_USA;
                                                        mostrarDialogFragmentFirma(listaFacturas2.isEmpty() ? "0" : listaFacturas2.get(0).idPago);
                                                        Alert.dialogo.cancel();
                                                    }
                                                }, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Alert.dialogo.cancel();
                                                    }
                                                });
                                    }
                                }
                            });


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            if (progressDialog != null)
                                progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    {
                                        Alert.vistaDialogoCerrarSesion(context, "¿Esta seguro que desea terminar el recaudo? ", "Terminar Recaudo",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        METODO_PAGO = Constantes.PAGO_TOTAL_PARCIAL_ESP;
                                                        mostrarDialogFragmentFirma(listaFacturas2.isEmpty() ? "0" : listaFacturas2.get(0).idPago);
                                                        Alert.dialogo.cancel();
                                                    }
                                                }, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Alert.dialogo.cancel();
                                                    }
                                                });
                                    }
                                }
                            });

                        }
                    }


                }

            } else {
                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {

                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "The difference has to be 0 to finish the collection").show();
                            }
                        });

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "La diferencia tiene que ser 0 para finalizar el recaudo").show();
                            }
                        });

                    }
                }
            }


        }

        if (formaPago.parcial == false) {


            if (!Utilidades.formatearDecimales(TotalFormasPago, 2).equals(Utilidades.formatearDecimales(precio, 2))) {
                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "Total forms of payment must be equal to the amount collected").show();
                            }
                        });

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "El total de formas de pago tiene que ser igual al monto del recaudo").show();
                            }
                        });

                    }
                }

            }

            if (TotalFormasPago == Utilidades.formatearDecimales(precio, 2)) {
                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(context, "¿Are you sure you want to finish the collection? ", "Collect",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                METODO_PAGO = Constantes.PAGO_TOTAL_USA;
                                                mostrarDialogFragmentFirma(listaFacturas2.isEmpty() ? "0" : listaFacturas2.get(0).idPago);
                                                Alert.dialogo.cancel();
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });


                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(context, "¿Esta seguro que desea terminar el recaudo? ", "Terminar Recaudo",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                METODO_PAGO = Constantes.PAGO_TOTAL_ESP;
                                                mostrarDialogFragmentFirma(listaFacturas2.isEmpty() ? "0" : listaFacturas2.get(0).idPago);
                                                Alert.dialogo.cancel();
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });

                    }
                }


            }

            if (Utilidades.formatearDecimales(precio, 2) - Utilidades.formatearDecimales(DiferenciaFormasPago, 2) != 0) {

                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {

                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "The difference has to be 0 to finish the collection").show();
                            }
                        });

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (progressDialog != null)
                            progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "La diferencia tiene que ser 0 para finalizar el recaudo").show();
                            }
                        });

                    }
                }
            }

        }


    }

    public void dialogTerminarRecaudoPagoTotalUSA() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {

            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "You have no information to send....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No internet connection.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoPagoTotalESP() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {

            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "No tiene informacion por enviar....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoPagoParcialUSA() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {


            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "You have no information to send....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No internet connection.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoPagoTotalParcialUSA() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {


            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "You have no information to send....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No internet connection.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoPagoTotalParcialESP() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {


            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String empresa;

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "No tiene informacion por enviar....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void dialogTerminarRecaudoPagoParcialESP() {
        Alert.dialogo.cancel();

        if (Utilidades.verificarNetwork(context)) {


            if (DataBaseBO.hayInformacionXEnviar(context)) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                String consec = "";
                String negocio = "";
                String vendedor = "";
                int contador = 1;
                int Position = 6;
                consec = DataBaseBO.cargarConsecutivo(context);
                negocio = DataBaseBO.cargarNegocioConsecutivo(context);
                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                int consec1 = Integer.parseInt(consec);
                int vendedorsum = Integer.parseInt(vendedor);
//                consec1 = consec1 + contador;
                DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha, context);

            } else {
                Toasty.warning(getApplicationContext(), "No tiene informacion por enviar....", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.warning(getApplicationContext(), "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();
            volverPantallaPrincipal();
        }
    }

    public void guardarFormaPago(View view) throws JSONException {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        // Check which radio button was clicked
        final RadioButton rbEfectivo = findViewById(R.id.rbEfectivo);
        final RadioButton rbCheque = findViewById(R.id.rbCheque);
        final RadioButton rbTrasnferencia = findViewById(R.id.rbTrasnferencia);
        final RadioButton rbTarjetaCredito = findViewById(R.id.rbTarjetaCredito);
        final RadioButton rbBitcoin = findViewById(R.id.rbBitcoin);

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_MultiplesFotos", true);
        editor1.remove("estado_MultiplesFotos");

        editor1.commit();


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(context);
        formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);
        PreferencesFotos.vaciarPreferencesFotoSeleccionada(context);

        if (formaPago != null) {

            if (formaPago.parcial == true) {


                if (rbEfectivo.isChecked()) {


                    MetodoDePagoEfectivo.vistaDialogoEfectivo(context
                    );


                }

                if (rbCheque.isChecked()) {


                    MetodoDePagoCheque.vistaDialogoCheque(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });


                }

                if (rbTrasnferencia.isChecked()) {


                    MetodoDePagoTransferencia.vistaDialogoTransferencia(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });


                }

                if (rbTarjetaCredito.isChecked()) {


                    MetodoDePagoTarjeta.vistaDialogoTarjetaCredito(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                }
                            });


                }

                if (rbBitcoin.isChecked()) {

                    MetodoDePagoBitcoin.vistaDialogoBitcoin(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                }
                            });


                }

            } else if (formaPago.parcial == false) {

                if (rbEfectivo.isChecked()) {

                    MetodoDePagoEfectivo.vistaDialogoEfectivo(context
                    );


                }

                if (rbCheque.isChecked()) {

                    MetodoDePagoCheque.vistaDialogoCheque(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    final RadioButton rbCheque = findViewById(R.id.rbCheque);
                                    rbCheque.setTextColor(Color.GRAY);
                                    rbCheque.setChecked(false);
                                    rbCheque.setEnabled(false);
                                    rbCheque.setClickable(false);
                                    MetodoDePagoCheque.dialogo.cancel();


                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final RadioButton rbCheque = findViewById(R.id.rbCheque);

                                    rbCheque.setChecked(true);
                                    rbCheque.setEnabled(true);
                                    rbCheque.setClickable(true);
                                    MetodoDePagoCheque.dialogo.cancel();
                                }
                            });


                }

                if (rbTrasnferencia.isChecked()) {

                    MetodoDePagoTransferencia.vistaDialogoTransferencia(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    final RadioButton rbTrasnferencia = findViewById(R.id.rbTrasnferencia);
                                    rbTrasnferencia.setTextColor(Color.GRAY);
                                    rbTrasnferencia.setChecked(false);
                                    rbTrasnferencia.setEnabled(false);
                                    rbTrasnferencia.setClickable(false);
                                    AlertPagos.dialogo.cancel();


                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final RadioButton rbTrasnferencia = findViewById(R.id.rbTrasnferencia);

                                    rbTrasnferencia.setChecked(true);
                                    rbTrasnferencia.setEnabled(true);
                                    rbTrasnferencia.setClickable(true);
                                    AlertPagos.dialogo.cancel();
                                }
                            });


                }

                if (rbTarjetaCredito.isChecked()) {

                    MetodoDePagoTarjeta.vistaDialogoTarjetaCredito(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    final RadioButton rbTarjetaCredito = findViewById(R.id.rbTarjetaCredito);
                                    rbTarjetaCredito.setTextColor(Color.GRAY);
                                    rbTarjetaCredito.setChecked(false);
                                    rbTarjetaCredito.setEnabled(false);
                                    rbTarjetaCredito.setClickable(false);
                                    AlertPagos.dialogo.cancel();


                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final RadioButton rbTarjetaCredito = findViewById(R.id.rbTarjetaCredito);

                                    rbTarjetaCredito.setChecked(true);
                                    rbTarjetaCredito.setEnabled(true);
                                    rbTarjetaCredito.setClickable(true);
                                    AlertPagos.dialogo.cancel();
                                }
                            });


                }

                if (rbBitcoin.isChecked()) {

                    MetodoDePagoBitcoin.vistaDialogoBitcoin(context, "Exito", "Información enviada correctamente",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    final RadioButton rbBitcoin = findViewById(R.id.rbBitcoin);
                                    rbBitcoin.setTextColor(Color.GRAY);
                                    rbBitcoin.setChecked(false);
                                    rbBitcoin.setEnabled(false);
                                    rbBitcoin.setClickable(false);
                                    AlertPagos.dialogo.cancel();


                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final RadioButton rbBitcoin = findViewById(R.id.rbBitcoin);
                                    rbBitcoin.setChecked(true);
                                    rbBitcoin.setEnabled(true);
                                    rbBitcoin.setClickable(true);
                                    AlertPagos.dialogo.cancel();
                                }
                            });


                }

            }

        } else if (formaPago == null) {

            if (rbEfectivo.isChecked()) {

                MetodoDePagoEfectivo.vistaDialogoEfectivo(context
                );


            }

            if (rbCheque.isChecked()) {


                MetodoDePagoCheque.vistaDialogoCheque(context, "Exito", "Información enviada correctamente",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final RadioButton rbCheque = findViewById(R.id.rbCheque);


                            }
                        });


            }

            if (rbTrasnferencia.isChecked()) {

                MetodoDePagoTransferencia.vistaDialogoTransferencia(context, "Exito", "Información enviada correctamente",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                            }
                        });


            }

            if (rbTarjetaCredito.isChecked()) {

                MetodoDePagoTarjeta.vistaDialogoTarjetaCredito(context, "Exito", "Información enviada correctamente",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

            }

            if (rbBitcoin.isChecked()) {

                MetodoDePagoBitcoin.vistaDialogoBitcoin(context, "Exito", "Información enviada correctamente",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                            }
                        });
            }

        }

    }

    public void Cancelar(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Alert.vistaDialogoCerrarSesion(context, "¿Are you sure you want to cancel the collection?", "Cancel Collection", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);
                        final List<String> documentosFinanciero = new ArrayList<>();

                        Gson gson3 = new Gson();
                        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(context);
                        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

                        Gson gson1 = new Gson();
                        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(context);
                        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

                        String documentoFinanciero = "";

                        if (facCollection != null) {

                            documentoFinanciero = clienteSel.consecutivo;
                            documentosFinanciero.add(documentoFinanciero);

                        }

                        if (anticipo != null) {

                            String nroRecibo = clienteSel.consecutivo;
                            documentosFinanciero.add(nroRecibo);

                        }

                        if (facCollection == null) {

                            if (anticipo != null) {

                                if (anticipo.estado == true) {

                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);

                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_ReciboDinero", true);
                                    editor1.remove("estado_ReciboDinero");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");
                                    editor1.commit();
                                    Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                    startActivity(vistaClienteActivity);
                                    finish();
                                    Alert.dialogo.cancel();

                                }

                                if (anticipo.estado == false) {

                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);

                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_ReciboDinero", true);
                                    editor1.remove("estado_ReciboDinero");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");
                                    editor1.commit();
                                    Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                    startActivity(vistaClienteActivity);
                                    finish();
                                    Alert.dialogo.cancel();

                                }
                            }

                        }

                        if (facCollection != null) {


                            if (formaPago.parcial == true) {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                                SharedPreferences.Editor editor1 = settings.edit();

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");

                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");
                                editor1.commit();
                                finish();
                                Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                startActivity(vistaClienteActivity);
                                Alert.dialogo.cancel();

                            }

                            if (formaPago.parcial == false) {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = settings.edit();
                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");
                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");

                                editor1.commit();
                                Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                startActivity(vistaClienteActivity);
                                finish();
                                Alert.dialogo.cancel();

                            }
                        }

                        DataBaseBO.eliminarFotosSinDocumentosAsociados(context);
                        Alert.dialogo.cancel();
                        finish();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alert.dialogo.cancel();

                    }
                });

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Alert.vistaDialogoCerrarSesion(context, "¿Esta seguro que desea cancelar el recaudo?", "Cancelar Recaudo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);
                        final List<String> documentosFinanciero = new ArrayList<>();

                        Gson gson3 = new Gson();
                        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(context);
                        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

                        Gson gson1 = new Gson();
                        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(context);
                        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

                        String documentoFinanciero = "";

                        if (facCollection != null) {
                            documentoFinanciero = clienteSel.consecutivo;
                            documentosFinanciero.add(documentoFinanciero);
                        }

                        if (anticipo != null) {

                            String nroRecibo = clienteSel.consecutivo;
                            documentosFinanciero.add(nroRecibo);

                        }

                        if (facCollection == null) {

                            if (anticipo != null) {

                                if (anticipo.estado == true) {

                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_ReciboDinero", true);
                                    editor1.remove("estado_ReciboDinero");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");
                                    editor1.commit();
                                    Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                    startActivity(vistaClienteActivity);
                                    finish();
                                    Alert.dialogo.cancel();

                                }

                                if (anticipo.estado == false) {


                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                    DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                    DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                                    SharedPreferences.Editor editor1 = settings.edit();

                                    editor1.putBoolean("estado_VistaCliente", true);
                                    editor1.remove("estado_VistaCliente");
                                    editor1.putBoolean("estado_ReciboDinero", true);
                                    editor1.remove("estado_ReciboDinero");
                                    editor1.putBoolean("estado_Cartera", true);
                                    editor1.remove("estado_Cartera");
                                    editor1.putBoolean("estado_AnticipoRecibo", true);
                                    editor1.remove("estado_AnticipoRecibo");
                                    editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                    editor1.remove("estado_FacturasSeleccionadas");
                                    editor1.putBoolean("estado_FormaPago", true);
                                    editor1.remove("estado_FormaPago");
                                    editor1.putBoolean("estado_FormaPagoTotal", true);
                                    editor1.remove("estado_FormaPagoTotal");
                                    editor1.putBoolean("estado_FormaPagoParcial", true);
                                    editor1.remove("estado_FormaPagoParcial");
                                    editor1.putBoolean("estado_MultiplesFotos", true);
                                    editor1.remove("estado_MultiplesFotos");
                                    editor1.commit();
                                    Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                    startActivity(vistaClienteActivity);
                                    finish();
                                    Alert.dialogo.cancel();

                                }
                            }

                        }

                        if (facCollection != null) {


                            if (formaPago.parcial == true) {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                                SharedPreferences.Editor editor1 = settings.edit();

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");
                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");
                                editor1.commit();
                                finish();
                                Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                startActivity(vistaClienteActivity);
                                Alert.dialogo.cancel();

                            }

                            if (formaPago.parcial == false) {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                DataBaseBO.eliminarConsecutivoId(vendedor, context);

                                DataBaseBO.eliminarRecaudosTotalAnticiPenD(documentosFinanciero, context);
                                DataBaseBO.eliminarFoto(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosTotalAntici(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosPendientesDataBase(documentosFinanciero, context);
                                DataBaseBO.eliminarRecaudosRealizadosDataBase(documentosFinanciero, context);

                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = settings.edit();
                                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
                                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());

                                editor1.putBoolean("estado_VistaCliente", true);
                                editor1.remove("estado_VistaCliente");
                                editor1.putBoolean("estado_Cartera", true);
                                editor1.remove("estado_Cartera");
                                editor1.putBoolean("estado_ReciboDinero", true);
                                editor1.remove("estado_ReciboDinero");
                                editor1.putBoolean("estado_AnticipoRecibo", true);
                                editor1.remove("estado_AnticipoRecibo");
                                editor1.putBoolean("estado_FacturasSeleccionadas", true);
                                editor1.remove("estado_FacturasSeleccionadas");
                                editor1.putBoolean("estado_FormaPago", true);
                                editor1.remove("estado_FormaPago");
                                editor1.putBoolean("estado_FormaPagoTotal", true);
                                editor1.remove("estado_FormaPagoTotal");
                                editor1.putBoolean("estado_FormaPagoParcial", true);
                                editor1.remove("estado_FormaPagoParcial");
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");

                                editor1.commit();
                                Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                                startActivity(vistaClienteActivity);
                                finish();
                                Alert.dialogo.cancel();

                            }
                        }

                        DataBaseBO.eliminarFotosSinDocumentosAsociados(context);

                        Alert.dialogo.cancel();
                        finish();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alert.dialogo.cancel();

                    }
                });
            }
        }


    }

    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FinalPrincipal", true);
        editor1.commit();
    }

    @Override
    public Serializable facturaCartera(List<Facturas> facturas) {

        try {
            Gson gson = new Gson();
            String jsonStringObject = gson.toJson(facturas);
            PreferencesCarteraFactura.guardarFacturaSeleccionada(this, jsonStringObject);


        } catch (Exception e) {
            System.out.println("error serializable" + e);
        }

        return null;
    }


    public void OnClickImprimir(View view) {

        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        SharedPreferences settings = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
        String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");

        if (macImpresora.equals("-")) {

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Alert.nutresaShow(context, "Information", "No Printer Set. Please first Configure the Printer!", "OK", null,

                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Alert.nutresaShow(context, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

                }
            }


        } else {

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    progressDialog = ProgressDialog.show(context, "",
                            "Please Wait...\n\nProcessing Information!", true);
                    progressDialog.show();

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    progressDialog = ProgressDialog.show(context, "",
                            "Por Favor Espere...\n\nProcesando Informacion!", true);
                    progressDialog.show();

                }
            }


            SharedPreferences set = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
            String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

            if (!tipoImpresora.equals("Intermec")) {
                ImprimirTirilla(macImpresora);
/*sewooLKP20 = new SewooLKP20(FormConfigPrinterNuevo.this);
imprimirSewooLKP20(macImpresora);*/
            } else {
                ImprimirTirilla(macImpresora);
            }
        }

    }

    public void imprimirTirillaGeneral(final String macAddress) {
        System.out.println("Entrу a imprimirTirillaGeneral");

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

                            //
                            if (socket != null) {

                                if (!tipoImpresora.equals("Intermec")) {

                                    socket.connect();
                                    Thread.sleep(3500);

                                    ReporstPrinter.ImprimiendoPrinter(socket, PrinterBO.formatoTirillaEntrega1(clienteSel.codigo, listaFacturas3, context));
                                    // OutputStream stream =
                                    // socket.getOutputStream();
                                    // String strPrint =
                                    // PrinterBO.formatoPrueba();
                                    // stream.write(strPrint.getBytes("UTF-8"));
                                    // Thread.sleep(3500);
                                    handlerFinish.sendEmptyMessage(0);

                                } else {

                                    socket.connect();
                                    Thread.sleep(3500);

                                    ReporstPrinter.ImprimiendoPrinter(socket, PrinterBO.formatoTirillaEntrega1(clienteSel.codigo, listaFacturas3, context));

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

                        context = context;
                        handlerFinish.sendEmptyMessage(0);
                    }

                    Looper.myLooper().quit();

                } catch (Exception e) {

                    String motivo = e.getMessage();

                    mensaje = "No se pudo ejecutar la Impresion.";

                    if (motivo != null) {
                        mensaje += "\n\n" + motivo;
                    }

                    context = context;
                    handlerFinish.sendEmptyMessage(0);

                } finally {

                    try {

                        /*
                         * Thread.sleep(3500);
                         *
                         * if (socket != null) { socket.close(); }
                         */
                    } catch (Exception e) {

                    }
                }
            }

        }).start();
    }


    private void ImprimirTirilla(final String macAddress) {

        List<Facturas> listaFacturas2 = DataBaseBO.cargarIdPagoOGRecaudosPendientes(nroRecibo, context);
        List<Facturas> listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientesRecaudos(nroRecibo, context);

        List<Facturas> listaFacturas3 = new ArrayList<>();

        listaFacturas3.addAll(listaFacturas2);

        listaFacturas3.addAll(listaFacturas4);
        new Thread(new Runnable() {
            public void run() {
                ZebraPrinterConnection printerConnection = null;
                try {
                    printerConnection = new BluetoothPrinterConnection(macAddress);
                    Looper.prepare();
                    //Abre la Conexion, la conexion Fisica es establecida aqui.
                    printerConnection.open();
                    ZebraPrinter zebraPrinter = ZebraPrinterFactory.getInstance(printerConnection);
                    PrinterLanguage printerLanguage = zebraPrinter.getPrinterControlLanguage();
                    Log.w("FormEstadisticaPedidos", "ImprimirTirilla -> Lenguaje de la Impresora " + printerLanguage);
                    // Envia los datos a la Impresora como un Arreglo de bytes.

                    //        String footer = PrinterBO.formatoTirillaEntrega1(clienteSel.codigo, listaFacturas3);
                    //
                    zebraPrinter.getPrinterControlLanguage();
                    String empresas = "";
                    empresas = DataBaseBO.cargarEmpresa(context);
                    String cpclData = "";

                    if (empresas.equals("ADHB")) {

                        cpclData = PrinterBO.formatoTirillaEntregaEmpresaEspaRecaudosPendientesNEW(getApplicationContext(), clienteSel.codigo, listaFacturas3, nroRecibo);

                    } else if (empresas.equals("AGUC")) {

                        cpclData = PrinterBO.formatoTirillaEntregaEmpresaUSAPendientesNEW(getApplicationContext(), clienteSel.codigo, listaFacturas3, nroRecibo);


                    } else {
                        cpclData = PrinterBO.formatoTirillaEntrega2(clienteSel.codigo, listaFacturas3, context);

                    }


                    String cpc = cpclData + "\n";
                    printerConnection.write(cpc.getBytes());
                    // Se asegura que los datos son enviados a la Impresora antes de cerrar la conexion.
                    Thread.sleep(100);
                    handlerFinish.sendEmptyMessage(0);
                    Looper.myLooper().quit();
                    volverPantallaPrincipal();
                } catch (Exception e) {
                    Log.e("FormEstadisticaPedidos", "ImprimirTirilla -> " + e.getMessage(), e);
                    mensaje = "No se pudo Imprimir.\n\n" + e.getMessage();
                    mensaje = "No se pudo Imprimir.\n\n" + e.getMessage();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "No se pudo Imprimir: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                    handlerFinish.sendEmptyMessage(0);
                    Looper.myLooper().quit();
                    volverPantallaPrincipal();
                } finally {                        // Cierra la conexion para liberar Recursos
                    try {
                        if (printerConnection != null) printerConnection.close();
                    } catch (ZebraPrinterConnectionException e) {
                        Log.e("FormEstadisticaPedidos", "ImprimirTirilla", e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "No se pudo Imprimir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                volverPantallaPrincipal();
                            }
                        });
                        handlerFinish.sendEmptyMessage(0);
                        Looper.myLooper().quit();
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

            DataBaseBO.borrarInfoTemp(context);

            estadoEnviadoRespuesta = true;
            Alert.dialogo.cancel();

            volverPantallaPrincipal();
        }
    };


    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        try {

            switch (codeRequest) {

                case Constantes.ENVIARINFORMACION:
                    enviarInfo(ok, respuestaServer, msg);

                    System.out.println("respuesta server " + respuestaServer);

                    break;

                case Constantes.DESCARGARINFO:
                    descargarInfo(ok, respuestaServer, msg);

                    break;

            }

        } catch (Exception exception) {
            System.out.println("Error en el repSync en el modulo Metodos de pago" + exception);
        }

    }

    private void descargarInfo(boolean ok, String respuestaServer, String msg) {

        if(progressDoalog != null)
            progressDoalog.dismiss();

        try {

            if (ok) {

                context.runOnUiThread(new Runnable() {

                    public void run() {

                        if (msg.contains("Unable to resolve host")) {

                            if(dialogResumenFirmaActivity != null)
                                dialogResumenFirmaActivity.saveButton.setEnabled(true);

                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(context, "No internet connection.", Toasty.LENGTH_SHORT).show();


                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(context, "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();

                                }
                            }
                        }
                        else
                        {
                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null)
                            {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Alert.alertGeneral(context, null, "The information is correctly recorded", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Alert.dialogo.cancel();

                                            String empresa = DataBaseBO.cargarEmpresa(context);

                                            if (empresa.equals("ADHB")) {
                                                //DIALOGO IMPRIMIR
                                                Alert.alertGeneral(context, null, "¿ print receipt ?", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        v.setEnabled(false);

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                v.setEnabled(true);
                                                            }
                                                        }, 600);


                                                        SharedPreferences settings = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                                                        String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");

                                                        if (macImpresora.equals("-")) {

                                                            if (lenguajeElegido == null) {

                                                            } else if (lenguajeElegido != null) {
                                                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                                                    Alert.nutresaShow(context, "Information", "No Printer Set. Please first Configure the Printer!", "OK", null,

                                                                            new View.OnClickListener() {

                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    Alert.dialogo.cancel();
                                                                                }

                                                                            }, null);

                                                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                                    Alert.nutresaShow(context, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

                                                                            new View.OnClickListener() {

                                                                                @Override
                                                                                public void onClick(View view) {

                                                                                    Alert.dialogo.cancel();
                                                                                }

                                                                            }, null);
                                                                }
                                                            }

                                                        } else {

                                                            if (lenguajeElegido == null) {

                                                            } else if (lenguajeElegido != null) {
                                                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                                                    progressDialog = ProgressDialog.show(context, "",
                                                                            "Please Wait...\n\nProcessing Information!", true);
                                                                    progressDialog.show();

                                                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                                    progressDialog = ProgressDialog.show(context, "",
                                                                            "Por Favor Espere...\n\nProcesando Informacion!", true);
                                                                    progressDialog.show();

                                                                }
                                                            }

                                                            SharedPreferences set = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                                                            String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

                                                            if (!tipoImpresora.equals("Intermec")) {
                                                                ImprimirTirilla(macImpresora);

                                                            } else {
                                                                ImprimirTirilla(macImpresora);
                                                            }
                                                        }

                                                    }
                                                }, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        DataBaseBO.borrarInfoTemp(context);
                                                        estadoEnviadoRespuesta = true;
                                                        Alert.dialogo.cancel();
                                                        volverPantallaPrincipal();
                                                    }
                                                });
                                            } else {
                                                DataBaseBO.borrarInfoTemp(context);
                                                estadoEnviadoRespuesta = true;
                                                volverPantallaPrincipal();
                                            }
                                        }
                                    }, null);


                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Alert.alertGeneral(context, null, "Se registro correctamente la información", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Alert.dialogo.cancel();

                                            String empresa = DataBaseBO.cargarEmpresa(context);

                                            if (empresa.equals("ADHB")) {
                                                //DIALOGO IMPRIMIR
                                                Alert.alertGeneral(context, null, "¿ Desea Imprimir el recibo ?", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        v.setEnabled(false);

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                v.setEnabled(true);
                                                            }
                                                        }, 600);


                                                        SharedPreferences settings = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                                                        String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");

                                                        if (macImpresora.equals("-")) {

                                                            if (lenguajeElegido == null) {

                                                            } else if (lenguajeElegido != null) {
                                                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                                                    Alert.nutresaShow(context, "Information", "No Printer Set. Please first Configure the Printer!", "OK", null,

                                                                            new View.OnClickListener() {

                                                                                @Override
                                                                                public void onClick(View view) {

                                                                                    Alert.dialogo.cancel();
                                                                                }

                                                                            }, null);

                                                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                                    Alert.nutresaShow(context, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

                                                                            new View.OnClickListener() {

                                                                                @Override
                                                                                public void onClick(View view) {

                                                                                    Alert.dialogo.cancel();
                                                                                }

                                                                            }, null);

                                                                }
                                                            }


                                                        } else {

                                                            if (lenguajeElegido == null) {

                                                            } else if (lenguajeElegido != null) {
                                                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                                                    progressDialog = ProgressDialog.show(context, "",
                                                                            "Please Wait...\n\nProcessing Information!", true);
                                                                    progressDialog.show();

                                                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                                    progressDialog = ProgressDialog.show(context, "",
                                                                            "Por Favor Espere...\n\nProcesando Informacion!", true);
                                                                    progressDialog.show();

                                                                }
                                                            }


                                                            SharedPreferences set = getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                                                            String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

                                                            if (!tipoImpresora.equals("Intermec")) {
                                                                ImprimirTirilla(macImpresora);

                                                            } else {
                                                                ImprimirTirilla(macImpresora);
                                                            }
                                                        }


                                                    }
                                                }, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        DataBaseBO.borrarInfoTemp(context);
                                                        estadoEnviadoRespuesta = true;
                                                        Alert.dialogo.cancel();
                                                        volverPantallaPrincipal();
                                                    }
                                                });
                                            } else {
                                                DataBaseBO.borrarInfoTemp(context);
                                                estadoEnviadoRespuesta = true;
                                                volverPantallaPrincipal();
                                            }
                                        }
                                    }, null);


                                }
                            }
                        }
                    }
                });

            } else {
                context.runOnUiThread(new Runnable() {

                    public void run() {

                        if(dialogResumenFirmaActivity != null)
                            dialogResumenFirmaActivity.saveButton.setEnabled(true);

                        String mensaje = "";
                        if (lenguajeElegido.lenguaje.equals("USA"))
                            mensaje = "Error downloading the database, please restart the day.";
                        else
                            mensaje = "Error descargando la base de datos, inicie día nuevamente";

                        ProgressView.getInstance().Dismiss();
                        Alert.alertGeneral(context, null, mensaje, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Alert.dialogo.cancel();
                                DataBaseBO.borrarInfoTemp(context);
                                volverPantallaLogin();

                            }
                        }, null);
                    }
                });
            }

        } catch (Exception e) {
            String mensaje = e.getMessage();
            Log.e(TAG, "respuestaLogin -> " + mensaje, e);
        }
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(4);
        }
    };


    private void enviarInfo(boolean ok, String respuestaServer, String msg) {

        if(progressDoalog != null)
            progressDoalog.dismiss();

        context.runOnUiThread(new Runnable() {
            public void run() {

                if (progressDialog != null)
                    progressDialog.dismiss();

                if (respuestaServer.equals("listo")) {


                    respuesta = respuestaServer;

                    if (respuestaServer.equals("listo") || respuestaServer.equals("ok")) {

                        PreferencesCartera.vaciarPreferencesCarteraSeleccionada(context);

                        progressDoalog = new ProgressDialog(context);
                        progressDialog.setCancelable(false);
                        progressDoalog.setMax(100);

                        if (formaPago != null) {
                            DataBaseBO.eliminarFacturaCartera(documentosFinanciero, context);
                        }

                        sincronizar2();

                    } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {
                        estadoEnviadoRespuesta = false;
                        if(dialogResumenFirmaActivity != null)
                            dialogResumenFirmaActivity.saveButton.setEnabled(true);
                    }

                } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {

                    if(dialogResumenFirmaActivity != null)
                        dialogResumenFirmaActivity.saveButton.setEnabled(true);

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.alertGeneral(context, null, "Could not Register Information", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    estadoEnviadoRespuesta = false;
                                    Alert.dialogo.cancel();


                                }
                            }, null);

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.alertGeneral(context, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    estadoEnviadoRespuesta = false;
                                    Alert.dialogo.cancel();


                                }
                            }, null);

                        }
                    }


                } else {

                    if(dialogResumenFirmaActivity != null)
                        dialogResumenFirmaActivity.saveButton.setEnabled(true);

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.alertGeneral(context, null, "Could not Register Information", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    estadoEnviadoRespuesta = false;
                                    Alert.dialogo.cancel();


                                }
                            }, null);

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.alertGeneral(context, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    estadoEnviadoRespuesta = false;
                                    Alert.dialogo.cancel();


                                }
                            }, null);

                        }
                    }
                }
            }
        });


    }

    private void sincronizar2() {

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(context);
        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

        Sync sync1 = new Sync(MetodosDePagoActivity.this, Constantes.DESCARGARINFO, context);

        sync1.user = usuarioApp.codigo;
        sync1.password = usuarioApp.contrasena;
        sync1.imei = Utilidades.obtenerImei(context);
        sync1.start();
        envioInformacion = true;

        progressDoalog = new ProgressDialog(this);

        if (lenguajeElegido.lenguaje.equals("USA")) {
            progressDoalog.setMessage(getResources().getString(R.string.descargando_informacion_eng));
        } else {
            progressDoalog.setMessage(getResources().getString(R.string.descargando_informacion_esp));
        }

        progressDoalog.setCancelable(false);
        progressDoalog.show();

    }


    @Override
    public void run() {

    }

    public void volverPantallaPrincipal() {
        if (esPreventa) {
            //BORRAR LA INSTANCIA DE QUE ES PREVENTA PARA NAVEGAR
            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = settings.edit();
            editor1.putBoolean("esPreventa", false);
            editor1.putBoolean("estado_FinalPrincipal", true);
            PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
            PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());
            PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
            editor1.commit();

            finishAffinity();

        } else {
            guardarVista();
            Intent vistaInforme = new Intent(context, PrincipalActivity.class);
            startActivity(vistaInforme);
        }
    }

    public void volverPantallaLogin() {
        PreferencesUsuario.vaciarPreferencesUsuario(context);

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        settings.edit().clear().apply();
        getSharedPreferences("session", 0).edit().clear().apply();
        getSharedPreferences("session", 0).edit().clear().apply();
        Alert.dialogo.cancel();

        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    /******
     * Método que se encarga de validar la empresa para mostrar el dialogo de firma
     *o continuar con el proceso de envío de información
     * * @param idPago
     */
    private void mostrarDialogFragmentFirma(String idPago) {
        String empresa = DataBaseBO.cargarEmpresa(context);

        if (empresa.equals("AGUC")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogFirmaFragment signatureDialogFragment = new DialogFirmaFragment(idPago, empresa, lenguajeElegido.lenguaje == null ? "ESP" : lenguajeElegido.lenguaje);
            signatureDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenTheme);
            signatureDialogFragment.show(fragmentManager, "signature_dialog");
        }
        else {
            continuarMetodoDePago();
        }
    }

    /****
     * Metodo encargado de cargar el resumen de la firma
     * @param idPago
     * @param signatureBitmap
     * @param empresa
     * @param vendedor
     */
    private void mostrarDialogResumenFragmentFirma(String idPago, Bitmap signatureBitmap, String empresa, String vendedor) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        dialogResumenFirmaActivity = new DialogResumenFirmaFragment(idPago,signatureBitmap, empresa, vendedor, lenguajeElegido.lenguaje == null ? "ESP" : lenguajeElegido.lenguaje);
        dialogResumenFirmaActivity.setStyle(DialogFragment.STYLE_NORMAL, R.style.Base_Theme_AppCompat_Dialog_MinWidth);
        dialogResumenFirmaActivity.setCancelable(false);
        dialogResumenFirmaActivity.show(fragmentManager, "signature_dialog");
    }

    /********
     * Metodo que se ejecuta al cerrar el dialogo de la firma y redirige al dialogo
     * de resumen de la firma
     */
    @Override
    public void onDialogDismissed(Bitmap signatureBitmap, String idPago, String empresa) {
        String vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
        mostrarDialogResumenFragmentFirma(idPago, signatureBitmap, empresa, vendedor);
    }

    /********
     * Metodo que se ejecuta al aceptar el resumen de la firma para continuar
     * con el envío de información
     */
    @Override
    public void onSuccesSignatureDialogListener() {
        continuarMetodoDePago();
    }

    /********
     * Metodo que se encarga de validar el método de pago para continuar con el envío
     * de informacion
     */
    private void continuarMetodoDePago() {

        DataBaseBO.verificarPagoCompletoPorTransferencia(nroRecibo, context);

        String empresa = DataBaseBO.cargarCodigo(context);
        obtenerCoordenadas(empresa);

        switch (METODO_PAGO) {

            case Constantes.PAGO_ANTICIPO_USA: {
                dialogTerminarRecaudoAnticipoUSA();
                break;
            }

            case Constantes.PAGO_ANTICIPO_ESP: {
                dialogTerminarRecaudoAnticipoESP();
                break;
            }

            case Constantes.PAGO_RECIBO_POR_LEGALIZAR_USA: {
                dialogTerminarRecaudoReciboPorLegalizarUSA();
                break;
            }

            case Constantes.PAGO_RECIBO_POR_LEGALIZAR_ESP: {
                dialogTerminarRecaudoReciboPorLegalizarESP();
                break;
            }

            case Constantes.PAGO_PARCIAL_USA: {
                dialogTerminarRecaudoPagoParcialUSA();
                break;
            }

            case Constantes.PAGO_PARCIAL_ESP: {
                dialogTerminarRecaudoPagoParcialESP();
                break;
            }

            case Constantes.PAGO_TOTAL_PARCIAL_USA: {
                dialogTerminarRecaudoPagoTotalParcialUSA();
                break;
            }

            case Constantes.PAGO_TOTAL_PARCIAL_ESP: {
                dialogTerminarRecaudoPagoTotalParcialESP();
                break;
            }

            case Constantes.PAGO_TOTAL_USA: {
                dialogTerminarRecaudoPagoTotalUSA();
                break;
            }

            case Constantes.PAGO_TOTAL_ESP: {
                dialogTerminarRecaudoPagoTotalESP();
                break;
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void obtenerCoordenadas(String empresa) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitud = location.getLatitude();
                            double longitud = location.getLongitude();
                            Log.d("Ubicacion", "Latitud: " + latitud + ", Longitud: " + longitud);
                            DataBaseBO.guardarCoordenadas(latitud, longitud, clienteSel.codigo, nroRecibo, context);
                        }

                        enviarInformacion(empresa);
                    }
            );
        }
    }

    public void enviarInformacion(String empresa)
    {

        progressDoalog = new ProgressDialog(this);

        if (lenguajeElegido.lenguaje.equals("USA")) {
            progressDoalog.setMessage(getResources().getString(R.string.enviando_informacion_eng));
        } else {
            progressDoalog.setMessage(getResources().getString(R.string.enviando_informacion_esp));
        }

        progressDoalog.setCancelable(false);
        progressDoalog.show();

        Sync sync = new Sync(MetodosDePagoActivity.this, Constantes.ENVIARINFORMACION, context);
        sync.user = empresa;
        sync.start();
        Alert.dialogo.cancel();
    }

}
