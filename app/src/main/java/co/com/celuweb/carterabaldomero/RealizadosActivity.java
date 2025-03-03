package co.com.celuweb.carterabaldomero;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import Adapters.AdapterListaClientes;
import Adapters.AdapterRecibosAnulados;
import Adapters.AdapterSeleccionFacRealizados;
import Adapters.AdaptersRecibosRealizados;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import businessObject.DataBaseBO;
import dataobject.Anticipo;
import dataobject.Cartera;
import dataobject.ClienteSincronizado;
import dataobject.Facturas;
import dataobject.FacturasRealizadas;
import dataobject.FormaPago;
import dataobject.Lenguaje;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Utilidades;

public class RealizadosActivity extends AppCompatActivity implements AdaptersRecibosRealizados.facturaCarteraRealizados {

    private RecyclerView rvFacturasRealizadas;
    private List<FacturasRealizadas> listaFacturasRealizadas;
    private List<FacturasRealizadas> listaFacturasRealizadasBusqueda;
    private Context contexto;
    private FormaPago formaPago;
    private ClienteSincronizado clienteSel;
    private Anticipo anticipo;
    List<FacturasRealizadas> listaFacturas2;
    private EditText etParametroBusqueda;
    private TextView tvRangoFecha;
    private ImageView ImgBusfecha;
    private EditText etFechaIncialSAP;
    private String parametroComparar="";
    private String empresa = "";
    private EditText etFechaFinSAP;
    private Lenguaje lenguajeElegido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizados);

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(this);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);


        etFechaFinSAP = findViewById(R.id.etFechaFinalSAP);
        etFechaIncialSAP = findViewById(R.id.etFechaIncialSAP);
        tvRangoFecha = findViewById(R.id.tvRangoFecha);
        ImgBusfecha = findViewById(R.id.ImgBusfecha);

        listaFacturasRealizadas = new ArrayList<>();
        listaFacturasRealizadas = DataBaseBO.cargarFacturasRealizadas();

        TextView tituloFacturasRealizadas = findViewById(R.id.tituloFacturasRealizadas);

        rvFacturasRealizadas = findViewById(R.id.rvFacturasRealizadas);
        rvFacturasRealizadas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdaptersRecibosRealizados adaptersRecibosRealizados = new AdaptersRecibosRealizados(listaFacturasRealizadas,RealizadosActivity.this);
        rvFacturasRealizadas.setAdapter(adaptersRecibosRealizados);

        etParametroBusqueda = findViewById(R.id.etParametroBusqueda2);
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

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Payments Collected"));

                tvRangoFecha.setText("Date range");
                tituloFacturasRealizadas.setText("Payments Collected");
                etParametroBusqueda.setHint("Search Filter");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Facturas Realizadas"));

            }
        }


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
                DatePickerDialog recogerFecha = new DatePickerDialog(RealizadosActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        empresa = DataBaseBO.cargarEmpresa();
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
                DatePickerDialog recogerFecha = new DatePickerDialog(RealizadosActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        empresa = DataBaseBO.cargarEmpresa();
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

        empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AGUC")) {
            etFechaIncialSAP.setHint("mm/dd/yyyy");
            etFechaFinSAP.setHint("mm/dd/yyyy");
        }

    }

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

    @Override
    public void onBackPressed() {

        eliminarEstado();
        Intent vistaClienteActivity = new Intent(this, InformesActivity.class);
        startActivity(vistaClienteActivity);



    }

    public void BuscarClientesParametroRuta(String parametroBusqueda) {
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
        busquedaListaClientes(parametroBusqueda);

    }

    private void busquedaListaClientes(String parametroBusqueda) {
        final Vector<String> listaItems = new Vector<>();
        parametroBusqueda = etParametroBusqueda.getText().toString();

        listaFacturasRealizadasBusqueda = DataBaseBO.cargarClientesBusquedaFacRealizadas(parametroBusqueda);

        String numeroRecibo="";
        for (FacturasRealizadas fac:listaFacturasRealizadasBusqueda) {
            numeroRecibo = fac.numeroRecibo;
        }


        if (numeroRecibo == null) {
        //    Toasty.warning(this, "No se encontraron registros con ese parametro de busqueda...", Toasty.LENGTH_SHORT).show();

        }else if (numeroRecibo != null) {
            rvFacturasRealizadas = findViewById(R.id.rvFacturasRealizadas);
            rvFacturasRealizadas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            AdaptersRecibosRealizados adaptersRecibosRealizados = new AdaptersRecibosRealizados(listaFacturasRealizadasBusqueda,RealizadosActivity.this);
            rvFacturasRealizadas.setAdapter(adaptersRecibosRealizados);
        }



    }

    /**
     * METODO ONCLICK PARA LOS RANGOS DE FECHA
     * @param view
     */
    public void onClickbuscarFechasRealizados(View view) throws ParseException {

        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        final String inicial = etFechaIncialSAP.getText().toString();
        final String fin = etFechaFinSAP.getText().toString();

        empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;
        SimpleDateFormat date = null;

        if (finalEmpresa.equals("AGUC"))
            date = new SimpleDateFormat("MM-dd-yyyy");
        else
            date = new SimpleDateFormat("yyyy-MM-dd");

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

    public void onClickbuscarFACPORCODIGO(View view) throws ParseException {

        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);

        parametroComparar = etParametroBusqueda.getText().toString();

        listaFacturasRealizadasBusqueda = DataBaseBO.cargarClientesBusquedaFacRealizadas(parametroComparar);

        if (listaFacturasRealizadasBusqueda.size() == 0) {
            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {



                    Toasty.warning(this, "The invoice was not found..", Toasty.LENGTH_SHORT).show();


                } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                    Toasty.warning(this, "No se encontro la factura..", Toasty.LENGTH_SHORT).show();


                }
            }
        }else{
            String numeroRecibo="";
            for (FacturasRealizadas fac:listaFacturasRealizadasBusqueda) {
                numeroRecibo = fac.numeroRecibo;
            }


            if (numeroRecibo == null) {
                //    Toasty.warning(this, "No se encontraron registros con ese parametro de busqueda...", Toasty.LENGTH_SHORT).show();

            }else if (numeroRecibo != null) {
                rvFacturasRealizadas = findViewById(R.id.rvFacturasRealizadas);
                rvFacturasRealizadas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                AdaptersRecibosRealizados adaptersRecibosRealizados = new AdaptersRecibosRealizados(listaFacturasRealizadasBusqueda,RealizadosActivity.this);
                rvFacturasRealizadas.setAdapter(adaptersRecibosRealizados);
            }
        }


    }

    private void fechas(String inicial, String fin) {

        inicial = etFechaIncialSAP.getText().toString().replace("/","-");
        fin = etFechaFinSAP.getText().toString().replace("/","-");

        rvFacturasRealizadas = findViewById(R.id.rvFacturasRealizadas);
        rvFacturasRealizadas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        listaFacturasRealizadas = DataBaseBO.cargarFechaCarteraRealizados(inicial, fin);

        rvFacturasRealizadas = findViewById(R.id.rvFacturasRealizadas);
        rvFacturasRealizadas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdaptersRecibosRealizados adaptersRecibosRealizados = new AdaptersRecibosRealizados(listaFacturasRealizadas,RealizadosActivity.this);
        rvFacturasRealizadas.setAdapter(adaptersRecibosRealizados);
        adaptersRecibosRealizados.notifyDataSetChanged();

    }


    @Override
    public Serializable facturaCarteraRealizados(List<FacturasRealizadas> facturasRealizadas) {
        return null;
    }

    private void eliminarEstado() {

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_FacturasSeleccRealizadas", true);
        editor1.remove("estado_FacturasSeleccRealizadas");
        editor1.commit();
        finish();


    }

}