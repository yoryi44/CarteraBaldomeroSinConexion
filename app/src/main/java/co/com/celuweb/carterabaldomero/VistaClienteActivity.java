package co.com.celuweb.carterabaldomero;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.ClienteSincronizado;
import dataobject.Lenguaje;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class VistaClienteActivity extends AppCompatActivity implements Synchronizer {

    private ClienteSincronizado clienteSel;
    private Lenguaje lenguajeElegido;

    NumberFormat formatoNumero;

    Boolean esPreventa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_cliente);

        //VACIAR PREFERENCES
        PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(this);
        PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarVista();
    }


    /**
     * METODO DE INICIALIZACION DE VARIABLES
     */
    private void configurarVista() {

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        esPreventa = settings.getBoolean("esPreventa", false);

        String finalEmpresa = DataBaseBO.cargarEmpresa(VistaClienteActivity.this);

        if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

            formatoNumero = NumberFormat.getInstance(new Locale("es"));
        }
        else
        {
            formatoNumero = NumberFormat.getInstance(new Locale("en"));
        }

        bannersUsiarios();

        //CARGAR DATOS CLIENTE VISTA
        Gson gson = new Gson();
        String stringJsonObject = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson.fromJson(stringJsonObject, ClienteSincronizado.class);

        TextView tvCodigoSapClienteSel = findViewById(R.id.tvCodigoSapClienteSel);
        TextView tvNombreClienteSel = findViewById(R.id.tvNombreClienteSel);
        TextView tvRazonSocialClienteSel = findViewById(R.id.tvRazonSocialClienteSel);
        TextView tvNITClienteSel = findViewById(R.id.tvNITClienteSel);
        TextView tvDireccionClienteSel = findViewById(R.id.tvDireccionClienteSel);
        TextView tvTelefonoClienteSel = findViewById(R.id.tvTelefonoClienteSel);
        TextView tvSegmentoClienteSel = findViewById(R.id.tvSegmentoClienteSel);
        TextView tvCarteraVencidaClienteSelPorcentaje = findViewById(R.id.tvCarteraVencidaClienteSelPorcentaje);
        TextView tvCarteraVencidaClienteSel = findViewById(R.id.tvCarteraVencidaClienteSel);

        TextView titulosap = findViewById(R.id.tvTituloCodigoSapClienteSel);
        TextView titulonombre = findViewById(R.id.tvTituloNombreClienteSel);
        TextView titulorazonsocial = findViewById(R.id.tvTituloRazonSocialClienteSel);
        TextView titulonit = findViewById(R.id.tvTituloNitClienteSel);
        TextView tituloemail = findViewById(R.id.tvTituloDireccionClienteSel);
        TextView titulocontacto = findViewById(R.id.tvTituloTelefonoClienteSel);
        TextView titulovendedor = findViewById(R.id.tvTituloSegmentoClienteSel);

        TextView titulocupocredito = findViewById(R.id.tvTituloCupoCreditoClienteSel);
        TextView titulocondicionpago = findViewById(R.id.tvTituloCondicionPagoClienteSel);
        TextView titulocarteravencida = findViewById(R.id.tvTituloCarteraVencidaClienteSel);
        TextView tituloporcentajecartera = findViewById(R.id.tvTituloCarteraVencidaClienteSelPor);

        TextView titulobotonReciboDinero = findViewById(R.id.titulobotonReciboDinero);
        TextView titulobotonSolicitudNotaCredito = findViewById(R.id.titulobotonSolicitudNotaCredito);
        TextView titulobotonSolicitudCredito = findViewById(R.id.titulobotonSolicitudCredito);
        TextView titulobotonBloqueoClientes = findViewById(R.id.titulobotonBloqueoClientes);
        TextView titulobotonRecibosAnulados = findViewById(R.id.titulobotonRecibosAnulados);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(VistaClienteActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        String tipoUsuario="";
        tipoUsuario = DataBaseBO.cargarTipoUsuarioApp(VistaClienteActivity.this);

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Account management"));

                titulosap.setText("Customer Code");
                titulonombre.setText("Customer Name");
                titulorazonsocial.setText("Social Reason");
                titulonit.setText("Tax Id");
                tituloemail.setText("Email");
                titulocontacto.setText("Phone");

                if (tipoUsuario.equals("10")) {
                    titulovendedor.setText("Collector");
                }else if (tipoUsuario.equals("4")) {
                    titulovendedor.setText("Seller");
                }


                titulocupocredito.setText("Credit Limit");
                titulocondicionpago.setText("Payment Term");
                titulocarteravencida.setText("Due Balance");
                tituloporcentajecartera.setText("% Due Balance");

                titulobotonReciboDinero.setText("Receivables");
                titulobotonSolicitudNotaCredito.setText("Credit note request");
                titulobotonSolicitudCredito.setText("Credit Application");
                titulobotonBloqueoClientes.setText("Block");
                titulobotonRecibosAnulados.setText("Cancelled Receipts");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Gestión de la visita"));

            }
        }


        tvCodigoSapClienteSel.setText(clienteSel.codigo);
        tvNombreClienteSel.setText(clienteSel.nombre);
        tvRazonSocialClienteSel.setText(clienteSel.razonSocial);
        tvNITClienteSel.setText(clienteSel.nit);
        tvDireccionClienteSel.setText(clienteSel.email);
        tvTelefonoClienteSel.setText(clienteSel.telefono);
        tvSegmentoClienteSel.setText(clienteSel.Vendedor1);
        tvCarteraVencidaClienteSel.setText("$" + formatoNumero.format(Utilidades.formatearDecimales(clienteSel.carteraVencida, 2)));
        tvCarteraVencidaClienteSelPorcentaje.setText(clienteSel.porcentajeCarteraVenciada + "%");

        TextView tvCupoCreditoClienteSel = findViewById(R.id.tvCupoCreditoClienteSel);
        TextView tvCondicionPagoClienteSel = findViewById(R.id.tvCondicionPagoClienteSel);

        String cupoCredito = Utilidades.separarMilesSinDecimal(String.valueOf(clienteSel.cupo), VistaClienteActivity.this);
        tvCupoCreditoClienteSel.setText(cupoCredito);
        tvCondicionPagoClienteSel.setText(clienteSel.condicionPago);
    }

    /**
     * METODO PARA CONFIGURAR LOS BANNERS
     */
    private void bannersUsiarios() {

        String empresa = "";

        empresa = DataBaseBO.cargarEmpresa(VistaClienteActivity.this);

        if (empresa.equals("AGCO")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.agco);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.agco);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A6194525")));

        } else if (empresa.equals("AGSC")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);

            li.setBackgroundResource(R.color.agsc);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.agsc);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AABR")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);

            li.setBackgroundResource(R.color.aabr);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.aabr);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("ADHB")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);

            li.setBackgroundResource(R.color.adhb);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.adhb);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AFPN")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.afpn);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.afpn);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AFPP")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.afpp);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.afpp);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AFPZ")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.afpz);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.afpz);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AGAH")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.agah);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.agah);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        } else if (empresa.equals("AGDP")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.agdp);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.agdp);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        } else if (empresa.equals("AGGC")) {

            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.aggc);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.aggc);
            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AGUC")) {

            (findViewById(R.id.tvRazonSocialClienteSel)).setVisibility(View.GONE);
            (findViewById(R.id.tvTituloRazonSocialClienteSel)).setVisibility(View.GONE);
            LinearLayout li = (LinearLayout) findViewById(R.id.vistacliente);
            li.setBackgroundResource(R.color.aguc);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.expandibleVistaC2);
            li2.setBackgroundResource(R.color.aguc);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        }


    }

    /**
     * METODO PARA REGRESAR
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * FINALIZA EL ACTIVITY
     */
    @Override
    public void onBackPressed() {

        if(esPreventa)
        {
            //BORRAR LA INSTANCIA DE QUE ES PREVENTA PARA NAVEGAR
            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = settings.edit();
            editor1.putBoolean("esPreventa", false);
            editor1.putBoolean("estado_FinalPrincipal", true);
            editor1.commit();

            finishAffinity();
        }
        else
        {
            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = settings.edit();
            editor1.putBoolean("estado_VistaCliente", true);
            editor1.remove("estado_VistaCliente");
            editor1.commit();
            finish();
            Intent vistaClienteActivity = new Intent(this, RutaActivity.class);
            startActivity(vistaClienteActivity);
        }
    }

    /**
     * METODO ENCARGADO DEL ONCLICK DE RECIBO DE DINERO
     *
     * @param view
     */
    public void onClickReciboDinero(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        Gson gson = new Gson();
        String stringJsonObject = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson.fromJson(stringJsonObject, ClienteSincronizado.class);

        guardarVista();

        Intent vistaClienteActivity = new Intent(this, ReciboDineroActivity.class);
        startActivity(vistaClienteActivity);
    }

    public void onClickAnuladosVistaCliente(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        Intent vistaClienteActivity = new Intent(this, AnuladosActivity.class);
        startActivity(vistaClienteActivity);

    }

    /**
     * METODO PARA GUARDAR EL ESTADO DE LA VISTA
     */
    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_ReciboDinero", estado);
        editor1.commit();
    }

    /**
     * Metodo para bloquear el cliente, el cual apunta a una url del servicio
     *
     * @param view vista a la cual se le agrega el evento
     */
    public void onClickBloquearCliente(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL
        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(this);
        Usuario usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);

        //Progress.show(VistaClienteActivity.this,"Procesando", "Bloqueando cliente...",false);
        ProgressView.getInstance().Show(VistaClienteActivity.this, "Bloqueando Cliente...");
        Sync sync = new Sync(VistaClienteActivity.this, Constantes.BLOQUEARCLIENTE, VistaClienteActivity.this);
        sync.user = usuarioApp.codigo;
        sync.codigoCliente = clienteSel.codigo;
        sync.start();
    }

    /**
     * RESPUESTA DE SERVIDOR
     *
     * @param ok
     * @param respuestaServer
     * @param msg
     * @param codeRequest
     */
    @Override
    public void respSync(final boolean ok, final String respuestaServer, final String msg, final int codeRequest) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    //Progress.hide();
                    ProgressView.getInstance().Dismiss();
                    if (ok && codeRequest == Constantes.BLOQUEARCLIENTE) {

                        Toasty.info(getApplicationContext(), "Petición de  bloquear/Desbloquear cliente ha sido enviada.").show();

                    } else {
                        Alert.alertGeneral(VistaClienteActivity.this, "Error", "Ocurrio un error inesperado " + respuestaServer,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Alert.dialogo.cancel();
                                    }
                                }, null);
                    }

                } catch (Exception e) {
                    Log.e("VISTACLIENTEEACTIVITY:", "" + e.getMessage());
                }

            }
        });

    }

}
