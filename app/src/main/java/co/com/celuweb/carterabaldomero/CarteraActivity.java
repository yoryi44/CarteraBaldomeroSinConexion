package co.com.celuweb.carterabaldomero;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import Adapters.AdapterCartera;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import businessObject.DataBaseBO;
import dataobject.Cartera;
import dataobject.ClienteSincronizado;
import dataobject.Lenguaje;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Alert;
import utilidades.Utilidades;

public class CarteraActivity extends AppCompatActivity implements AdapterCartera.facturaCartera {

    private EditText etParametroBusqueda;
    private List<Cartera> listaCartera;
    private RecyclerView rvListaCartera;
    private TextView tvRangoFecha,titulotvFacturas;
    private ImageView ImgBusfecha;
    private Vector<Cartera> listaParametosSpinner;
    Activity context;
    private EditText etFechaIncialSAP;
    private EditText etFechaFinSAP;
    private ClienteSincronizado clienteSel;
    private Lenguaje lenguajeElegido;

    private static final String DEBUG_TAG = "Velocity";

    private VelocityTracker mVelocityTracker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cartera_activity);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(CarteraActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        listaCartera = new ArrayList<>();
        List<Cartera> listaDias = new ArrayList<>();

        final Spinner spinner = findViewById(R.id.spinnerFiltroCartera);

        final Spinner spinner2 = findViewById(R.id.spinnerFiltroTipo);
        spinner2.setVisibility(View.INVISIBLE);

        etFechaFinSAP = findViewById(R.id.etFechaFinalSAP);
        etFechaIncialSAP = findViewById(R.id.etFechaIncialSAP);
        tvRangoFecha = findViewById(R.id.tvRangoFecha);
        titulotvFacturas = findViewById(R.id.titulotvFacturas);
        ImgBusfecha = findViewById(R.id.ImgBusfecha);

        etFechaIncialSAP.setVisibility(View.GONE);
        etFechaFinSAP.setVisibility(View.GONE);
        tvRangoFecha.setVisibility(View.GONE);
        ImgBusfecha.setVisibility(View.GONE);
        etParametroBusqueda = findViewById(R.id.etParametroBusqueda2);

        String empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AGUC")) {
            etFechaIncialSAP.setHint("mm/dd/yyyy");
            etFechaFinSAP.setHint("mm/dd/yyyy");
        }

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Accounts Summary"));


                final ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.CARTERA_INGLES, R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter2);
                spinner.setVisibility(View.VISIBLE);
                titulotvFacturas.setText("Legal Documents");
                tvRangoFecha.setText("Date Range");

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int ipos, long l) {
                        String parametroBusqueda = spinner.getSelectedItem().toString();

                        switch (ipos) {
                            /**
                             * Spinner seleccion Selecion desde 0
                             */

                            case 0:

                                etFechaIncialSAP.setVisibility(View.GONE);
                                etFechaFinSAP.setVisibility(View.GONE);
                                tvRangoFecha.setVisibility(View.GONE);
                                ImgBusfecha.setVisibility(View.GONE);
                                buscarInicio();
                                adapter2.notifyDataSetChanged();
                                spinner2.setVisibility(View.INVISIBLE);
                                break;


                            /**
                             * Spinner seleccion Fechavecto
                             */
                            case 2:
                                ImgBusfecha.setVisibility(View.VISIBLE);
                                rvListaCartera = findViewById(R.id.rvListaCartera);
                                rvListaCartera.setLayoutManager(new LinearLayoutManager(CarteraActivity.this, LinearLayoutManager.VERTICAL, false));

                                rvListaCartera = findViewById(R.id.rvListaCartera);
                                rvListaCartera.setLayoutManager(new LinearLayoutManager(CarteraActivity.this, LinearLayoutManager.VERTICAL, false));
                                final AdapterCartera adapter = new AdapterCartera(listaCartera, CarteraActivity.this);
                                rvListaCartera.setAdapter(adapter);


                                listaCartera.clear();

                                etFechaIncialSAP.setVisibility(View.VISIBLE);
                                etFechaFinSAP.setVisibility(View.VISIBLE);
                                tvRangoFecha.setVisibility(View.VISIBLE);
                                spinner2.setVisibility(View.INVISIBLE);

                                //Calendario para obtener fecha
                                final String CERO = "0";
                                final String BARRA = "-";
                                final Calendar c = Calendar.getInstance();

                                //Fecha
                                final int mes = c.get(Calendar.MONTH);
                                final int dia = c.get(Calendar.DAY_OF_MONTH);
                                final int anio = c.get(Calendar.YEAR);

                                etFechaIncialSAP.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatePickerDialog recogerFecha = new DatePickerDialog(CarteraActivity.this, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                                                final int mesActual = month + 1;
                                                //Formateo el día obtenido: antepone el 0 si son menores de 10
                                                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                                                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                                                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                                                //Muestro la fecha con el formato deseado
                                                String empresa = DataBaseBO.cargarEmpresa();
                                                final String finalEmpresa = empresa;

                                                if (finalEmpresa.equals("AGUC"))
                                                    etFechaIncialSAP.setText(mesFormateado + BARRA + diaFormateado + BARRA + year);
                                                else
                                                    etFechaIncialSAP.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
                                            }
                                            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                                            /**
                                             *También puede cargar los valores que usted desee
                                             */
                                        }, anio, mes, dia);
                                        //Muestro el widget
                                        recogerFecha.show();
                                    }
                                });

                                etFechaFinSAP.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatePickerDialog recogerFecha = new DatePickerDialog(CarteraActivity.this, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                                                final int mesActual = month + 1;
                                                //Formateo el día obtenido: antepone el 0 si son menores de 10
                                                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                                                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                                                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                                                //Muestro la fecha con el formato deseado
                                                String empresa = DataBaseBO.cargarEmpresa();
                                                final String finalEmpresa = empresa;

                                                if (finalEmpresa.equals("AGUC"))
                                                    etFechaFinSAP.setText(mesFormateado + BARRA + diaFormateado + BARRA + year);
                                                else
                                                    etFechaFinSAP.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
                                            }
                                            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                                            /**
                                             *También puede cargar los valores que usted desee
                                             */
                                        }, anio, mes, dia);
                                        //Muestro el widget
                                        recogerFecha.show();
                                    }
                                });


                                break;


                            /***
                             * Spinner seleccion tipo
                             */
                            case 1:

                                etFechaIncialSAP.setVisibility(View.GONE);
                                etFechaFinSAP.setVisibility(View.GONE);
                                tvRangoFecha.setVisibility(View.GONE);
                                ImgBusfecha.setVisibility(View.GONE);
                                spinner2.setVisibility(View.VISIBLE);
                                String[] items;
                                Vector<String> listaItems = new Vector<String>();

                                listaParametosSpinner = DataBaseBO.cargarTipoCartera(listaItems);

                                if (listaItems.size() > 0) {
                                    items = new String[listaItems.size()];
                                    listaItems.copyInto(items);

                                } else {
                                    items = new String[]{};

                                    if (listaParametosSpinner != null)
                                        listaParametosSpinner.removeAllElements();
                                }

                                ArrayAdapter adapter3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, items);
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner2.setAdapter(adapter3);
                                adapter3.notifyDataSetChanged();
                                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        final String parametroBusqueda2 = spinner2.getSelectedItem().toString();
                                        busquedaListaCarteraTipo(parametroBusqueda2);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                                break;
                            default:
                                break;
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }


                });

                etParametroBusqueda.setHint("Search Filter");


            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Cartera"));

                final ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.CARTERA, R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter2);
                spinner.setVisibility(View.VISIBLE);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int ipos, long l) {
                        String parametroBusqueda = spinner.getSelectedItem().toString();

                        switch (ipos) {
                            /**
                             * Spinner seleccion Selecion desde 0
                             */

                            case 0:

                                etFechaIncialSAP.setVisibility(View.GONE);
                                etFechaFinSAP.setVisibility(View.GONE);
                                tvRangoFecha.setVisibility(View.GONE);
                                ImgBusfecha.setVisibility(View.GONE);
                                buscarInicio();
                                adapter2.notifyDataSetChanged();
                                spinner2.setVisibility(View.INVISIBLE);
                                break;


                            /**
                             * Spinner seleccion Fechavecto
                             */
                            case 2:
                                ImgBusfecha.setVisibility(View.VISIBLE);
                                rvListaCartera = findViewById(R.id.rvListaCartera);
                                rvListaCartera.setLayoutManager(new LinearLayoutManager(CarteraActivity.this, LinearLayoutManager.VERTICAL, false));

                                rvListaCartera = findViewById(R.id.rvListaCartera);
                                rvListaCartera.setLayoutManager(new LinearLayoutManager(CarteraActivity.this, LinearLayoutManager.VERTICAL, false));
                                final AdapterCartera adapter = new AdapterCartera(listaCartera, CarteraActivity.this);
                                rvListaCartera.setAdapter(adapter);


                                listaCartera.clear();

                                etFechaIncialSAP.setVisibility(View.VISIBLE);
                                etFechaFinSAP.setVisibility(View.VISIBLE);
                                tvRangoFecha.setVisibility(View.VISIBLE);
                                spinner2.setVisibility(View.INVISIBLE);

                                //Calendario para obtener fecha
                                final String CERO = "0";
                                final String BARRA = "-";
                                final Calendar c = Calendar.getInstance();

                                //Fecha
                                final int mes = c.get(Calendar.MONTH);
                                final int dia = c.get(Calendar.DAY_OF_MONTH);
                                final int anio = c.get(Calendar.YEAR);

                                etFechaIncialSAP.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatePickerDialog recogerFecha = new DatePickerDialog(CarteraActivity.this, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                                                final int mesActual = month + 1;
                                                //Formateo el día obtenido: antepone el 0 si son menores de 10
                                                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                                                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                                                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                                                //Muestro la fecha con el formato deseado
                                               String empresa = DataBaseBO.cargarEmpresa();
                                                final String finalEmpresa = empresa;

                                                if (finalEmpresa.equals("AGUC"))
                                                    etFechaIncialSAP.setText(mesFormateado + BARRA + diaFormateado + BARRA + year);
                                                else
                                                    etFechaIncialSAP.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
                                            }
                                            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                                            /**
                                             *También puede cargar los valores que usted desee
                                             */
                                        }, anio, mes, dia);
                                        //Muestro el widget
                                        recogerFecha.show();
                                    }
                                });

                                etFechaFinSAP.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatePickerDialog recogerFecha = new DatePickerDialog(CarteraActivity.this, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                                                final int mesActual = month + 1;
                                                //Formateo el día obtenido: antepone el 0 si son menores de 10
                                                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                                                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                                                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                                                //Muestro la fecha con el formato deseado
                                                String empresa = DataBaseBO.cargarEmpresa();
                                                final String finalEmpresa = empresa;

                                                if (finalEmpresa.equals("AGUC"))
                                                    etFechaFinSAP.setText(mesFormateado + BARRA + diaFormateado + BARRA + year);
                                                else
                                                    etFechaFinSAP.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
                                            }
                                            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                                            /**
                                             *También puede cargar los valores que usted desee
                                             */
                                        }, anio, mes, dia);
                                        //Muestro el widget
                                        recogerFecha.show();
                                    }
                                });


                                break;


                            /***
                             * Spinner seleccion tipo
                             */
                            case 1:

                                etFechaIncialSAP.setVisibility(View.GONE);
                                etFechaFinSAP.setVisibility(View.GONE);
                                tvRangoFecha.setVisibility(View.GONE);
                                ImgBusfecha.setVisibility(View.GONE);
                                spinner2.setVisibility(View.VISIBLE);
                                String[] items;
                                Vector<String> listaItems = new Vector<String>();

                                listaParametosSpinner = DataBaseBO.cargarTipoCartera(listaItems);

                                if (listaItems.size() > 0) {
                                    items = new String[listaItems.size()];
                                    listaItems.copyInto(items);

                                } else {
                                    items = new String[]{};

                                    if (listaParametosSpinner != null)
                                        listaParametosSpinner.removeAllElements();
                                }

                                ArrayAdapter adapter3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, items);
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner2.setAdapter(adapter3);
                                adapter3.notifyDataSetChanged();
                                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        final String parametroBusqueda2 = spinner2.getSelectedItem().toString();
                                        busquedaListaCarteraTipo(parametroBusqueda2);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                                break;
                            default:
                                break;
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }


                });

            }
        }



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
                    onClickBuscarCarteraParametroRuta2(s.toString());
                } else {
                    //Limpiar la cadena ya que la busqueda de letras es  vacia.
                }
            }
        });





    }

    /**
     * METODO PARA BUSCAR POR FECHA
     *
     * @param inicial
     * @param fin
     */
    private void fechas(String inicial, String fin) {

        inicial = etFechaIncialSAP.getText().toString().replace("/","-");
        fin = etFechaFinSAP.getText().toString().replace("/","-");

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        rvListaCartera = findViewById(R.id.rvListaCartera);
        rvListaCartera.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        final Vector<String> listaItems = new Vector<>();


        String parametro = clienteSel.codigo;
        listaCartera = DataBaseBO.cargarFechaCartera(parametro, listaItems, inicial, fin);

        rvListaCartera = findViewById(R.id.rvListaCartera);
        rvListaCartera.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final AdapterCartera adapter = new AdapterCartera(listaCartera, CarteraActivity.this);
        rvListaCartera.setAdapter(adapter);
        adapter.notifyDataSetChanged();


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
        switch (item.getItemId()) {

            case R.id.menu_forward:


                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);


                onClickRecaudoDinero();
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

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Alert.vistaDialogoCerrarSesion(CarteraActivity.this, "¿If you return to cancel the collection, are you sure you want to cancel the collection?", "Cancel Collection", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        List<Cartera> carteraS = new ArrayList<>();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(CarteraActivity.this);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

                        final List<String> claseDocumento = new ArrayList<>();
                        final List<String> documentosFinanciero = new ArrayList<>();
                        List<String> codigoUs = new ArrayList<>();
                        final List<String> documentt = new ArrayList<>();
                        String claseDocument = "";
                        String documentoFinanciero = "";
                        String codigoU = "";
                        String nombreU = "";
                        double precioTotal = 0;
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


                        if (!documentt.equals("")) {
                            DataBaseBO.eliminarRecaudosTotal(documentosFinanciero);
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_Cartera", true);
                            editor1.remove("estado_Cartera");
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();

                        } else if (documentosFinanciero.equals("")) {
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_Cartera", true);
                            editor1.remove("estado_Cartera");
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();
                        }

                        Alert.dialogo.cancel();
                        finish();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alert.dialogo.cancel();

                    }
                });


            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Alert.vistaDialogoCerrarSesion(CarteraActivity.this, "¿Si regresa cancelara el recaudo,esta seguro que desea cancelar el recaudo?", "Cancelar Recaudo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        List<Cartera> carteraS = new ArrayList<>();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(CarteraActivity.this);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

                        final List<String> claseDocumento = new ArrayList<>();
                        final List<String> documentosFinanciero = new ArrayList<>();
                        List<String> codigoUs = new ArrayList<>();
                        final List<String> documentt = new ArrayList<>();
                        String claseDocument = "";
                        String documentoFinanciero = "";
                        String codigoU = "";
                        String nombreU = "";
                        double precioTotal = 0;
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


                        if (!documentt.equals("")) {
                            DataBaseBO.eliminarRecaudosTotal(documentosFinanciero);
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_Cartera", true);
                            editor1.remove("estado_Cartera");
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();

                        } else if (documentosFinanciero.equals("")) {
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_Cartera", true);
                            editor1.remove("estado_Cartera");
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();
                        }

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

    /**
     * METODO OCLICK PARA LA LUPA DEL BUSCAR
     *
     * @param view
     */
    public void onClickbuscarParaFac(View view) {

        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        String parametroBusqueda;
        parametroBusqueda = etParametroBusqueda.getText().toString();
        onClickBuscarCarteraParametroRuta2(parametroBusqueda);


    }

    /**
     * METODO ONCLICK PARA LOS RANGOS DE FECHA
     *
     * @param view
     */
    public void onClickbuscarFechas(View view) throws ParseException {

        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        final String inicial = etFechaIncialSAP.getText().toString();
        final String fin = etFechaFinSAP.getText().toString();

        String empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;
        SimpleDateFormat date = null;

        if (finalEmpresa.equals("AGUC"))
            date = new SimpleDateFormat("MM-dd-yyyy");
        else
            date = new SimpleDateFormat("yyyy-MM-dd");

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                if (fin.equals("")) {
                    Toasty.warning(this, "The final date cannot be blank..", Toasty.LENGTH_SHORT).show();
                }
                if (inicial.equals("")) {
                    Toasty.warning(this, "The starting date cannot be blank..", Toasty.LENGTH_SHORT).show();
                }
                if (!fin.equals("") && !inicial.equals("")) {


                    Date fechaInicial = date.parse(inicial);
                    Date fechaFinal = date.parse(fin);

                    if (fechaInicial.after(fechaFinal)) {

                        Toasty.warning(this, "The starting date cannot be greater than the end date..", Toasty.LENGTH_SHORT).show();

                    }else if (fechaFinal.before(fechaInicial)) {

                        Toasty.warning(this, "The final date cannot be less than the Initial date..", Toasty.LENGTH_SHORT).show();


                    }else{
                        fechas(inicial, fin);
                    }

                }


            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                if (fin.equals("")) {
                    Toasty.warning(this, "La fecha final no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                }
                if (inicial.equals("")) {
                    Toasty.warning(this, "La fecha inicial no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                }
                if (!fin.equals("") && !inicial.equals("")) {


                    Date fechaInicial = date.parse(inicial);
                    Date fechaFinal = date.parse(fin);

                    if (fechaInicial.after(fechaFinal)) {

                        Toasty.warning(this, "La fecha inicial no puede ser mayor a la fecha final..", Toasty.LENGTH_SHORT).show();

                    }else if (fechaFinal.before(fechaInicial)) {

                        Toasty.warning(this, "La fecha final no puede ser menor a la fecha Inicial..", Toasty.LENGTH_SHORT).show();


                    }else{
                        fechas(inicial, fin);
                    }

                }


            }
        }


    }

    /**
     * METODO, BUSCA POR TIPO
     *
     * @param parametroBusqueda
     */
    private void busquedaListaCarteraTipo(String parametroBusqueda) {

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        rvListaCartera = findViewById(R.id.rvListaCartera);
        rvListaCartera.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        final Vector<String> listaItems = new Vector<>();
        final Spinner spinner2 = findViewById(R.id.spinnerFiltroTipo);
        parametroBusqueda = spinner2.getSelectedItem().toString();

        String parametro = clienteSel.codigo;
        listaCartera = DataBaseBO.cargarCarteraTipoParametroBusqueda(parametroBusqueda, parametro, listaItems);

        rvListaCartera = findViewById(R.id.rvListaCartera);
        rvListaCartera.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterCartera adapter = new AdapterCartera(listaCartera, CarteraActivity.this);
        rvListaCartera.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rvListaCartera.setHasFixedSize(true);
        rvListaCartera.setItemViewCacheSize(20);
        rvListaCartera.setDrawingCacheEnabled(true);
        rvListaCartera.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        rvListaCartera.getLayoutManager().smoothScrollToPosition(rvListaCartera, new RecyclerView.State(), 0);


    }

    /**
     * Metodo para el textWatcher("BUSCAR AL ESCRIBIR")
     *
     * @param parametroBusqueda
     */
    private void busquedaListaCartera(String parametroBusqueda) {

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        final Vector<String> listaItems = new Vector<>();
        parametroBusqueda = etParametroBusqueda.getText().toString();
        String parametro = clienteSel.codigo;
        listaCartera = DataBaseBO.cargarCarteraParametroBusqueda(parametroBusqueda, parametro, listaItems);

        rvListaCartera = findViewById(R.id.rvListaCartera);
        rvListaCartera.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final AdapterCartera adapter = new AdapterCartera(listaCartera, CarteraActivity.this);
        rvListaCartera.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    /**
     * METODO, MUESTRA LA CARTERA COMPLETA AL INICIO
     */
    @SuppressLint("WrongConstant")
    private void buscarInicio() {

        CheckBox cbCarteraFactura = findViewById(R.id.cbCarteraFactura);
        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        listaCartera = new ArrayList<>();
        rvListaCartera = findViewById(R.id.rvListaCartera);
        Vector<String> listaItems = new Vector<>();
        String parametro = clienteSel.codigo;
        listaCartera = DataBaseBO.cargarCarteraCompleta(parametro, listaItems);
        rvListaCartera.setVisibility(View.VISIBLE);

        AdapterCartera adapter3 = new AdapterCartera(listaCartera, CarteraActivity.this);
        rvListaCartera.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        rvListaCartera.setAdapter(adapter3);

        rvListaCartera.setHasFixedSize(true);
        rvListaCartera.setItemViewCacheSize(50);
        rvListaCartera.setDrawingCacheEnabled(true);
        rvListaCartera.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        rvListaCartera.getLayoutManager().smoothScrollToPosition(rvListaCartera, new RecyclerView.State(), 0);
        adapter3.notifyDataSetChanged();



    }

    /**
     * METODO ONCLICK PARA BUSCAR EN CARTERA
     *
     * @param parametroBusqueda
     */
    public void onClickBuscarCarteraParametroRuta2(String parametroBusqueda) {
        // 1. PARAMETRO DE BUSQUEDA
        parametroBusqueda = etParametroBusqueda.getText().toString();

        if (parametroBusqueda.length() == 0) {

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
        busquedaListaCartera(parametroBusqueda);

    }

    /**
     * METODO ONCLICK PARA VER LAS FACTURAS SELECCIONADAS
     *
     * @param
     */
    public void onClickRecaudoDinero() {
        activityformapago();
    }

    /**
     * METODO,OBTIENE LA CARTERA SELECCIONADA, VISTA FACTURASSELECCIOANADAS
     */
    private void activityformapago() {

        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(CarteraActivity.this);
        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        double valorFac =0;



        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                if (stringJsonObject != null) {
                    if (!stringJsonObject.equals("")) {

                        for (Cartera car:facCollection) {

                            valorFac += car.getSaldo();
                        }

                        if (valorFac < 0) {
                            Toasty.warning(this, "The total invoice balance is negative..", Toasty.LENGTH_SHORT).show();

                        }else if (valorFac > 0) {

                            guardarVista();
                            Intent vistaClienteActivity = new Intent(this, FacturasSeleccionadasActivity.class);
                            startActivity(vistaClienteActivity);
                            finish();

                        }


                    } else if (stringJsonObject.equals("[]")) {
                        Toasty.warning(this, "No invoice has been selected..", Toasty.LENGTH_SHORT).show();

                    } else if (stringJsonObject.isEmpty()) {
                        Toasty.warning(this, "No invoice has been selected...", Toasty.LENGTH_SHORT).show();

                    }
                }else {
                    Toasty.warning(this, "No invoice has been selected...", Toasty.LENGTH_SHORT).show();
                }


            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                if (stringJsonObject != null) {
                    if (!stringJsonObject.equals("")) {

                        for (Cartera car:facCollection) {

                            valorFac += car.getSaldo();
                        }

                        if (valorFac < 0) {
                            Toasty.warning(this, "El total del saldo de la factura es negativo..", Toasty.LENGTH_SHORT).show();

                        }else if (valorFac > 0) {

                            guardarVista();
                            Intent vistaClienteActivity = new Intent(this, FacturasSeleccionadasActivity.class);
                            startActivity(vistaClienteActivity);
                            finish();

                        }


                    } else if (stringJsonObject.equals("[]")) {
                        Toasty.warning(this, "No se ha seleccionado una factura..", Toasty.LENGTH_SHORT).show();

                    } else if (stringJsonObject.isEmpty()) {
                        Toasty.warning(this, "No se ha seleccionado una factura..", Toasty.LENGTH_SHORT).show();

                    }
                }else {
                    Toasty.warning(this, "No se ha seleccionado una factura..", Toasty.LENGTH_SHORT).show();
                }


            }
        }


    }

    /**
     * INTERFACE LIST<CARTERA> CARTERA
     *
     * @param cartera
     * @return
     */
    @Override
    public Serializable facturaCartera(List<Cartera> cartera) {
        try {
            Gson gson = new Gson();
            String jsonStringObject = gson.toJson(cartera);
            PreferencesCartera.guardarCarteraSeleccionada(this, jsonStringObject);

        } catch (Exception e) {
            System.out.println("error serializable" + e);
        }
        return null;
    }



    /**
     * METODO PARA GUARDAR EL ESTADO DE LA VISTA
     */
    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FacturasSeleccionadas", estado);
        editor1.commit();
    }


}
