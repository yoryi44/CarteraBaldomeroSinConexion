package co.com.celuweb.carterabaldomero;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import Adapters.AdaptersRecibosPendientes;
import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.Anticipo;
import dataobject.ClienteSincronizado;
import dataobject.FormaPago;
import dataobject.Lenguaje;
import dataobject.ReciboDinero;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import sharedpreferences.PreferencesReciboDinero;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.Utilidades;

public class ReciboDineroActivity extends AppCompatActivity implements Synchronizer {

    private ClienteSincronizado clienteSel;
    private Lenguaje lenguajeElegido;
    ProgressDialog progressDoalog;

    private Usuario usuarioApp;

    private boolean envioInformacion = false;
    Context context = ReciboDineroActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recibo_dinero);

        enviarInformacion();

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

    }

    @Override
    protected void onResume() {

        super.onResume();
        configurarVista();
    }

    /**
     * METODO MENU
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {


            item.setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    item.setEnabled(true);
                }
            }, 600);


            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {

            case R.id.menu_forward:

                progressDoalog = new ProgressDialog(context);

                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);

                onClickCarteraActivity();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * METODO PARA FUNCIONAMIENTO DEL MENU
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fw, menu);
        return true;
    }

    /**
     * FINALIZA EL ACTIVITY
     */
    @Override
    public void onBackPressed() {
        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_ReciboDinero", true);
        editor1.remove("estado_ReciboDinero");
        editor1.putBoolean("estado_AnticipoRecibo", true);
        editor1.remove("estado_AnticipoRecibo");
        editor1.putBoolean("estado_Cartera", true);
        editor1.remove("estado_Cartera");

        editor1.commit();
        finish();
        Intent carteraActivity = new Intent(this, VistaClienteActivity.class);
        startActivity(carteraActivity);
    }

    /**
     * CONFIGURA LA VISTA LA INICIALIZA
     */
    private void configurarVista() {

        TextView txtCompaReciboDinero = findViewById(R.id.txtCompaReciboDinero);
        TextView tvNombreUsuario = findViewById(R.id.txtUsuarioReciboDinero);
        TextView lblConsecutivoReciboDinero = findViewById(R.id.txtConsecutivoReciboDinero);
        TextView lblFechaReciboDinero = findViewById(R.id.txtFechaReciboDinero);
        TextView lblClienteReciboDinero = findViewById(R.id.txtClienteReciboDinero);
        TextView lblRazonSocialReciboDinero = findViewById(R.id.txtRazonSocialReciboDinero);

        TextView titulolblFechaReciboDinero = findViewById(R.id.titulolblFechaReciboDinero);
        TextView titulolblConsecutivoReciboDinero = findViewById(R.id.titulolblConsecutivoReciboDinero);
        TextView lblUsuarioReciboDinero = findViewById(R.id.lblUsuarioReciboDinero);
        TextView titulolblClienteReciboDinero = findViewById(R.id.titulolblClienteReciboDinero);
        TextView titulolblRazonSocialReciboDinero = findViewById(R.id.titulolblRazonSocialReciboDinero);

        TextView tituloformasPago = findViewById(R.id.tituloformasPago);
        final RadioButton rbAnticipo = findViewById(R.id.rbAnticipo);
        final RadioButton rbReciboLegalizar = findViewById(R.id.rbReciboLegalizar);
        final RadioButton rbReciboPago = findViewById(R.id.rbReciboPago);

        String empresa23 = "";
        empresa23 = DataBaseBO.cargarEmpresa(context);
        final String finalEmpresa23 = empresa23;

        if (empresa23.equals("AGUC")) {
            rbAnticipo.setVisibility(View.GONE);
        }

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Payment method"));


                titulolblFechaReciboDinero.setText("Date:");
                titulolblConsecutivoReciboDinero.setText("Consecutive:");
                lblUsuarioReciboDinero.setText("User:");
                titulolblClienteReciboDinero.setText("Customer code");
                titulolblRazonSocialReciboDinero.setText("Customer Name");

                tituloformasPago.setText("Payment method");

                rbAnticipo.setText("Advance");
                rbReciboLegalizar.setText("Receipt to legalize");
                rbReciboPago.setText("Regular Payment");


            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Formas de Pago"));

            }
        }

        //CARGAR DATOS CLIENTE VISTA
        Gson gson = new Gson();
        String stringJsonObject = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson.fromJson(stringJsonObject, ClienteSincronizado.class);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        String empresa = "";
        String nombreUsuario = "";
        String consecutivo = "";
        String consecutivoNegocio = "";
        String consecutivoVendedor = "";
        String empresaAguc = "";

        empresa = DataBaseBO.cargarRazonSocial(context);
        empresaAguc = DataBaseBO.cargarEmpresa(context);
        txtCompaReciboDinero.setText(empresa);
        nombreUsuario = DataBaseBO.cargarUsuarioApp(context);
        tvNombreUsuario.setText(nombreUsuario);
        consecutivo = DataBaseBO.cargarConsecutivo(context);
        consecutivoNegocio = DataBaseBO.cargarNegocioConsecutivo(context);
        consecutivoVendedor = DataBaseBO.cargarVendedorConsecutivo(context);

        String strDate = sdf.format(c.getTime());


        int contador = 1;
        int consec1 = Integer.parseInt(consecutivo);
        int vendedorsum = Integer.parseInt(consecutivoVendedor);
        double valorfinal = 0;

        consec1 = consec1 + contador;
        String consecutivofinal = (consecutivoNegocio + vendedorsum + consec1);


        lblConsecutivoReciboDinero.setText(consecutivoNegocio + vendedorsum + consec1);
        lblFechaReciboDinero.setText(strDate);
        lblRazonSocialReciboDinero.setText(clienteSel.nombre);
        lblClienteReciboDinero.setText(clienteSel.codigo);

        Gson gson1 = new Gson();
        clienteSel.consecutivo = consecutivofinal;
        clienteSel.consecutivoInicial = consec1;
        String jsonStringObject1 = gson1.toJson(clienteSel);
        PreferencesClienteSeleccionado.guardarClienteSeleccionado(this, jsonStringObject1);

    }

    /**
     * ONCLICK PARA SEGUIR A LA SGTE ACTIVITY,OBTIENE EL CLIENTE SELECCIONADO Y LLAMA AL GUARDAR VISTA
     */
    public void onClickCarteraActivity() {
        Gson gson = new Gson();
        String stringJsonObject = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson.fromJson(stringJsonObject, ClienteSincronizado.class);


        final RadioButton rbAnticipo = findViewById(R.id.rbAnticipo);
        final RadioButton rbReciboLegalizar = findViewById(R.id.rbReciboLegalizar);
        final RadioButton rbReciboPago = findViewById(R.id.rbReciboPago);

        if (!rbAnticipo.isChecked() && !rbReciboLegalizar.isChecked() && !rbReciboPago.isChecked()) {

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Toasty.warning(this, "Select payment type..", Toasty.LENGTH_SHORT).show();


                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Toasty.warning(this, "Seleccione el tipo de pago..", Toasty.LENGTH_SHORT).show();

                }
            }


        } else if (rbAnticipo.isChecked()) {

            Anticipo anticipo = new Anticipo();
            anticipo.estado = true;

            Gson gson33 = new Gson();
            String jsonStringObject = gson33.toJson(anticipo);
            PreferencesAnticipo.guardarAnticipoSeleccionada(this, jsonStringObject);

            guardarVistaAnticipoRecibo();
            Intent carteraActivity = new Intent(this, MetodosDePagoActivity.class);
            startActivity(carteraActivity);

        } else if (rbReciboLegalizar.isChecked()) {
            Anticipo anticipo = new Anticipo();
            anticipo.estado = false;

            Gson gson33 = new Gson();
            String jsonStringObject = gson33.toJson(anticipo);
            PreferencesAnticipo.guardarAnticipoSeleccionada(this, jsonStringObject);

            guardarVistaAnticipoRecibo();
            Intent carteraActivity = new Intent(this, MetodosDePagoActivity.class);
            startActivity(carteraActivity);

        } else if (rbReciboPago.isChecked()) {
            guardarVista();
            Intent carteraActivity = new Intent(this, CarteraActivity.class);
            startActivity(carteraActivity);
        }


    }

    /**
     * SHARED PREFERENCE VISTA
     */
    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_Cartera", estado);
        editor1.commit();
    }


    private void guardarVistaAnticipoRecibo() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_AnticipoRecibo", estado);
        editor1.commit();
    }

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
            progressDoalog.cancel();

        onClickCarteraActivity();

    }

    private void enviarInformacion() {

        progressDoalog = ProgressDialog.show(context, "", "Organizando Informacion...", true);
        progressDoalog.show();

        final String empresa;
        empresa = DataBaseBO.cargarCodigo(context);
        Sync sync = new Sync(ReciboDineroActivity.this::respSync, Constantes.ENVIARINFORMACION, context);
        sync.user = empresa;
        sync.start();

    }

    private void enviarInfo(boolean ok, String respuestaServer, String msg) {

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(context);
        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);

        (ReciboDineroActivity.this).runOnUiThread(new Runnable() {
            public void run() {

                if (respuestaServer.equals("listo")) {

                    if (respuestaServer.equals("listo") || respuestaServer.equals("ok")) {

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Sync sync1 = new Sync(ReciboDineroActivity.this::respSync, Constantes.DESCARGARINFO, context);
                                sync1.user = usuarioApp.codigo;
                                sync1.password = usuarioApp.contrasena;
                                sync1.imei = Utilidades.obtenerImei(context);
                                sync1.start();
                                envioInformacion = true;

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Sync sync1 = new Sync(ReciboDineroActivity.this::respSync, Constantes.DESCARGARINFO, context);
                                sync1.user = usuarioApp.codigo;
                                sync1.password = usuarioApp.contrasena;
                                sync1.imei = Utilidades.obtenerImei(context);
                                sync1.start();
                                envioInformacion = true;

                            }
                        }


                    } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {
                        if(progressDoalog != null)
                            progressDoalog.cancel();
                    }


                } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {

                    if(progressDoalog != null)
                        progressDoalog.cancel();

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
//                        if (lenguajeElegido.lenguaje.equals("USA")) {
//
//                            Alert.alertGeneral(context, null, "Could not Register Information", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//
//                                    Alert.dialogo.cancel();
//
//
//                                }
//                            }, null);
//
//                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {
//
//                            Alert.alertGeneral(context, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//
//                                    Alert.dialogo.cancel();
//
//
//                                }
//                            }, null);
//
//                        }
                    }


                }
            }
        });


    }

}
