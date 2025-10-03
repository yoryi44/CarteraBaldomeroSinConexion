package co.com.celuweb.carterabaldomero;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.Anticipo;
import dataobject.Lenguaje;
import dataobject.Usuario;
import dataobject.Version;
import es.dmoral.toasty.Toasty;
import servicio.MyWorker;
import servicio.Sync;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesCarteraFactura;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesParcial;
import sharedpreferences.PreferencesPendientesFacturas;
import sharedpreferences.PreferencesReciboDinero;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class PrincipalActivity extends AppCompatActivity implements Synchronizer {


    private Usuario usuarioApp;
    private Anticipo anticipo;
    private boolean envioInformacion = false;
    ProgressDialog progressDoalog;
    private TextView InformesPrincipal;
    private TextView GestionCartera;
    private Lenguaje lenguajeElegido;
    private TextView cerrar;
    private static final String WORK_TAG = "my_work_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        actualizarVersionApp();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PrincipalActivity.cargarUsuariosAsynTask asyncTask = new PrincipalActivity.cargarUsuariosAsynTask();
                asyncTask.execute();

            }
        }, 1000);

        Log.i("WORKER", "Enviando información...");
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInitialDelay(15, TimeUnit.SECONDS)
                .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL, // Política de retroceso
                        10, // Tiempo inicial de retraso
                        TimeUnit.SECONDS // Unidad de tiempo
                )
                .addTag(WORK_TAG) // Asignar un tag al trabajo
                .build();

        // Encolar la solicitud de trabajo
        WorkManager.getInstance(this).enqueue(workRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            actualizarVersionApp();
            configurarVista();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        try {

            switch (codeRequest) {

                case Constantes.ENVIARINFORMACION:
//                    enviarInfo(ok, respuestaServer, msg);

                    System.out.println("respuesta server " + respuestaServer);

                    break;

                case Constantes.DESCARGARINFO:
                    descargarInfo(ok, respuestaServer, msg);
                    break;

                case Constantes.DOWNLOAD_VERSION_APP:
                    RespuestaDownloadVersionApp(ok, respuestaServer, msg);
                    break;

                case Constantes.CERRAR_SESION:
                    respuestaCerrarSesion(ok, respuestaServer, msg);
                    break;

            }

        } catch (Exception exception) {
            System.out.println("Error en el repSync en el modulo Metodos de pago" + exception);
        }

    }

    private void descargarInfo(boolean ok, String respuestaServer, String msg) {

        if(progressDoalog != null)
            progressDoalog.cancel();

        if(ok)
        {
            if (lenguajeElegido == null) {
            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Alert.alertGeneral(PrincipalActivity.this, null, "The information is correctly recorded", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    guardarVista();
                                    Intent vistaInforme = new Intent(PrincipalActivity.this, PrincipalActivity.class);
                                    startActivity(vistaInforme);


                                    Alert.dialogo.cancel();


                                }
                            }, null);
                        }
                    });

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Alert.alertGeneral(PrincipalActivity.this, null, "Se sincronizo correctamente", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    guardarVista();
                                    Intent vistaInforme = new Intent(PrincipalActivity.this, PrincipalActivity.class);
                                    startActivity(vistaInforme);


                                    Alert.dialogo.cancel();


                                }
                            }, null);
                        }
                    });
                }
            }
        }
        else
        {
            if (lenguajeElegido == null) {
            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Alert.alertGeneral(PrincipalActivity.this, null, "Process error", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            guardarVista();
                            Intent vistaInforme = new Intent(PrincipalActivity.this, PrincipalActivity.class);
                            startActivity(vistaInforme);


                            Alert.dialogo.cancel();


                        }
                    }, null);


                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Alert.alertGeneral(PrincipalActivity.this, null, "Error en el proceso", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            guardarVista();
                            Intent vistaInforme = new Intent(PrincipalActivity.this, PrincipalActivity.class);
                            startActivity(vistaInforme);


                            Alert.dialogo.cancel();


                        }
                    }, null);


                }
            }
        }

    }

    public void RespuestaDownloadVersionApp(final boolean ok, final String respuestaServer, String msg) {

        if (progressDoalog != null)
            progressDoalog.cancel();

        this.runOnUiThread(new Runnable() {

            public void run() {

                if (ok ) {

                    installApk();

//                    File fileApp = new File(Utilidades.dirApp(PrincipalActivity.this), Constantes.fileNameApk);
//
//                    if (fileApp.exists()) {
//
////                        Uri uri = Uri.fromFile(fileApp);
//                        Uri uri = FileProvider.getUriForFile(PrincipalActivity.this, BuildConfig.APPLICATION_ID + ".provider",fileApp);
//
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
//                        startActivityForResult(intent, Constantes.RESP_ACTUALIZAR_VERSION);
//
//                    } else {
//
//                        Utilidades.MostrarAlertDialog(PrincipalActivity.this, "No se pudo actualizar la version.");
//                    }

                } else {

                    Utilidades.MostrarAlertDialog(PrincipalActivity.this, respuestaServer);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class cargarUsuariosAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                configurarVista();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ProgressView.getInstance().Dismiss();
        }
    }


    @SuppressLint("ResourceType")
    public void configurarVista() throws InterruptedException {
        Thread.sleep(1000);
        ProgressView.getInstance().Dismiss();
        bannersUsuarios();

        InformesPrincipal = findViewById(R.id.InformesPrincipal);
        GestionCartera = findViewById(R.id.GestionCartera);
        cerrar = findViewById(R.id.itCerrarSesion);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(PrincipalActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        try {
            TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
            TextView tvEmpresa = findViewById(R.id.tvFechaUltimaSincronizacionUsuario);

            Gson gson = new Gson();
            String stringJsonObject = PreferencesUsuario.obtenerUsuario(this);
            usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);

            String nombreUsuario = "";
            String empresa = "";

            nombreUsuario = DataBaseBO.cargarUsuarioApp(PrincipalActivity.this);
            empresa = DataBaseBO.cargarCodigo(PrincipalActivity.this);

            tvNombreUsuario.setText(nombreUsuario);
            tvEmpresa.setText(empresa);
        } catch (Exception w) {
            System.out.println("Sin Data" + w);
        }
        TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        TextView tvEmpresa = findViewById(R.id.tvFechaUltimaSincronizacionUsuario);

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(this);
        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(this);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        String nombreUsuario = "", tipoUsuario = "";
        String empresa = "";

        nombreUsuario = DataBaseBO.cargarUsuarioApp(PrincipalActivity.this);
        empresa = DataBaseBO.cargarCodigo(PrincipalActivity.this);
        tipoUsuario = DataBaseBO.cargarTipoUsuarioApp(PrincipalActivity.this);

        tvNombreUsuario.setText(nombreUsuario);
        tvEmpresa.setText(empresa);

        TextView tvVersionApp = findViewById(R.id.tvVersionApp);
        String ambiente = (Constantes.APLICACION == Constantes.PRODUCCION) ? "Producción" : "Pruebas";

        String version = "Versión " + Utilidades.getVersion(PrincipalActivity.this) + " " + ambiente;

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {

            if (lenguajeElegido.lenguaje.equals("USA")) {
                GestionCartera.setText("Portfolio Management");
                InformesPrincipal.setText("Reports");
                // cerrar.setText("Close Session");
                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setTitle(Utilidades.tituloFormato(this, "Receivables Online"));


                if (tipoUsuario.equals("10")) {
                    tvVersionApp.setText("COLLECTOR");
                } else if (tipoUsuario.equals("4")) {
                    tvVersionApp.setText("SELLER");
                }

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                GestionCartera.setText("Gestion de Cartera");
                InformesPrincipal.setText("Informes");
                //  cerrar.setText("Cerrar Sesion");
                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setTitle(Utilidades.tituloFormato(this, "Cartera En Línea"));

                if (tipoUsuario.equals("10")) {
                    tvVersionApp.setText("COBRADOR");
                } else if (tipoUsuario.equals("4")) {
                    tvVersionApp.setText("VENDEDOR");
                }
            }
        }


        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

        LinearLayout llBotonInformes = findViewById(R.id.llBotonInformes);
        llBotonInformes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                view.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, 600);


                boolean hayClientesEnRutero = Utilidades.existeArchivoDataBase();
                if (hayClientesEnRutero) {

                    SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
                    boolean estado = true;
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putBoolean("estado_Informes", true);
                    editor1.commit();

                    eliminarEstados();


                    Intent vistaInforme = new Intent(PrincipalActivity.this, InformesActivity.class);
                    startActivity(vistaInforme);
                } else {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.alertGeneral(PrincipalActivity.this, "Warning", "No customer information",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (Alert.dialogo != null) {
                                                Alert.dialogo.cancel();
                                            }


                                        }
                                    }, null);


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.alertGeneral(PrincipalActivity.this, "Advertencia", "No hay información de clientes",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (Alert.dialogo != null) {
                                                Alert.dialogo.cancel();
                                            }


                                        }
                                    }, null);
                        }
                    }


                }

            }
        });

        LinearLayout llBotonGestionCartera = findViewById(R.id.llBotonGestionCartera);
        llBotonGestionCartera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                view.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, 600);


                if (anticipo == null) {
                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                    PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                    PreferencesCarteraFactura.vaciarPreferencesFacturaSeleccionada(getApplicationContext());
                    PreferencesClienteSeleccionado.vaciarPreferencesClienteSeleccionado(getApplicationContext());
                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                    PreferencesParcial.vaciarPreferencesParcialSeleccionada(getApplicationContext());
                    PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(getApplicationContext());
                    PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(getApplicationContext());
                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());

                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = settings.edit();
                    editor1.putBoolean("estado_Principal", true);
                    editor1.remove("estado_Principal");
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

                    editor1.putBoolean("estado_FacturasSeleccRealizadas", true);
                    editor1.remove("estado_FacturasSeleccRealizadas");

                    editor1.putBoolean("estado_Pendientes", true);
                    editor1.remove("estado_Pendientes");
                    editor1.putBoolean("estado_MetodosDePagoPendientes", true);
                    editor1.remove("estado_MetodosDePagoPendientes");
                    editor1.putBoolean("estado_FacturasSeleccMultiplesPendientes", true);
                    editor1.remove("estado_FacturasSeleccMultiplesPendientes");
                    editor1.commit();
                    finish();
                }

                if (anticipo != null) {
                    PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
                    PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                    PreferencesCarteraFactura.vaciarPreferencesFacturaSeleccionada(getApplicationContext());
                    PreferencesClienteSeleccionado.vaciarPreferencesClienteSeleccionado(getApplicationContext());
                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
                    PreferencesParcial.vaciarPreferencesParcialSeleccionada(getApplicationContext());
                    PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(getApplicationContext());
                    PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(getApplicationContext());
                    PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());

                    SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = settings.edit();
                    editor1.putBoolean("estado_Principal", true);
                    editor1.remove("estado_Principal");
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

                    editor1.putBoolean("estado_Pendientes", true);
                    editor1.remove("estado_Pendientes");
                    editor1.putBoolean("estado_MetodosDePagoPendientes", true);
                    editor1.remove("estado_MetodosDePagoPendientes");
                    editor1.putBoolean("estado_FacturasSeleccMultiplesPendientes", true);
                    editor1.remove("estado_FacturasSeleccMultiplesPendientes");
                    editor1.commit();
                    finish();
                }


                guardarVista();
                Intent vistaRuta = new Intent(PrincipalActivity.this, RutaActivity.class);
                startActivity(vistaRuta);
