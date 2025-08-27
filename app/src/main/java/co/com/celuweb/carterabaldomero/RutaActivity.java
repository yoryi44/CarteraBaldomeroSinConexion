package co.com.celuweb.carterabaldomero;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import Adapters.AdapterListaClientes;
import Adapters.AdapterListaDias;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.Anticipo;
import dataobject.ClienteSincronizado;
import dataobject.Dias;
import dataobject.Lenguaje;
import dataobject.Main;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesCarteraFactura;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesParcial;
import sharedpreferences.PreferencesPendientesFacturas;
import sharedpreferences.PreferencesReciboDinero;
import sharedpreferences.PreferencesUsuario;
import utilidades.Constantes;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class RutaActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, Synchronizer {

    private RecyclerView rvListaClientesSincronizados;
    private List<ClienteSincronizado> listaClientesSincronizados2;
    private EditText etParametroBusqueda;
    private List<Dias> listaDias;
    private Anticipo anticipo;
    private Lenguaje lenguajeElegido;
    Button lstClientes,ruta,buzon;
    TextView clientes;
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);
         lstClientes = findViewById(R.id.lstClientes);
         ruta = findViewById(R.id.nombreruta);
         buzon = findViewById(R.id.nombrebuzon);
         clientes = findViewById(R.id.clientes);

        etParametroBusqueda = findViewById(R.id.etParametroBusqueda2);


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(RutaActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Route"));
                lstClientes.setText("CUSTOMERS LIST");
                ruta.setText("ROUTE");
                buzon.setText("INBOX");
                clientes.setText("Customers");
                etParametroBusqueda.setHint("Search filter");

            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Ruta"));

            }
        }



        listaClientesSincronizados2 = new ArrayList<>();
        listaDias = new ArrayList<>();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerDiasRuta);
        spinner.setVisibility(View.INVISIBLE);
        Vector<String> listaItems = new Vector<>();
        listaClientesSincronizados2 = DataBaseBO.cargarClientes(listaItems, RutaActivity.this);

        rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
        rvListaClientesSincronizados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterListaClientes adapter = new AdapterListaClientes(listaClientesSincronizados2, RutaActivity.this);
        rvListaClientesSincronizados.setAdapter(adapter);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infocliente(view);
            }
        });

        /**
         * ASYNTASK
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RutaActivity.cargarRuteroAsynTask asyncTask = new RutaActivity.cargarRuteroAsynTask();
                asyncTask.execute();
            }
        }, 1000);

        /**
         * TEXTWATCHER
         */

        etParametroBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (!s.toString().equals("")) {
                    BuscarClientesParametroRuta(s.toString());
                } else {
                    //Limpiar la cadena ya que la busqueda de letras es  vacia.
                }
            }
        });

    }

    /**
     * INICIALIZACION BANNERUSUARIOS
     */
    private void configurarVista() {
        bannersUsiarios();

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_Ruta", estado);
        editor1.commit();
        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(this);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);
        try {

            // SE OBTIENE LA INFORMACION DE CLIENTES EN LISTA DEL RUTERO
            boolean existeArchivoDataBase = Utilidades.existeArchivoDataBase();


            if (existeArchivoDataBase) {

//                mostrarVistaDialogoRuta(false);

                if (Main.clientesOrdenados) {
//                listaClientesSincronizados = Main.listaClientes;
                    int orden = PreferencesClienteSeleccionado.obtenerOrden(RutaActivity.this);
                    Main.ordenClientes = PreferencesClienteSeleccionado.loadListaClienteOrdenados(RutaActivity.this);
//                cargarListaClientesSincronizados(false, 1);


                } else {
                    // SE CARGA LA LISTA DE CLIENTES
                    int orden = PreferencesClienteSeleccionado.obtenerOrden(RutaActivity.this);
                    Main.ordenClientes = PreferencesClienteSeleccionado.loadListaClienteOrdenados(RutaActivity.this);
                }

            } else {


            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    /**
     * BANNER USUARIO SEGUN SU EMPRESA
     */
    private void bannersUsiarios() {

        String empresa = "";

        empresa = DataBaseBO.cargarEmpresa(RutaActivity.this);

        if (empresa.equals("AGCO")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A6194525")));

        } else if (empresa.equals("AGSC")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AABR")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("ADHB")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AFPN")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AFPP")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AFPZ")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AGAH")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        } else if (empresa.equals("AGDP")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E002E86")));

        } else if (empresa.equals("AGGC")) {

            ActionBar barVista = getSupportActionBar();
            barVista.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DF2E22")));

        } else if (empresa.equals("AGUC")) {

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

        return super.onOptionsItemSelected(item);
    }

    /**
     * FINALIZA EL ACTIVITY
     */
    @Override
    public void onBackPressed() {


        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_Ruta", true);
        editor1.remove("estado_Ruta");
        editor1.commit();


        Intent vistaClienteActivity = new Intent(this, PrincipalActivity.class);

        startActivity(vistaClienteActivity);


        finish();
    }

    /**
     * Metodo para dar evento al boton de modificar ruta que se muestra en la vista
     *
     * @param view vista del boton
     */
    public void onClickModificarRuta(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerDiasRuta);
        spinner.setVisibility(View.INVISIBLE);
        Vector<String> listaItems = new Vector<>();
        listaClientesSincronizados2 = DataBaseBO.cargarClientes(listaItems, RutaActivity.this);

        rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
        rvListaClientesSincronizados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterListaClientes adapter = new AdapterListaClientes(listaClientesSincronizados2, RutaActivity.this);
        rvListaClientesSincronizados.setAdapter(adapter);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                infocliente(view);
            }
        });

    }

    /**
     * METODO PARA EL ONCLICK
     *
     * @param v
     */
    private void infocliente(View v) {
        onClick(v);
    }

    /**
     * METODO ONCLICK PARA LA RUTA,CLIENTES,RUTA:DIAS
     *
     * @param view
     */
    public void onClickRuta(View view) {


        rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
        rvListaClientesSincronizados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterListaClientes adapter = new AdapterListaClientes(listaClientesSincronizados2, RutaActivity.this);


        if (adapter == null) {
            rvListaClientesSincronizados.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            listaClientesSincronizados2.clear();
            adapter.notifyDataSetChanged();
        }

        final Spinner spinner = findViewById(R.id.spinnerDiasRuta);
        String parametroBusqueda = spinner.getSelectedItem().toString();

        Vector<String> listaItems = new Vector<>();
        listaDias = DataBaseBO.cargarDias(parametroBusqueda, listaItems, RutaActivity.this);


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(RutaActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.DIAS_INGLES, R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter2);
                spinner.setVisibility(View.VISIBLE);

            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.DIAS, R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter2);
                spinner.setVisibility(View.VISIBLE);

            }
        }




        rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
        rvListaClientesSincronizados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterListaDias adapter3 = new AdapterListaDias(listaDias, RutaActivity.this);
        rvListaClientesSincronizados.setAdapter(adapter3);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int ipos, long l) {

                buscarDia();
                rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
                AdapterListaDias adapter3 = new AdapterListaDias(listaDias, RutaActivity.this);
                rvListaClientesSincronizados.setAdapter(adapter3);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }


        });


    }

    /**
     * METODO PARA LA BUSQUEDA DEL DIA DE RUTA
     */
    private void buscarDia() {

        final Spinner spinner = findViewById(R.id.spinnerDiasRuta);
        String parametroBusqueda = spinner.getSelectedItem().toString();

        Vector<String> listaItems = new Vector<>();
        listaDias = DataBaseBO.cargarDias(parametroBusqueda, listaItems, RutaActivity.this);

        rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
        rvListaClientesSincronizados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterListaDias adapter3 = new AdapterListaDias(listaDias, RutaActivity.this);
        rvListaClientesSincronizados.setAdapter(adapter3);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int ipos, long l) {

                buscarDia();
                rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
                AdapterListaDias adapter3 = new AdapterListaDias(listaDias, RutaActivity.this);
                rvListaClientesSincronizados.setAdapter(adapter3);

                adapter3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        infocliente2(view);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                listaDias.clear();
            }
        });


    }

    /**
     * Metodo para buscar un cliente en especifico y poder organizarlos en el orden que se
     * seleccionaron
     *
     * @param codigoCliente codigo del cliente
     * @return el cliente correspondiente al codigo pasado por parametro
     */
    private ClienteSincronizado buscarClienteSincronizado(String codigoCliente) {

        ClienteSincronizado clienteRetorno = null;
        //Clientes que se sincronizaron o seleccionaron al momento de armar el rutero
        List<ClienteSincronizado> listaClientesDatabase = DataBaseBO.obtenerListaClientesSincronizadosAux(0, RutaActivity.this);
        for (ClienteSincronizado cliente : listaClientesDatabase) {
            if (cliente.codigo.trim().equals(codigoCliente.trim())) {
                clienteRetorno = cliente;
            }
        }
        return clienteRetorno;
    }

    /**
     * SHARED PREFERENCE VISTA
     */
    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_VistaCliente", estado);
        editor1.commit();
    }

    /**
     * SHARED PREFERENCE VISTA
     */
    private void guardarVistaPendientes() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FinalPrincipal", estado);
        editor1.commit();
    }

    /**
     * ONCLICK PARA BUSCAR CLIENTE
     *
     * @param view
     */
    public void onClickBuscarClientesParametroRuta(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);



        String parametroBusqueda;
        parametroBusqueda = etParametroBusqueda.getText().toString();
        BuscarClientesParametroRuta(parametroBusqueda);

    }

    /**
     * METODO, TOMA EL PARAMETRO DE BUSQUEDA Y LLAMA AL METODO
     *
     * @param parametroBusqueda
     */
    public void BuscarClientesParametroRuta(String parametroBusqueda) {
        // 1. PARAMETRO DE BUSQUEDA
        parametroBusqueda = etParametroBusqueda.getText().toString();

        if (parametroBusqueda.length() == 0) {

            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(RutaActivity.this);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


            if (lenguajeElegido == null) {

            }else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Toasty.warning(this, "Enter a search parameter.", Toasty.LENGTH_SHORT).show();
                    return;

                }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Toasty.warning(this, "Ingrese un parámetro de búsqueda.", Toasty.LENGTH_SHORT).show();
                    return;

                }
            }



        }
        busquedaListaClientes(parametroBusqueda);

    }

    /**
     * METODO DONDE TOMA EL PARAMETRO DE BUSQUEDA CIENTES, BUSCA,LISTA,ADAPTER ONCLICK
     *
     * @param parametroBusqueda
     */
    private void busquedaListaClientes(String parametroBusqueda) {
        final Vector<String> listaItems = new Vector<>();
        parametroBusqueda = etParametroBusqueda.getText().toString();
        listaClientesSincronizados2 = DataBaseBO.cargarClientesBusqueda(parametroBusqueda, listaItems, RutaActivity.this);

        rvListaClientesSincronizados = findViewById(R.id.rvListaClientesSincronizados);
        rvListaClientesSincronizados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final AdapterListaClientes adapter = new AdapterListaClientes(listaClientesSincronizados2, RutaActivity.this);
        rvListaClientesSincronizados.setAdapter(adapter);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infocliente(view);
            }
        });

    }

    /**
     * METODO, ONCLICK PARA VER EL CLIENTE LO GUARDA Y DIRIGE
     *
     * @param v
     */
    @Override
    public void onClick(View v) {


        v.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
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
            editor1.putBoolean("estado_Ruta", true);
            editor1.remove("estado_Ruta");
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
            editor1.putBoolean("estado_FinalPrincipal", true);
            editor1.remove("estado_FinalPrincipal");
            editor1.putBoolean("estado_Pendientes", true);
            editor1.remove("estado_Pendientes");
            editor1.commit();
            finish();
        }else  if (anticipo != null) {
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
            editor1.putBoolean("estado_Ruta", true);
            editor1.remove("estado_Ruta");
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
            editor1.putBoolean("estado_FinalPrincipal", true);
            editor1.remove("estado_FinalPrincipal");
            editor1.putBoolean("estado_Pendientes", true);
            editor1.remove("estado_Pendientes");
            editor1.commit();
            finish();
        }


        ClienteSincronizado clientes = new ClienteSincronizado();
        // ACCION DE ABRIR VISTA NUEVA CUANDO SE SELECCIONA LA CARD DEL CLIENTE
        int position = rvListaClientesSincronizados.getChildAdapterPosition(v);
        // 1. SE OBTIENE EL OBJETO DEL CLIENTE
        ClienteSincronizado usuarioSeleccionado = listaClientesSincronizados2.get(position);
        guardarVista();
        // 2. SE ALMACENA EL CLIENTE SELECCIONADO
        Gson gson = new Gson();
        String jsonStringObject = gson.toJson(usuarioSeleccionado);
        PreferencesClienteSeleccionado.guardarClienteSeleccionado(this, jsonStringObject);

        // 3. SE MUESTRA LA VISTA PRINCIPAL CON LA INFORMACION DEL CLIENTE
        Intent vistaClienteActivity = new Intent(this, VistaClienteActivity.class);
        startActivity(vistaClienteActivity);
    }

    /**
     * METODO PARA EL ONCLICK
     *
     * @param v
     */
    private void infocliente2(View v) {
        onClick2(v);
    }

    /**
     * METODO, ONCLICK PARA VER EL CLIENTE LO GUARDA Y DIRIGE
     *
     * @param v
     */
    public void onClick2(View v) {


        v.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
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
            editor1.putBoolean("estado_Ruta", true);
            editor1.remove("estado_Ruta");
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
            editor1.putBoolean("estado_FinalPrincipal", true);
            editor1.remove("estado_FinalPrincipal");
            editor1.putBoolean("estado_MetodosDePagoPendientes", true);
            editor1.remove("estado_MetodosDePagoPendientes");
            editor1.commit();
        }else  if (anticipo != null) {
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
            editor1.putBoolean("estado_Ruta", true);
            editor1.remove("estado_Ruta");
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
            editor1.putBoolean("estado_FinalPrincipal", true);
            editor1.remove("estado_FinalPrincipal");
            editor1.putBoolean("estado_MetodosDePagoPendientes", true);
            editor1.remove("estado_MetodosDePagoPendientes");
            editor1.commit();
        }



        ClienteSincronizado clientes = new ClienteSincronizado();
        // ACCION DE ABRIR VISTA NUEVA CUANDO SE SELECCIONA LA CARD DEL CLIENTE
        int position = rvListaClientesSincronizados.getChildAdapterPosition(v);
        // 1. SE OBTIENE EL OBJETO DEL CLIENTE
        Dias usuarioSeleccionado = listaDias.get(position);
        guardarVista();
        // 2. SE ALMACENA EL CLIENTE SELECCIONADO
        Gson gson = new Gson();
        String jsonStringObject = gson.toJson(usuarioSeleccionado);
        PreferencesClienteSeleccionado.guardarClienteSeleccionado(this, jsonStringObject);

        // 3. SE MUESTRA LA VISTA PRINCIPAL CON LA INFORMACION DEL CLIENTE
        Intent vistaClienteActivity = new Intent(this, VistaClienteActivity.class);

        startActivity(vistaClienteActivity);
    }

    public void onClickPendientes(View v){

        progressDoalog = new ProgressDialog(RutaActivity.this);

        v.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 600);

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(RutaActivity.this);
        Usuario usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

        guardarVistaPendientes();
        Intent vistaClienteActivity = new Intent(this, PendientesActivity.class);
        PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(RutaActivity.this);
        PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(RutaActivity.this);
        PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(this);
        startActivity(vistaClienteActivity);

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

            }

        } catch (Exception exception) {
            System.out.println("Error en el repSync en el modulo Metodos de pago" + exception);
        }

    }

    private void descargarInfo(boolean ok, String respuestaServer, String msg) {

        if(progressDoalog != null)
            progressDoalog.cancel();

        guardarVistaPendientes();
        Intent vistaClienteActivity = new Intent(this, PendientesActivity.class);
        PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(RutaActivity.this);
        PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(RutaActivity.this);
        PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(this);
        startActivity(vistaClienteActivity);

    }

    /**
     * METODO PARA SELECCIONAR EL DIA DE LA SEMANA Y MOSRTAR SU LISTA
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        if (selectedItem.equals("Lunes")) {
            Context context = getApplicationContext();
            CharSequence text = "LUNES!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }

    /**
     * METODO PREDETERMINADO POR SI NO SE SELECCIONA,NO ELIMINAR
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * ACCION ASYNTASK PARA CONFIGURARVISTA
     */
    private class cargarRuteroAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            configurarVista();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ProgressView.getInstance().Dismiss();
        }
    }
}