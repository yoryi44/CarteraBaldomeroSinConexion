package co.com.celuweb.carterabaldomero;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.ClienteSincronizado;
import dataobject.Lenguaje;
import dataobject.Main;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesNombreFirma;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class LoginActivity extends AppCompatActivity implements Synchronizer {

    public static final String TAG = LoginActivity.class.getName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final String NOMBRE = "usuario";
    private EditText etUsuario;
    private EditText etContrasena;
    private Button inicio;
    private Lenguaje lenguajeElegido;

    private String usuario = "";
    private String contrasena = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ValidarUsuario();

    }

    /**
     * VALIDAR EL USUARIO
     */
    private void ValidarUsuario() {

        //BORRAR EL NOMBRE DE LA FIRMA ALMACENADO EN EL SHARED PREFERERNCES
        PreferencesNombreFirma.vaciarPreferencesNombreFirma(this);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(LoginActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        /**
         * TEXT DE PRODUCCION O PRUEBAS
         * VERSION Y PERMISOS
         */
        TextView tvVersionApp = findViewById(R.id.tvVersionAppLogin);

        Main.version = Utilidades.getVersion(LoginActivity.this);

        if (lenguajeElegido == null) {
            String ambiente = (Constantes.APLICACION == Constantes.PRODUCCION) ? "Producción" : "Pruebas Beta";

            String version = "Versión " + Main.version + " " + ambiente;
            tvVersionApp.setText(version);
        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                String ambiente = (Constantes.APLICACION == Constantes.PRODUCCION) ? "Production" : "Beta Testing";

                String version = "Versión " + Main.version + " " + ambiente;
                tvVersionApp.setText(version);

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                String ambiente = (Constantes.APLICACION == Constantes.PRODUCCION) ? "Producción" : "Pruebas Beta";

                String version = "Versión " + Main.version + " " + ambiente;
                tvVersionApp.setText(version);

            }
        }


        /**
         * PERMISOS
         */
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions()) {
            }
        }

        /**
         * SHARED PREFERENCE
         */
        SharedPreferences preferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        //VALIDAR SI ME ESTAN LLAMANDO DESDE PREVENTA USA
        if (!preferences.getAll().isEmpty()) {

            Bundle bundle = getIntent().getExtras();

            //VALIDAR EL CLIENTE DESDE PREVENTA
            if (bundle != null) {

                if (bundle.containsKey(Intent.EXTRA_TEXT)) {

                    //RECUPERAR CLIENTE
                    String codigo = bundle.getString(Intent.EXTRA_TEXT);

                    //CARGAR CLIENTE DESDE CARTERA VALDOMERO
                    List<ClienteSincronizado> listaClientesSincronizados2 = DataBaseBO.cargarClientesBusqueda(codigo, new Vector<>());

                    //VALIDAR SI EXISTE EL CLIENTE EN CARTERA VALDOMERO
                    if (!listaClientesSincronizados2.isEmpty()) {

                        //GUARDA LA INSTANCIA DE QUE ES PREVENTA PARA NAVEGAR
                        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = settings.edit();
                        editor1.putBoolean("esPreventa", true);
                        editor1.apply();

                        ClienteSincronizado usuarioSeleccionado = listaClientesSincronizados2.get(0);
                        guardarVista();
                        // 2. SE ALMACENA EL CLIENTE SELECCIONADO
                        Gson gson = new Gson();
                        String jsonStringObject = gson.toJson(usuarioSeleccionado);
                        PreferencesClienteSeleccionado.guardarClienteSeleccionado(this, jsonStringObject);

                        // 3. SE MUESTRA LA VISTA PRINCIPAL CON LA INFORMACION DEL CLIENTE
                        Intent vistaClienteActivity = new Intent(this, VistaClienteActivity.class);
                        startActivity(vistaClienteActivity);
                    } else {
                        Toast.makeText(LoginActivity.this, "El cliente no existe en cartera", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    activitysIntents();
                }
            } else {
                activitysIntents();
            }
        } else if (preferences == null) {

        }

    }

    /**
     * ESTADOS, INICIO ACTIVITYS SHAREDPREFRENCE
     */
    private void activitysIntents() {

        //BORRAR LA INSTANCIA DE QUE ES PREVENTA PARA NAVEGAR
        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("esPreventa", false);
        editor1.commit();

        SharedPreferences preferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        if (preferences.getBoolean("estado_Principal", false) == false) {
//            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(login);
//            finish();
        } else {
            Intent login = new Intent(getApplicationContext(), PrincipalActivity.class);
            startActivity(login);
            finish();
        }

        if (preferences.getBoolean("estado_Ruta", false) == false) {


        } else {
            Intent login = new Intent(getApplicationContext(), RutaActivity.class);
            startActivity(login);
            finish();
        }

        if (preferences.getBoolean("estado_VistaCliente", false) == false) {

        } else {

            Intent login = new Intent(getApplicationContext(), VistaClienteActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_ReciboDinero", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), ReciboDineroActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_Cartera", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), CarteraActivity.class);
            startActivity(login);
            finish();

        }


        if (preferences.getBoolean("estado_AnticipoRecibo", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), MetodosDePagoActivity.class);
            startActivity(login);
            finish();

        }


        if (preferences.getBoolean("estado_FacturasSeleccionadas", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), FacturasSeleccionadasActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_FormaPago", false) == false) {


        } else {
            Intent login = new Intent(getApplicationContext(), formaPagoActivity.class);
            startActivity(login);
            finish();


        }

        if (preferences.getBoolean("estado_FormaPagoTotal", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), MetodosDePagoActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_FormaPagoParcial", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), MetodosDePagoActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_MultiplesFotos", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), MultiplesFotosActivity.class);
            startActivity(login);
            finish();

        }


        if (preferences.getBoolean("estado_FinalPrincipal", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), PrincipalActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_Pendientes", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), PendientesActivity.class);
            startActivity(login);
            finish();


        }

        if (preferences.getBoolean("estado_MetodosDePagoPendientes", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), MetodosDePagoPendientesActivity.class);
            startActivity(login);
            finish();


        }

        if (preferences.getBoolean("estado_Informes", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), InformesActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_FacturasRealizadas", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), RealizadosActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_FacturasSeleccRealizadas", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), FacturasRealizadasSeleccionadasActivity.class);
            startActivity(login);
            finish();

        }

        if (preferences.getBoolean("estado_FacturasSeleccMultiplesPendientes", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), MetodosDePagoPendientesActivity.class);
            startActivity(login);
            finish();


        }

        if (preferences.getBoolean("estado_Lenguaje", false) == false) {


        } else {

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            finish();


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarVista();
        ValidarUsuario();
    }

    /**
     * INICIALIZACION DE USUARIO Y CONTRASEÑA,
     * METODO DE CARGA
     */
    public void configurarVista() {
        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        inicio = findViewById(R.id.Inicio);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                etUsuario.setHint("User");
                etContrasena.setHint("Password");
                inicio.setText("SIGN IN");
            } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                etUsuario.setHint("Usuario");
                etContrasena.setHint("Contraseña");
                inicio.setText("INGRESAR");
            }
        }
        carga();

    }

    /**
     * METODO,CARGA,VALIDA EL USUARIO GUARDADO EN SAHREDPREFERENCES
     */
    private void carga() {

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(this);
        if (stringJsonObject != null) {
            SharedPreferences settings = getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
            String user = settings.getString("user", "");
            String pass = settings.getString("pass", "");

            etUsuario.setText(user);
            etContrasena.setText(pass);

        }

    }

    public void onClickUSA(View view) {
        etUsuario.setHint("User");
        etContrasena.setHint("Password");
        inicio.setText("SIGN IN");

        Lenguaje lenguaje = new Lenguaje();
        lenguaje.lenguaje = "USA";
        Gson gson = new Gson();
        String jsonStringObject = gson.toJson(lenguaje);
        PreferencesLenguaje.guardarLenguajeSeleccionada(LoginActivity.this, jsonStringObject);


    }

    public void onClickESP(View view) {
        etUsuario.setHint("Usuario");
        etContrasena.setHint("Contraseña");
        inicio.setText("INGRESAR");

        Lenguaje lenguaje = new Lenguaje();
        lenguaje.lenguaje = "ESP";
        Gson gson = new Gson();
        String jsonStringObject = gson.toJson(lenguaje);
        PreferencesLenguaje.guardarLenguajeSeleccionada(LoginActivity.this, jsonStringObject);


    }

    /**
     * METODO LLAMADO AL VALIDAR EL INICIO DE SESION
     *
     * @param view
     */
    public void onClickIngresar(View view) throws InterruptedException {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        hideKey(view);

        usuario = etUsuario.getText().toString();
        contrasena = etContrasena.getText().toString();

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(LoginActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {
            Toasty.warning(this, "Primero seleccione un idioma antes de iniciar sesion.", Toasty.LENGTH_SHORT).show();

        } else if (lenguajeElegido != null) {

            if (lenguajeElegido.lenguaje.equals("USA")) {

                if (usuario.equals("")) {
                    Toasty.warning(this, "Please enter the user name.", Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (contrasena.equals("")) {
                    Toasty.warning(this, "Please enter the password.", Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (Utilidades.verificarNetwork(LoginActivity.this)) {


                    ProgressView.getInstance().Show(LoginActivity.this, "Validating Credential");


                    usuarioLogin(usuario, contrasena);


                } else {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(this, "No internet connection.", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(this, "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
                        }
                    }

                }

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                if (usuario.equals("")) {
                    Toasty.warning(this, "Por favor ingrese el usuario.", Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (contrasena.equals("")) {
                    Toasty.warning(this, "Por favor ingrese la contraseña.", Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (Utilidades.verificarNetwork(LoginActivity.this)) {


                    ProgressView.getInstance().Show(LoginActivity.this, "Validando Credenciales");


                    usuarioLogin(usuario, contrasena);


                } else {

                    Toasty.warning(this, "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();
                }
            }
        }


    }

    /**
     * REALIZA LA PETICION CON EL SERVIDOR
     *
     * @param user
     * @param password
     */
    public void usuarioLogin(String user, String password) {

        try {


            Sync sync = new Sync(LoginActivity.this, Constantes.LOGIN);


            sync.user = user;
            sync.password = password;
            sync.start();

        } catch (Exception exception) {
            System.out.println("Error en el usuario login modulo login");
        }

    }

    /**
     * RESPUESTA DEL SERVIDOR DEL SINCRONIZADOR
     *
     * @param ok
     * @param respuestaServer
     * @param msg
     * @param codeRequest
     */
    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
        try {

            switch (codeRequest) {

                case Constantes.LOGIN:
                    respuestaLogin(ok, respuestaServer, msg);

                    break;

                case Constantes.DESCARGARINFO:
                    respuestaSincronizacionClientes(ok, respuestaServer, msg);


                    break;

            }

        } catch (Exception exception) {
            System.out.println("Error en el repSync en el modulo Login");
        }

    }

    private void respuestaSincronizacionClientes(boolean ok, String respuestaServer, final String msg) {

        //Progress.hide();


        try {

            if (ok) {

                LoginActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        etUsuario.getText().toString();
                        etContrasena.getText().toString();

                        if (respuestaServer.equals("Usuario Incorrecto\n")) {


                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Alert.nutresaShow(LoginActivity.this, "Information", "Incorrect user and/or password", "Accept", null,

                                            new View.OnClickListener() {

                                                @Override
                                                public void onClick(View view) {
                                                    ProgressView.getInstance().Dismiss();
                                                    Alert.dialogo.cancel();
                                                }

                                            }, null);

                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Alert.nutresaShow(LoginActivity.this, "Informacion", "Usuario y/o clave incorrecto.", "Aceptar", null,

                                            new View.OnClickListener() {

                                                @Override
                                                public void onClick(View view) {
                                                    ProgressView.getInstance().Dismiss();
                                                    Alert.dialogo.cancel();
                                                }

                                            }, null);
                                }
                            }
                            //ALERT


                        } else if (msg.contains("Unable to resolve host")) {

                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(LoginActivity.this, "No internet connection.", Toasty.LENGTH_SHORT).show();


                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(LoginActivity.this, "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();

                                }
                            }


                        } else if (respuestaServer.equals("Se ha cargado al cliente al rutero correctamente")) {


                            boolean hayClientesEnRutero = Utilidades.existeArchivoDataBaseTemp();

                            if (hayClientesEnRutero) {

                                Intent vistaPrincipal = new Intent(LoginActivity.this, PrincipalActivity.class);
                                startActivity(vistaPrincipal);

                                finish();


                            } else {

                                if (lenguajeElegido == null) {

                                } else if (lenguajeElegido != null) {
                                    if (lenguajeElegido.lenguaje.equals("USA")) {


                                        Toasty.warning(LoginActivity.this, "The amount field cannot be blank or set to 0..", Toasty.LENGTH_SHORT).show();
                                        finish();

                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                                        Toasty.warning(LoginActivity.this, "El campo del monto no puede quedar en blanco o estar en 0..", Toasty.LENGTH_SHORT).show();
                                        finish();

                                    }
                                }


                            }


                        }
                    }
                });

            } else {
                LoginActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        String mensaje = "";
                        if (lenguajeElegido.lenguaje.equals("USA"))
                            mensaje = "Error downloading database";
                        else
                            mensaje = "Error descargando la base de datos";

                        ProgressView.getInstance().Dismiss();
                        Alert.alertGeneral(LoginActivity.this, null, "Error descargando la base de datos", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Alert.dialogo.cancel();


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

    /**
     * RESPUESTA DEL SINCRONIZADOR Y VALIDACIONES DE INICIO
     *
     * @param ok
     * @param respuestaServer
     * @param msg
     */
    public void respuestaLogin(final boolean ok, final String respuestaServer, final String msg) {

        try {


            if (ok) {

                if (respuestaServer.equals("Usuario Incorrecto\n")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Usuario o Contraseña Incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ProgressView.getInstance().Dismiss();
                } else {
                    Usuario usuarioApp = new Usuario();
                    usuarioApp.codigo = usuario;
                    usuarioApp.contrasena = contrasena;
                    Gson gson = new Gson();
                    String jsonStringObject = gson.toJson(usuarioApp);

                    PreferencesUsuario.guardarUsuario(LoginActivity.this, jsonStringObject);

                    Sync sync1 = new Sync(LoginActivity.this, Constantes.DESCARGARINFO);

                    sync1.user = usuario;
                    sync1.password = contrasena;
                    sync1.start();

                    credencialesUsuario(respuestaServer, etUsuario.getText().toString(), etContrasena.getText().toString());
                }

            } else {
                if (respuestaServer.equals("error")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "error al iniciar dia", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ProgressView.getInstance().Dismiss();


//                    try {
//                        Thread.sleep(2000);
//                        Intent vistaPrincipal = new Intent(LoginActivity.this, PrincipalActivity.class);
//                        startActivity(vistaPrincipal);
//
//                        finish();
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }


                }

            }
        } catch (Exception e) {

            String mensaje = e.getMessage();
            Log.e(TAG, "respuestaLogin -> " + mensaje, e);

        }
    }

    /**
     * METODO PARA VALIDAR EL USUARIO Y DESCARGAR BASE DE DATOS
     *
     * @param respuestaServer
     * @param etUsuarioFinal
     * @param etContrasenaFinal
     */
    private void credencialesUsuario(String respuestaServer, final String etUsuarioFinal, final String etContrasenaFinal) {

        try {

            if (respuestaServer.equals("ok\n")) {
                LoginActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        Sync sync = new Sync(LoginActivity.this, Constantes.LOGIN);

                        guardarVista();
                        if (etUsuarioFinal.equals(sync.user) && etContrasenaFinal.equals(sync.password)) {
                            Intent vistaPrincipal = new Intent(LoginActivity.this, PrincipalActivity.class);

                            vistaPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(vistaPrincipal, 100); //........

                            SharedPreferences settings = getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
                            String user = etUsuario.getText().toString();
                            String pass = etContrasena.getText().toString();

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("user", user);
                            editor.putString("pass", pass);
                            editor.commit();
                        }


                    }
                });


            } else if (respuestaServer.equals("Usuario Incorrecto\n")) {

                Usuario usuarioApp = new Usuario();

                usuarioApp.codigo = etUsuarioFinal;
                usuarioApp.contrasena = etContrasenaFinal;

                Gson gson = new Gson();
                String jsonStringObject = gson.toJson(usuarioApp);

                PreferencesUsuario.guardarUsuario(this, jsonStringObject);


            }
        } catch (Exception e) {


        }


    }

    /**
     * SHARED PREFERENCE VISTA
     */
    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_Principal", estado);
        editor1.commit();


    }

    private void guardarVistaLenguaje() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_Lenguaje", estado);
        editor1.commit();


    }

    /**
     * OCULTA EL TECLADO AL MOMENTO DE PRESIONAR EL BOTON
     *
     * @param v
     */
    private void hideKey(View v) {

        InputMethodManager q = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        q.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * PERMISOS
     *
     * @return TRUE/FALSE
     */
    private boolean checkAndRequestPermissions() {

        int permisoInternet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permisoEstadoRed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int permisoEstadoTelefono = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permisoEscrituraExterna = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permisoLecturaExterna = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permisoGPS = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permisoLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permisoCamara = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permisoInternet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }

        if (permisoEstadoRed != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }

        if (permisoEstadoTelefono != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (permisoEscrituraExterna != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permisoLecturaExterna != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (permisoGPS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permisoLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }

        if (permisoCamara != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            // inicializacionConfiguracionApp();
            return false;
        }

        return true;
    }


}