//
            }
        });


    }

    /**
     * METODO PARA VERIFICAR USUARIO Y CONFIGURAR BANNERS
     */
    @SuppressLint("ResourceType")
    private void bannersUsuarios() {

        String empresa = "";

        empresa = DataBaseBO.cargarEmpresa(PrincipalActivity.this);

        if (empresa.equals("AGCO")) {

            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.agco);
            LinearLayout li = (LinearLayout) findViewById(R.id.colorbanner);
            li.setBackgroundResource(R.color.agco);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.llBotonInformes);
            li2.setBackgroundResource(R.color.agco);
            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A6194525")));

        }

        if (empresa.equals("AGSC")) {

            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.agsc);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        }

        if (empresa.equals("AABR")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.aabr);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        }

        if (empresa.equals("ADHB")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.adhb);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        }

        if (empresa.equals("AFPN")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.afpn);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        }

        if (empresa.equals("AFPP")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.afpp);


            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        }

        if (empresa.equals("AFPZ")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.afpz);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        }

        if (empresa.equals("AGAH")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.agah);
            LinearLayout li = (LinearLayout) findViewById(R.id.colorbanner);
            li.setBackgroundResource(R.color.agah);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.llBotonInformes);
            li2.setBackgroundResource(R.color.agah);
            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        }

        if (empresa.equals("AGDP")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.agdp);
            LinearLayout li = (LinearLayout) findViewById(R.id.colorbanner);
            li.setBackgroundResource(R.color.agdp);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.llBotonInformes);
            li2.setBackgroundResource(R.color.agdp);
            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        }

        if (empresa.equals("AGGC")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.aggc);

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        }

        if (empresa.equals("AGUC")) {
            LinearLayout li1 = (LinearLayout) findViewById(R.id.imageusu);
            li1.setBackgroundResource(R.mipmap.aguc);
            LinearLayout li = (LinearLayout) findViewById(R.id.colorbanner);
            li.setBackgroundResource(R.color.aguc);
            LinearLayout li2 = (LinearLayout) findViewById(R.id.llBotonInformes);
            li2.setBackgroundResource(R.color.aguc);
            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        }


    }

    /**
     * ITEMS DEL MENU Y SU METODO
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.itCerrarSesion:

                item.setEnabled(false);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);


                cerrarSesion();
                return true;

            case R.id.itEnviarInformacion:

                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);


                sincronizar();

                return true;

            case R.id.itEnviarInformacion2:

                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);


                enviarInformacion();

                return true;

            case R.id.itConfigurarImpresora:

                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);


                configurarImpresora();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * METODO PARA SINCRONIZAR DATOS CON EL SERVER
     */
    private void sincronizar() {

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(PrincipalActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Alert.vistaDialogoCerrarSesion(this, "¿Are you sure you want to synchronize information?", "Synchronize Information",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (Utilidades.verificarNetwork(PrincipalActivity.this)) {
                                    //   ProgressView.getInstance().Show(PrincipalActivity.this, "Sincronizando info...");
                                    progressDoalog = new ProgressDialog(PrincipalActivity.this);
                                    progressDoalog.setCancelable(false);
                                    progressDoalog.setMax(100);

                                    Alert.dialogo.cancel();

                                    if (Utilidades.verificarNetwork(PrincipalActivity.this)) {


                                        if (!DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {

                                            Gson gson = new Gson();
                                            String stringJsonObject = PreferencesUsuario.obtenerUsuario(PrincipalActivity.this);
                                            usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
                                            // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

                                            Sync sync1 = new Sync(PrincipalActivity.this, Constantes.DESCARGARINFO, PrincipalActivity.this);

                                            sync1.user = usuarioApp.codigo;
                                            sync1.password = usuarioApp.contrasena;
                                            sync1.imei = Utilidades.obtenerImei(PrincipalActivity.this);
                                            sync1.start();
                                            envioInformacion = true;

                                            progressDoalog.setMessage("Synchronizing information....");
                                            progressDoalog.setTitle("Sync");
                                            progressDoalog.setCancelable(false);
                                            progressDoalog.show();


                                        } else if (DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {
                                            Toasty.warning(getApplicationContext(), "There is registered information, to synchronize first send information.", Toasty.LENGTH_SHORT).show();
                                            Alert.dialogo.cancel();
                                        }
                                    } else {
                                        ProgressView.getInstance().Dismiss();
                                        Toasty.warning(getApplicationContext(), "No connection to the internet.", Toasty.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toasty.warning(getApplicationContext(), "No connection to the internet", Toasty.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                            }
                        });


            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Alert.vistaDialogoCerrarSesion(this, "¿Esta seguro que quiere sincronizar informacion?", "Sincronizar Informacion",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (Utilidades.verificarNetwork(PrincipalActivity.this)) {
                                    //   ProgressView.getInstance().Show(PrincipalActivity.this, "Sincronizando info...");
                                    progressDoalog = new ProgressDialog(PrincipalActivity.this);
                                    progressDoalog.setCancelable(false);
                                    progressDoalog.setMax(100);

                                    Alert.dialogo.cancel();

                                    if (Utilidades.verificarNetwork(PrincipalActivity.this)) {


                                        if (!DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {

                                            Gson gson = new Gson();
                                            String stringJsonObject = PreferencesUsuario.obtenerUsuario(PrincipalActivity.this);
                                            usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
                                            // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

                                            Sync sync1 = new Sync(PrincipalActivity.this, Constantes.DESCARGARINFO, PrincipalActivity.this);

                                            sync1.user = usuarioApp.codigo;
                                            sync1.password = usuarioApp.contrasena;
                                            sync1.imei = Utilidades.obtenerImei(PrincipalActivity.this);
                                            sync1.start();
                                            envioInformacion = true;

                                            progressDoalog.setMessage("Sincronizando informacion....");
                                            progressDoalog.setTitle("Sincronizacion");
                                            progressDoalog.setCancelable(false);
                                            progressDoalog.show();


                                        } else if (DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {

                                            if (lenguajeElegido == null) {

                                            } else if (lenguajeElegido != null) {
                                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                                    Toasty.warning(getApplicationContext(), "There is registered information, to synchronize first send information.", Toasty.LENGTH_SHORT).show();


                                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                    Toasty.warning(getApplicationContext(), "Hay informacion registrada, para sincronizar primero envia informacion.", Toasty.LENGTH_SHORT).show();

                                                }
                                            }
                                            Alert.dialogo.cancel();
                                        }


                                    } else {
                                        ProgressView.getInstance().Dismiss();
                                        if (lenguajeElegido == null) {

                                        } else if (lenguajeElegido != null) {
                                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                                Toasty.warning(getApplicationContext(), "No internet connection.", Toasty.LENGTH_SHORT).show();


                                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                Toasty.warning(getApplicationContext(), "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();

                                            }
                                        }

                                    }
                                }
                                else {
                                    Toasty.warning(getApplicationContext(), "No tiene conexion a internet", Toasty.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                            }
                        });

            }
        }


    }

    private void sincronizar2() {

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(PrincipalActivity.this);
        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

        Sync sync1 = new Sync(PrincipalActivity.this, Constantes.DESCARGARINFO, PrincipalActivity.this);

        sync1.user = usuarioApp.codigo;
        sync1.password = usuarioApp.contrasena;
        sync1.imei = Utilidades.obtenerImei(PrincipalActivity.this);
        sync1.start();
        envioInformacion = true;

        //    File folder = new File(Environment.getExternalStorageDirectory() +
        //           File.separator + "CarteraBaldomero/Temp.db");

        //     if (!folder.exists()) {
        //          System.out.println("No Existe temp.....  " + folder);
        //    } else {
        progressDoalog = new ProgressDialog(PrincipalActivity.this);
        progressDoalog.setCancelable(false);
        progressDoalog.setMax(100);

        progressDoalog.setMessage("Descargando informacion....");
        progressDoalog.setTitle("Descargando");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDoalog.setCancelable(false);
        progressDoalog.show();

    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(2);
        }
    };


    private void configurarImpresora() {
        Intent intent = new Intent(PrincipalActivity.this, FormConfigurarImpresora.class);
        startActivity(intent);
    }

    /**
     * METODO PARA MOSTRAR EL DIALOGO DE CERRAR SESION
     */
    @Override
    public void onBackPressed() {
        mostrarDialogoCerrarSesion();
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

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(PrincipalActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                inflater.inflate(R.menu.home_menu_ingles, menu);

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                inflater.inflate(R.menu.home_menu, menu);
            }
        }


        return true;
    }

    /**
     * DIALOGO PARA CERRAR SESION
     */
    private void mostrarDialogoCerrarSesion() {

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(PrincipalActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                Alert.vistaDialogoCerrarSesion(this, "¿Are you sure you want shut down?", "Close Session",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();

                                if (DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {
                                    // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL
                                    Alert.alertGeneral(PrincipalActivity.this, null, "You have pending information to send", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Alert.dialogo.cancel();
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Alert.dialogo.cancel();
                                        }
                                    });

                                } else if (!DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {

                                    cerrarSesionSync();

                                }

                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                            }
                        });

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Alert.vistaDialogoCerrarSesion(this, "Cerrar Sesion", "¿Esta seguro que quiere cerrar sesion?",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();

                                if (DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {
                                    Alert.alertGeneral(PrincipalActivity.this, null, "Tiene información pendiente por enviar", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Alert.dialogo.cancel();
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Alert.dialogo.cancel();
                                        }
                                    });

                                } else if (!DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {

                                    cerrarSesionSync();

                                }

                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                            }
                        });
            }
        }


    }

    /**
     * METODO PARA CERRAR SESION
     */
    private void cerrarSesion() {

        mostrarDialogoCerrarSesion();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferencesUsuario.vaciarPreferencesUsuario(PrincipalActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        // Cancelar el trabajo cuando la actividad se destruya
        WorkManager.getInstance(this).cancelAllWorkByTag(WORK_TAG);
    }

    /**
     * METODO PARA ENVIAR INFORMACION ( SINCRONIZAR DATOS)
     */
    private void enviarInformacion() {

        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(PrincipalActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Alert.vistaDialogoTerminarDia(PrincipalActivity.this, "Send Information", "¿Are you sure you want to send information?",
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (Utilidades.verificarNetwork(PrincipalActivity.this)) {
                                    if (DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {
                                        final String empresa;
                                        empresa = DataBaseBO.cargarCodigo(PrincipalActivity.this);
                                        ProgressView.getInstance().Show(PrincipalActivity.this, "Sending information...");

                                        Sync sync = new Sync(PrincipalActivity.this, Constantes.ENVIARINFORMACION, PrincipalActivity.this);
                                        sync.user = empresa;
                                        sync.start();
                                        Alert.dialogo.cancel();
                                        DataBaseBO.borrarInfoTemp(PrincipalActivity.this);
                                        ProgressView.getInstance().Dismiss();
                                        envioInformacion = true;
                                    } else {
                                        envioInformacion = false;
                                        Alert.dialogo.cancel();
                                        ProgressView.getInstance().Dismiss();
                                    }

                                    if (envioInformacion == true) {

                                        Alert.alertGeneral(PrincipalActivity.this, null, "The information was sent correctly", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ProgressView.getInstance().Dismiss();
                                                Alert.dialogo.cancel();
                                            }
                                        }, null);

                                    } else {
                                        Alert.alertGeneral(PrincipalActivity.this, null, "No information to be sent", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ProgressView.getInstance().Dismiss();
                                                Alert.dialogo.cancel();
                                            }
                                        }, null);
                                    }
                                }
                                else
                                {
                                    Toasty.warning(getApplicationContext(), "No connection to the internet", Toasty.LENGTH_SHORT).show();
                                }
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Alert.dialogo.cancel();
                            }
                        });

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Alert.vistaDialogoTerminarDia(PrincipalActivity.this, "Enviar Información", "¿Esta seguro que desea enviar información?",
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (Utilidades.verificarNetwork(PrincipalActivity.this)) {
                                    if (DataBaseBO.hayInformacionXEnviar(PrincipalActivity.this)) {
                                        final String empresa;
                                        empresa = DataBaseBO.cargarCodigo(PrincipalActivity.this);
                                        ProgressView.getInstance().Show(PrincipalActivity.this, "Enviando Información...");

                                        Sync sync = new Sync(PrincipalActivity.this, Constantes.ENVIARINFORMACION, PrincipalActivity.this);
                                        sync.user = empresa;
                                        sync.start();
                                        Alert.dialogo.cancel();
                                        DataBaseBO.borrarInfoTemp(PrincipalActivity.this);
                                        ProgressView.getInstance().Dismiss();
                                        envioInformacion = true;
                                    } else {
                                        envioInformacion = false;
                                        Alert.dialogo.cancel();
                                        ProgressView.getInstance().Dismiss();
                                    }

                                    if (envioInformacion == true) {

                                        Alert.alertGeneral(PrincipalActivity.this, null, "Se envió correctamente la información", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ProgressView.getInstance().Dismiss();
                                                Alert.dialogo.cancel();
                                            }
                                        }, null);

                                    } else {
                                        Alert.alertGeneral(PrincipalActivity.this, null, "No hay información por enviar", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ProgressView.getInstance().Dismiss();
                                                Alert.dialogo.cancel();
                                            }
                                        }, null);
                                    }
                                }
                                else
                                {
                                    Toasty.warning(getApplicationContext(), "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
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


    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_Ruta", estado);
        editor1.commit();


    }

    private void guardarVistaPrincipal() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FinalPrincipal", estado);
        editor1.commit();


    }

    private void eliminarEstados() {
        PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(getApplicationContext());
        PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
        PreferencesCarteraFactura.vaciarPreferencesFacturaSeleccionada(getApplicationContext());
        PreferencesClienteSeleccionado.vaciarPreferencesClienteSeleccionado(getApplicationContext());
        PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
        PreferencesParcial.vaciarPreferencesParcialSeleccionada(getApplicationContext());
        PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(getApplicationContext());
        PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(getApplicationContext());

        PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(getApplicationContext());

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_Principal", true);
        editor1.remove("estado_Principal");
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

        editor1.putBoolean("estado_FacturasSeleccRealizadas", true);
        editor1.remove("estado_FacturasSeleccRealizadas");

        editor1.putBoolean("estado_Pendientes", true);
        editor1.remove("estado_Pendientes");
        editor1.putBoolean("estado_MetodosDePagoPendientes", true);
        editor1.remove("estado_MetodosDePagoPendientes");
        editor1.putBoolean("estado_FacturasSeleccMultiplesPendientes", true);
        editor1.remove("estado_FacturasSeleccMultiplesPendientes");
        editor1.commit();
        finish();

    }

    public void actualizarVersionApp() {

        final Version versionSvr = DataBaseBO.ObtenerVersionApp(PrincipalActivity.this);
        final String empresa = DataBaseBO.cargarEmpresa(PrincipalActivity.this);
        String versionApp = ObtenerVersion();

        if (versionSvr != null && versionApp != null) {

            float versionServer = Utilidades.ToFloat(versionSvr.version.replace(".", ""));
            float versionLocal = Utilidades.ToFloat(versionApp.replace(".", ""));

            if (versionLocal < versionServer && empresa.equals(versionSvr.empresa)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
                builder.setMessage("Hay una version de la aplicacion: " + versionSvr.version)
                        .setCancelable(false)
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                progressDoalog = ProgressDialog.show(PrincipalActivity.this, "", "Descargando Version " + versionSvr.version + "...", true);
                                progressDoalog.show();

                                Sync sync = new Sync(PrincipalActivity.this::respSync, Constantes.DOWNLOAD_VERSION_APP, PrincipalActivity.this);
                                sync.start();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        }
    }

    public String ObtenerVersion() {

        String versionApp;

        try {

            versionApp = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {

            versionApp = "0.0";
            Log.e("FormPrincipalActivity", "ObtenerVersion: " + e.getMessage(), e);
        }

        return versionApp;
    }

    public void installApk() {

        Context context = PrincipalActivity.this;

        File fileApp = new File(Utilidades.dirApp(PrincipalActivity.this), Constantes.fileNameApk);

        if (!fileApp.exists()) {
            // El archivo no existe
            return;
        }

        // Verificar permisos de instalación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                // Solicitar permiso para instalar aplicaciones desconocidas
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                return;
            }
        }

        // Crear la URI usando FileProvider (requerido para Android 7+)
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext().getPackageName() + ".provider",
                    fileApp
            );
        } else {
            apkUri = Uri.fromFile(fileApp);
        }

        // Crear intent de instalación
        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        installIntent.setData(apkUri);
        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Para Android 10+ necesitamos flag adicional
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        // Iniciar la instalación
        context.startActivity(installIntent);
    }

    private void cerrarSesionSync() {

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(PrincipalActivity.this);
        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

        Sync sync1 = new Sync(PrincipalActivity.this, Constantes.CERRAR_SESION, PrincipalActivity.this);

        sync1.user = usuarioApp.codigo;
        sync1.password = usuarioApp.contrasena;
        sync1.imei = Utilidades.obtenerImei(PrincipalActivity.this);
        sync1.version = Utilidades.getVersion(PrincipalActivity.this);
        sync1.start();
        envioInformacion = true;

        progressDoalog = new ProgressDialog(PrincipalActivity.this);
        if (lenguajeElegido.lenguaje.equals("ESP")) {
            progressDoalog.setMessage("Sincronizacion");
        } else {
            progressDoalog.setMessage("logging out");
        }

        progressDoalog.setCancelable(false);
        progressDoalog.show();

    }

    private void respuestaCerrarSesion(boolean ok, String respuestaServer, String msg) {

        if(progressDoalog != null)
            progressDoalog.dismiss();

        if(ok) {
            PreferencesUsuario.vaciarPreferencesUsuario(PrincipalActivity.this);

            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
            settings.edit().clear().apply();
            getSharedPreferences("session", 0).edit().clear().apply();
            getSharedPreferences("session", 0).edit().clear().apply();
            Alert.dialogo.cancel();

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            finish();
        } else {

            String mensaje = "";

            if (lenguajeElegido.lenguaje.equals("ESP")) {
                mensaje = "Error al cerrar sesion.";
            } else {
                mensaje = "error when closing session";
            }

            Utilidades.MostrarAlertDialog(PrincipalActivity.this, mensaje);

        }
    }
}
