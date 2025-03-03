package co.com.celuweb.carterabaldomero;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import Adapters.AdaptersRecibosPendientes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import dataobject.Lenguaje;
import dataobject.Pendientes;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Utilidades;

public class PendientesActivity extends AppCompatActivity implements AdaptersRecibosPendientes.facturaCarteraPendientes {

    private RecyclerView rvListaCarteraFacturasPendientes;
    private List<Pendientes> listaFacturasPendientes;
    private List<Pendientes> listaSumas;
    private List<Pendientes> listaFacturasPendientesAnulacion;
    private List<Pendientes> cargarFacturasPendientesCompleta;
    String numeroRecibo = "";
    private Lenguaje lenguajeElegido;
    String empresa;
    TextView textNumRec,textCodCli,textFechaCrea,textFechaCierre,textMonto,textStatus,txtImprimir,textSeleccion,textMotivosAnula;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendientes);

        empresa = DataBaseBO.cargarEmpresa();

        textNumRec = findViewById(R.id.txtNumRe);
        textCodCli = findViewById(R.id.txtCodCli);
        textFechaCrea = findViewById(R.id.txtFechCrea);
        textFechaCierre = findViewById(R.id.txtFechCie);
        textMonto = findViewById(R.id.txtMont);
        textStatus = findViewById(R.id.txtSta);
        textSeleccion = findViewById(R.id.txtSeleccion);
        txtImprimir = findViewById(R.id.txtImprimir);
        textMotivosAnula = findViewById(R.id.txtMotivosAnula);

        if(empresa.equals("ADHB"))
        {
            txtImprimir.setVisibility(View.VISIBLE);
        }


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(PendientesActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Pending Deposits"));

                textNumRec.setText("Consecutive  ");
                textCodCli.setText("Customer Code ");
                textFechaCrea.setText("Creation Date ");
                textFechaCierre.setText("Dateline         ");
                textMonto.setText("Amount");
                textStatus.setText("Status");
                textSeleccion.setText("Selection");
                txtImprimir.setText("Print");
                textMotivosAnula.setText("Grounds for Annulment");

            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Recaudos Pendientes"));

            }
        }


        listaFacturasPendientes = DataBaseBO.cargarFacturasPendientes();
        /// List<Pendientes> sumaPagos = DataBaseBO.cargarSumasPendientesViaPago();

        listaFacturasPendientesAnulacion = DataBaseBO.cargarFacturasPendientesAnulacion();
        if (listaFacturasPendientesAnulacion.size() > 0) {
            for (Pendientes pen : listaFacturasPendientesAnulacion) {

                numeroRecibo = pen.getNumeroAnulacion();

            }
            listaFacturasPendientes.remove(numeroRecibo);

        }


        rvListaCarteraFacturasPendientes = findViewById(R.id.rvListaCarteraFacturasPendientes);
        rvListaCarteraFacturasPendientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdaptersRecibosPendientes adaptersRecibosPendientes = new AdaptersRecibosPendientes(listaFacturasPendientes, PendientesActivity.this);
        rvListaCarteraFacturasPendientes.setAdapter(adaptersRecibosPendientes);
        adaptersRecibosPendientes.notifyDataSetChanged();

        rvListaCarteraFacturasPendientes.setHasFixedSize(true);
        rvListaCarteraFacturasPendientes.setItemViewCacheSize(20);
        rvListaCarteraFacturasPendientes.setDrawingCacheEnabled(true);
        rvListaCarteraFacturasPendientes.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        rvListaCarteraFacturasPendientes.getLayoutManager().smoothScrollToPosition(rvListaCarteraFacturasPendientes, new RecyclerView.State(), 0);



        PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(this);
        //  TextView txtSaldoTotalFacturasPendientes = findViewById(R.id.txtSaldoTotalFacturasPendientes);

        double precioTotal = 0;
        String numeroRecibo = "";

        if (listaFacturasPendientes != null) {

            for (Pendientes cartera1 : listaFacturasPendientes) {
                numeroRecibo = cartera1.getNumeroRecibo();
                precioTotal += cartera1.getMontoPendientes();
            }
        }


        //    txtSaldoTotalFacturasPendientes.setText(Utilidades.separarMilesSinDecimal(String.valueOf(precioTotal)));


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
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

        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("ADHB") || finalEmpresa.equals("AGUC") || finalEmpresa.equals("AGSC")  || finalEmpresa.equals("AGGC")
                || finalEmpresa.equals("AGDP")  || finalEmpresa.equals("AABR")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH")) {
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
        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("ADHB") || finalEmpresa.equals("AGUC") || finalEmpresa.equals("AGSC")  || finalEmpresa.equals("AGGC")
                || finalEmpresa.equals("AGDP")  || finalEmpresa.equals("AABR")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_fw, menu);
        }

        return true;
    }

    public void onClickRecaudoDinero() {
        activityformapago();
    }

    /**
     * METODO,OBTIENE LA CARTERA SELECCIONADA, VISTA FACTURASSELECCIOANADAS
     */
    private void activityformapago() {


        Gson gsonPen = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(PendientesActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);

        if (facCollectionPendientes == null) {
            Toasty.warning(this, "No se han Seleccionado Facturas....", Toasty.LENGTH_SHORT).show();
        }else if (facCollectionPendientes != null) {
            if (facCollectionPendientes.size()>1) {
                Intent vistaClienteActivity = new Intent(this, FacturasSeleccionadasPendientesActivity.class);
                startActivity(vistaClienteActivity);
                finish();
            }
        }


    }

    @Override
    public void onBackPressed() {
        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_Pendientes", true);
        editor1.remove("estado_Pendientes");

        editor1.commit();

        Intent vistaClienteActivity = new Intent(this, RutaActivity.class);

        startActivity(vistaClienteActivity);



    }

    @Override
    public Serializable facturaCartera(List<Pendientes> pendientes) {
        return null;
    }

    private void eliminarEstado() {

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_MetodosDePagoPendientes", true);
        editor1.remove("estado_MetodosDePagoPendientes");
        editor1.commit();
        finish();


    }


}
