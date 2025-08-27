package co.com.celuweb.carterabaldomero;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import Adapters.AdapterFacturasPendientes;
import Adapters.AdapterFacturasSeleccionMultiplePendientes;
import Adapters.AdaptersRecibosPendientes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import businessObject.DataBaseBO;
import dataobject.Cartera;
import dataobject.Lenguaje;
import dataobject.MultiplesEstado;
import dataobject.Pendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import utilidades.Utilidades;

public class FacturasSeleccionadasPendientesActivity extends AppCompatActivity implements AdaptersRecibosPendientes.facturaCarteraPendientes {

    private List<String> numeroRecibos;
    private TextView txtCantidadEfec, txtCantidadCheques, txtCantidadEntregada, txtValorEntregado, txtValorCheques, txtValorEfectivo;
    private TextView txtTituloNumRecibo, txtTituloNombreComercial, txtTituloCancelado, txtTituloMetodoPago, txtTituloResumen, txtTituloValor,txtTituloCantidad;
    private Lenguaje lenguajeElegido;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_facturas_seleccion_multiple_pendientes);


        txtCantidadEfec = findViewById(R.id.txtCantidadEfec);
        txtCantidadCheques = findViewById(R.id.txtCantidadCheques);
        txtCantidadEntregada = findViewById(R.id.txtCantidadEntregada);

        txtValorEntregado = findViewById(R.id.txtValorEntregado);
        txtValorEfectivo = findViewById(R.id.txtValorEfectivo);
        txtValorCheques = findViewById(R.id.txtValorCheques);

        txtTituloNumRecibo = findViewById(R.id.txt);
        txtTituloNombreComercial = findViewById(R.id.txt1);
        txtTituloCancelado = findViewById(R.id.txt2);
        txtTituloMetodoPago = findViewById(R.id.txt4);

        txtTituloResumen = findViewById(R.id.txtinfo);
        txtTituloValor = findViewById(R.id.txt2info);
        txtTituloCantidad = findViewById(R.id.txt4info);


        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObject = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(FacturasSeleccionadasPendientesActivity.this);
        final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(FacturasSeleccionadasPendientesActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Selected payments outstandig"));

                txtTituloNumRecibo.setText("Num Receipt");
                txtTituloNombreComercial.setText("Nam Commercial");
                txtTituloCancelado.setText("Cancelled");
                txtTituloMetodoPago.setText("Met. Pay");

                txtTituloResumen.setText("Summary");
                txtTituloValor.setText("Amount");
                txtTituloCantidad.setText("Quantity");


            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Facturas Seleccionadas Pendientes"));

            }
        }


        if (facCollection != null) {

            RecyclerView rvListaFacturasPendientes = findViewById(R.id.rvListaCarteraFacturasSelecPendientesMultiple);
            rvListaFacturasPendientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            final AdapterFacturasSeleccionMultiplePendientes adapter = new AdapterFacturasSeleccionMultiplePendientes((List<Pendientes>) facCollection, FacturasSeleccionadasPendientesActivity.this);
            rvListaFacturasPendientes.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

        String numeroRecibo = "";
        numeroRecibos = new ArrayList<>();
        double precioTotal = 0;


        for (Pendientes fac : facCollection) {
            numeroRecibo = fac.getNumeroRecibo();
            numeroRecibos.add(numeroRecibo);
            precioTotal += fac.getMontoPendientes();



        }


        int cuantasEfectivo = DataBaseBO.cuantasEfectivo(numeroRecibos, FacturasSeleccionadasPendientesActivity.this);
        int cuantasCheque = DataBaseBO.cuantasCheque(numeroRecibos, FacturasSeleccionadasPendientesActivity.this);

        double valorEfectivo = DataBaseBO.TotalFormasPagoPendientesEfectivo(numeroRecibos, FacturasSeleccionadasPendientesActivity.this);
        double valorCheques = DataBaseBO.TotalFormasPagoPendientesChequesData(numeroRecibos, FacturasSeleccionadasPendientesActivity.this);

        txtCantidadEfec.setText(String.valueOf(cuantasEfectivo));
        txtCantidadCheques.setText(String.valueOf(cuantasCheque));
        txtCantidadEntregada.setText(String.valueOf(cuantasCheque + cuantasEfectivo));


        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa(FacturasSeleccionadasPendientesActivity.this);
        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
            txtValorEntregado.setText(formatoNumero.format(valorEfectivo+valorCheques));
            txtValorEfectivo.setText(formatoNumero.format(valorEfectivo));
            txtValorCheques.setText(formatoNumero.format(valorCheques));


        } else {

            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
            txtValorEntregado.setText(formatoNumero.format(valorEfectivo+valorCheques));
            txtValorEfectivo.setText(formatoNumero.format(valorEfectivo));
            txtValorCheques.setText(formatoNumero.format(valorCheques));


        }


        MultiplesEstado multiplesEstado = new MultiplesEstado();

        multiplesEstado.total = precioTotal;

        Gson gson33 = new Gson();
        String jsonStringObject = gson33.toJson(multiplesEstado);
        PreferencesFacturasMultiplesPendientes.guardarFacturasMultiplesPendientesSeleccionado(this, jsonStringObject);


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
        switch (item.getItemId()) {

            case R.id.menu_forward:

                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);



                onClicksiguiente();
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

    private void onClicksiguiente() {
        guardarVista();
        Intent vistaClienteActivity = new Intent(this, MetodosDePagoPendientesActivity.class);
        startActivity(vistaClienteActivity);
    }

    @Override
    public void onBackPressed() {


        Intent vistaClienteActivity = new Intent(this, PendientesActivity.class);
        startActivity(vistaClienteActivity);
        finish();
        super.onBackPressed();
    }

    /**
     * SHARED PREFERENCE VISTA
     */
    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FacturasSeleccMultiplesPendientes", estado);
        editor1.commit();
    }

    @Override
    public Serializable facturaCartera(List<Pendientes> pendientes) {
        return null;
    }
}
