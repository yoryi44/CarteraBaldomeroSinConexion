package co.com.celuweb.carterabaldomero;

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

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import Adapters.AdapterFacturasSeleccionMultiple;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import businessObject.DataBaseBO;
import dataobject.Cartera;
import dataobject.Lenguaje;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesCarteraFactura;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Alert;
import utilidades.Utilidades;

public class FacturasSeleccionadasActivity extends AppCompatActivity implements AdapterFacturasSeleccionMultiple.facturaCartera {

    private Lenguaje lenguajeElegido;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturas_seleccionadas);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(FacturasSeleccionadasActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        TextView numdoc = findViewById(R.id.txt);
        TextView tipo = findViewById(R.id.txt1);
        TextView saldo = findViewById(R.id.txt2);
        TextView total = findViewById(R.id.txt3);

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Selected Documents"));
               numdoc.setText("Nu. Document");
               tipo.setText("Type");
               saldo.setText("Balance");
               total.setText("total Collection");

            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Facturas Seleccionadas"));

            }
        }



        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa(FacturasSeleccionadasActivity.this);
        final String finalEmpresa = empresa;
        TextView simboloCheq = findViewById(R.id.simboloformas1);

        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(FacturasSeleccionadasActivity.this);
        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (finalEmpresa.equals("AABR")) {
            simboloCheq.setText("$");

        }
        if (finalEmpresa.equals("ADHB")) {
            simboloCheq.setText("$");

        }
        if (finalEmpresa.equals("AGSC")) {
            simboloCheq.setText("$");

        }
        if (finalEmpresa.equals("AGGC")) {
            simboloCheq.setText("Q");
        }
        if (finalEmpresa.equals("AFPN")) {
            simboloCheq.setText("C$");

        }
        if (finalEmpresa.equals("AFPZ")) {
            simboloCheq.setText("₡");

        }
        if (finalEmpresa.equals("AGCO")) {
            simboloCheq.setText("$");

        }
        if (finalEmpresa.equals("AGAH")) {
            simboloCheq.setText("₡");

        }
        if (finalEmpresa.equals("AGDP")) {
            simboloCheq.setText("Q");


        }
        if (finalEmpresa.equals("AGUC")) {
            simboloCheq.setText("$");

        }

        if (facCollection != null) {
            Cartera cartera = new Cartera();
            RecyclerView rvListaCarteraFacturasSelec = findViewById(R.id.rvListaCarteraFacturasSelec);
            rvListaCarteraFacturasSelec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            final AdapterFacturasSeleccionMultiple adapter = new AdapterFacturasSeleccionMultiple((List<Cartera>) facCollection, FacturasSeleccionadasActivity.this);
            rvListaCarteraFacturasSelec.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


        TextView tvSaldoCarteraTotalFP = findViewById(R.id.txtSaldoTotalFacturas);

        int i = 0;
        double suma = 00.0f;
        double precioTotal = 0;

        if (facCollection != null) {

            for (Cartera cartera1 : facCollection) {
                precioTotal += cartera1.getSaldo();
            }
        }




        if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
            tvSaldoCarteraTotalFP.setText(formatoNumero.format(precioTotal));

        } else {

            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
            tvSaldoCarteraTotalFP.setText(formatoNumero.format(precioTotal));

        }




    }

    /**
     * INTERFACE double CARTERA
     * @param cartera
     * @return
     */
    @Override
    public Cartera facturaCartera(double cartera) {

        return null;
    }

    /**
     *  ONCLICK VISTA FORMAPAGO
     * @param
     */
    public void oncClicksigVentaFormaPago() {
        guardarVista();
        Intent vistaClienteActivity = new Intent(this, formaPagoActivity.class);
        startActivity(vistaClienteActivity);
        finish();

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

                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);


                oncClicksigVentaFormaPago();
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
      //  PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
        editor1.putBoolean("estado_FacturasSeleccionadas", true);
        editor1.remove("estado_FacturasSeleccionadas");
        Intent vistaClienteActivity = new Intent(getApplicationContext(), CarteraActivity.class);
        startActivity(vistaClienteActivity);
        editor1.commit();
        finish();
    }

    /**
     * METODO PARA GUARDAR EL ESTADO DE LA VISTA
     */
    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FormaPago", estado);
        editor1.commit();

    }

    /**
     * METODO PARA CANCELAR LA FORMA DE PAGO, COLLECTION FACTURAS SELECCIONADAS
     * @param view
     */
    public void cancelarFormaPago(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Alert.vistaDialogoCerrarSesion(FacturasSeleccionadasActivity.this, "¿Are you sure you want to cancel the collection?", "Cancel Collection", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        List<Cartera> carteraS = new ArrayList<>();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(FacturasSeleccionadasActivity.this);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

                        final List<String> claseDocumento = new ArrayList<>();
                        List<String> codigoUs = new ArrayList<>();
                        final List<String> documentt = new ArrayList<>();
                        String claseDocument = "";
                        String codigoU = "";
                        String nombreU="";
                        double precioTotal = 00.0f;
                        String document = "";

                        for (Cartera cartera1 : facCollection) {
                            document = cartera1.getDocumento();
                            precioTotal += cartera1.getSaldo();

                            claseDocument = cartera1.getConcepto();
                            claseDocumento.add(claseDocument);
                            documentt.add(document);

                        }


                        if (!documentt.equals("")) {
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.commit();
                            finish();
                            Intent vistaClienteActivity = new Intent(getApplicationContext(), CarteraActivity.class);
                            startActivity(vistaClienteActivity);
                            finish();
                            Alert.dialogo.cancel();

                        } else if (documentt.equals("")) {
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.commit();
                            finish();
                            Intent vistaClienteActivity = new Intent(getApplicationContext(), CarteraActivity.class);
                            startActivity(vistaClienteActivity);
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

                Alert.vistaDialogoCerrarSesion(FacturasSeleccionadasActivity.this, "¿Esta seguro que desea cancelar el recaudo?", "Cancelar Recaudo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        List<Cartera> carteraS = new ArrayList<>();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(FacturasSeleccionadasActivity.this);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

                        final List<String> claseDocumento = new ArrayList<>();
                        List<String> codigoUs = new ArrayList<>();
                        final List<String> documentt = new ArrayList<>();
                        String claseDocument = "";
                        String codigoU = "";
                        String nombreU="";
                        double precioTotal = 00.0f;
                        String document = "";

                        for (Cartera cartera1 : facCollection) {
                            document = cartera1.getDocumento();
                            precioTotal += cartera1.getSaldo();

                            claseDocument = cartera1.getConcepto();
                            claseDocumento.add(claseDocument);
                            documentt.add(document);

                        }


                        if (!documentt.equals("")) {
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.commit();
                            finish();
                            Intent vistaClienteActivity = new Intent(getApplicationContext(), CarteraActivity.class);
                            startActivity(vistaClienteActivity);
                            finish();
                            Alert.dialogo.cancel();

                        } else if (documentt.equals("")) {
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.commit();
                            finish();
                            Intent vistaClienteActivity = new Intent(getApplicationContext(), CarteraActivity.class);
                            startActivity(vistaClienteActivity);
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
}