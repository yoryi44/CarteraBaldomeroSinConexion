package co.com.celuweb.carterabaldomero;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

import Adapters.AdapterFormasPagoFacturasSelecc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import dataobject.Cartera;
import dataobject.FormaPago;
import dataobject.Lenguaje;
import dataobject.MotivosAbono;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Alert;
import metodosPago.AlertPagos;
import utilidades.Utilidades;

public class formaPagoActivity extends AppCompatActivity implements AdapterFormasPagoFacturasSelecc.facturaCartera {

    private TextView tvMontoFactura,tituloReciboDineroFacSelec,tituloReciboDineroFormasPago,tituloReciboDineroMonto,tituloReciboDineroObserva;
    private TextView idOtrosPagos;
    public EditText valorRecaudoParcial;
    public EditText observaciones;
    public double valorComparar = 0;
    private Lenguaje lenguajeElegido;
    double precioTotal = 0;
    Spinner spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.formapago_activity);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(formaPagoActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        TextView simboloCheq = findViewById(R.id.simboloformas);
        TextView simboloCheq1 = findViewById(R.id.simboloformas1);
        valorRecaudoParcial = findViewById(R.id.tvValorRecaudoParcial);
        observaciones = findViewById(R.id.tvObvservacionCartera);

        tituloReciboDineroFacSelec = findViewById(R.id.tituloReciboDineroFacSelec);
        idOtrosPagos = findViewById(R.id.idOtrosPagos);
        tituloReciboDineroFormasPago = findViewById(R.id.tituloReciboDineroFormasPago);
        tituloReciboDineroMonto = findViewById(R.id.tituloReciboDineroMonto);
        tituloReciboDineroObserva = findViewById(R.id.tituloReciboDineroObserva);

        String empresa = DataBaseBO.cargarEmpresa(formaPagoActivity.this);
        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AABR")) {
            simboloCheq.setText("$");
            simboloCheq1.setText("$");
        }
        if (finalEmpresa.equals("ADHB")) {
            simboloCheq.setText("$");
            simboloCheq1.setText("$");
        }
        if (finalEmpresa.equals("AGSC")) {
            simboloCheq.setText("$");
            simboloCheq1.setText("$");
        }
        if (finalEmpresa.equals("AGGC")) {
            simboloCheq.setText("Q");
            simboloCheq1.setText("Q");
        }
        if (finalEmpresa.equals("AFPN")) {
            simboloCheq.setText("C$");
            simboloCheq1.setText("C$");
        }
        if (finalEmpresa.equals("AFPZ")) {
            simboloCheq.setText("₡");
            simboloCheq1.setText("₡");
        }
        if (finalEmpresa.equals("AGCO")) {
            simboloCheq.setText("$");
            simboloCheq1.setText("$");
        }
        if (finalEmpresa.equals("AGAH")) {
            simboloCheq.setText("₡");
            simboloCheq1.setText("₡");
        }
        if (finalEmpresa.equals("AGDP")) {
            simboloCheq.setText("Q");
            simboloCheq1.setText("Q");

        }
        if (finalEmpresa.equals("AGUC")) {
            simboloCheq.setText("$");
            simboloCheq1.setText("$");
        }

        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(formaPagoActivity.this);

        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Cartera cartera = new Cartera();
        RecyclerView rvListaCarteraFacturasSelecFormasPago = findViewById(R.id.rvListaCarteraFacturasSelecFormasPago);
        rvListaCarteraFacturasSelecFormasPago.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final AdapterFormasPagoFacturasSelecc adapter = new AdapterFormasPagoFacturasSelecc((List<Cartera>) facCollection, formaPagoActivity.this);
        rvListaCarteraFacturasSelecFormasPago.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        for (Cartera cartera1 : facCollection) {
            precioTotal += cartera1.getSaldo();
            valorComparar = precioTotal;
        }


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


                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);


                guardarFormaPago();
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
        PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(this);
        editor1.putBoolean("estado_FormaPago", true);
        editor1.remove("estado_FormaPago");
        Intent vistaClienteActivity = new Intent(getApplicationContext(), FacturasSeleccionadasActivity.class);
        startActivity(vistaClienteActivity);
        editor1.commit();
        finish();
    }

    /**
     * METODO DE INICIALIZACION DE VARIABLES
     */
    private void configurarVista() {



        final RadioButton rbPagoParcialCartera = findViewById(R.id.rbPagoParcialCartera);
        final RadioButton rbPagoTotalCartera = findViewById(R.id.rbPagoTotalCartera);

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "payment "));

                tituloReciboDineroFacSelec.setText("Selected Documents");
                tituloReciboDineroFormasPago.setText("Fulfillment");
                tituloReciboDineroMonto.setText("Amount");
                tituloReciboDineroObserva.setText("Observation");
                rbPagoParcialCartera.setText("Others payments");
                rbPagoTotalCartera.setText("Full payment");
                idOtrosPagos.setText("Pay");

            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Recibo de Dinero"));
                idOtrosPagos.setText("Pago");

            }
        }


        tvMontoFactura = findViewById(R.id.tvMontoFactura);

        String input = "";
        input = String.valueOf(precioTotal);
        double valor = 0;
        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa(formaPagoActivity.this);
        final String finalEmpresa = empresa;

        /// decimales .., else ... else ,,.  5
        valorRecaudoParcial.addTextChangedListener(new TextWatcher() {

            boolean condicion = false;
            String estadoTextoAnterior = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(getApplicationContext());
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                    String input = s.toString();

                    if (!input.isEmpty()) {


                        input = input.replace(",", "").replace(".", "");
                        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,##.#");

                        String newPrice = formatoNumero.format(Double.parseDouble(input));
                        newPrice = newPrice.replace(",", ".");
                        if (newPrice.length() > 3) {
                            newPrice = newPrice.substring(0, newPrice.length() - 3) + ',' + newPrice.substring(newPrice.length() - 2);
                        }

                        valorRecaudoParcial.removeTextChangedListener(this); //To Prevent from Infinite Loop
                        valorRecaudoParcial.setText(newPrice);
                        valorRecaudoParcial.setSelection(newPrice.length()); //Move Cursor to end of String
                        valorRecaudoParcial.addTextChangedListener(this);


                    }

                } else if (finalEmpresa.equals("AGCO")) {

                    String input = s.toString();

                    if (!input.isEmpty()) {


                        if (input.length() < 3) {
                            String newPrice2 = input;
                            valorRecaudoParcial.removeTextChangedListener(this); //To Prevent from Infinite Loop
                            valorRecaudoParcial.setText(newPrice2);
                            valorRecaudoParcial.setSelection(newPrice2.length()); //Move Cursor to end of String
                            valorRecaudoParcial.addTextChangedListener(this);

                        } else {

                            input = input.replace(".", "").replace(",", "");
                            DecimalFormat formatoNumero = new DecimalFormat("###,###,###,###");

                            String newPrice = formatoNumero.format(Double.parseDouble(input));

                            newPrice = newPrice.replace(",", ".");

                            valorRecaudoParcial.removeTextChangedListener(this); //To Prevent from Infinite Loop
                            valorRecaudoParcial.setText(newPrice);
                            valorRecaudoParcial.setSelection(newPrice.length()); //Move Cursor to end of String
                            valorRecaudoParcial.addTextChangedListener(this);

                        }
                    }

                } else {

                    String input = s.toString();

                    if (!input.isEmpty()) {


                        input = input.replace(",", "").replace(".", "");
                        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,##.#");

                        String newPrice = formatoNumero.format(Double.parseDouble(input));
                        newPrice = newPrice.replace(".", ",");
                        if (newPrice.length() > 3) {
                            newPrice = newPrice.substring(0, newPrice.length() - 3) + '.' + newPrice.substring(newPrice.length() - 2);
                        }

                        valorRecaudoParcial.removeTextChangedListener(this); //To Prevent from Infinite Loop
                        valorRecaudoParcial.setText(newPrice);
                        valorRecaudoParcial.setSelection(newPrice.length()); //Move Cursor to end of String
                        valorRecaudoParcial.addTextChangedListener(this);


                    }

                }


            }


        });

        if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
            tvMontoFactura.setText(formatoNumero.format(precioTotal));

        } else {

            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
            tvMontoFactura.setText(formatoNumero.format(precioTotal));

        }


    }

    /**
     * METODO RADIOBUTTON PARA FORMAS DE PAGO
     *
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        LinearLayout li1 = (LinearLayout) findViewById(R.id.linearCarteraValorRecaudo);
        spinner2 = findViewById(R.id.spinnerFiltroMotivoFactura);

        String[] items;
        Vector<String> listaItems = new Vector<String>();

        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                listaItems.add("Select an option");
            }else if (lenguajeElegido.lenguaje.equals("ESP")) {
                listaItems.add("Seleccione una opción");
            }
        }

        Vector<MotivosAbono> listaParametosSpinner = DataBaseBO.cargarMotivosAbono(listaItems, formaPagoActivity.this);

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


        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rbPagoTotalCartera:
                if (checked)
                    li1.setVisibility(View.INVISIBLE);
                spinner2.setVisibility(View.VISIBLE);
                break;
            case R.id.rbPagoParcialCartera:
                if (checked)
                    li1.setVisibility(View.VISIBLE);
                spinner2.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * METODO PARA GUARDAR LA FORMA DE PAGO SELECCIONADA
     *
     * @param
     */
    public void guardarFormaPago() {

        final RadioButton rbPagoParcialCartera = findViewById(R.id.rbPagoParcialCartera);
        final RadioButton rbPagoTotalCartera = findViewById(R.id.rbPagoTotalCartera);

        if (!rbPagoTotalCartera.isChecked() && !rbPagoParcialCartera.isChecked()) {

            if (lenguajeElegido == null) {

            }else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Toasty.warning(this, "Select payment type..", Toasty.LENGTH_SHORT).show();

                }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Toasty.warning(this, "Seleccione el tipo de pago..", Toasty.LENGTH_SHORT).show();

                }
            }



        } else if (rbPagoTotalCartera.isChecked()) {

            String valorRecaudo = tvMontoFactura.getText().toString();
            double valorARecaudar = Utilidades.toFloat(valorRecaudo);
            //    String valor = tvSaldoCarteraFP.getText().toString();


            FormaPago formaPago = new FormaPago();
            formaPago.parcial = false;
            formaPago.valor = precioTotal;
            formaPago.observaciones = observaciones.getText().toString().trim();
            formaPago.observacionesMotivo = spinner2.getSelectedItem().toString();

            Gson gson = new Gson();
            String jsonStringObject = gson.toJson(formaPago);
            PreferencesFormaPago.guardarFormaSeleccionada(formaPagoActivity.this, jsonStringObject);

            String empresa = DataBaseBO.cargarEmpresa(formaPagoActivity.this);

            if(empresa.equals("AGUC"))
            {
                if(spinner2.getSelectedItemPosition() == 0)
                {
                    if (lenguajeElegido == null) {

                    }else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {


                            Toasty.warning(this, "Select an option", Toasty.LENGTH_SHORT).show();


                        }else if (lenguajeElegido.lenguaje.equals("ESP")) {


                            Toasty.warning(this, "Seleccione una opción", Toasty.LENGTH_SHORT).show();


                        }
                    }

                    return;
                }
            }

            if (observaciones.getText().toString().isEmpty()) {

                if (lenguajeElegido == null) {

                }else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {


                        Toasty.warning(this, "Field of observations cannot be blank..", Toasty.LENGTH_SHORT).show();


                    }else if (lenguajeElegido.lenguaje.equals("ESP")) {


                        Toasty.warning(this, "El campo de observaciones no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();


                    }
                }

            } else if (!observaciones.getText().toString().isEmpty()) {
                guardarVistaTotal();
                Intent vistaClienteActivity = new Intent(this, MetodosDePagoActivity.class);
                startActivity(vistaClienteActivity);
            }


        } else if (rbPagoParcialCartera.isChecked()) {

            valorRecaudoParcial.getText().toString();

            String empresa = DataBaseBO.cargarEmpresa(formaPagoActivity.this);

            if(empresa.equals("AGUC"))
            {
                if(spinner2.getSelectedItemPosition() == 0)
                {
                    if (lenguajeElegido == null) {

                    }else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {


                            Toasty.warning(this, "Select an option", Toasty.LENGTH_SHORT).show();


                        }else if (lenguajeElegido.lenguaje.equals("ESP")) {


                            Toasty.warning(this, "Seleccione una opción", Toasty.LENGTH_SHORT).show();


                        }
                    }

                    return;
                }
            }

            if (observaciones.getText().toString().isEmpty()) {

                if (lenguajeElegido == null) {

                }else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        Toasty.warning(this, "Field of observations cannot be blank..", Toasty.LENGTH_SHORT).show();


                    }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        Toasty.warning(this, "El campo de observaciones no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();


                    }
                }


            }

            if (valorRecaudoParcial.getText().toString().isEmpty()) {

                if (lenguajeElegido == null) {

                }else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        Toasty.warning(this, "The value of the partial collection cannot remain empty..", Toasty.LENGTH_SHORT).show();


                    }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        Toasty.warning(this, "El valor del recaudo parcial no puede quedar vacio..", Toasty.LENGTH_SHORT).show();


                    }
                }


            } else if (!valorRecaudoParcial.getText().toString().isEmpty() && !observaciones.getText().toString().isEmpty()) {

                double valor = 0;
                empresa = DataBaseBO.cargarEmpresa(formaPagoActivity.this);
                final String finalEmpresa = empresa;
                double monto = 0;
                String input = valorRecaudoParcial.getText().toString();

                /// decimales .., else ... else ,,.  5

                if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                    if (!input.isEmpty()) {
                        if (input.contains(".") && input.contains(",")) {

                            input = input.replace(".", "");
                            input = input.replace(",", ".");
                            valor = Double.parseDouble(input);

                        } else if (input.contains(",")) {

                            input = input.replace(",", ".");
                            valor = Double.parseDouble(input);

                        } else if (!input.contains(".") && !input.contains(",")) {
                            valor = Double.parseDouble(input);
                        }
                    }

                } else if (finalEmpresa.equals("AGCO")) {

                    if (!input.isEmpty()) {

                        if (input.contains(".")) {

                            input = input.replace(".", "");
                            valor = Double.parseDouble(input);


                        } else if (!input.contains(".") && !input.contains(",")) {
                            valor = Double.parseDouble(input);
                        }
                    }

                } else {

                    if (!input.isEmpty()) {

                        if (input.contains(",")) {

                            input = input.replace(",", "");
                            valor = Double.parseDouble(input);

                        } else if (input.contains(".")) {


                            valor = Double.parseDouble(input);


                        } else if (!input.contains(".") && !input.contains(",")) {
                            valor = Double.parseDouble(input);
                        }
                    }
                }


                if (input.isEmpty()) {

                    if (lenguajeElegido == null) {

                    }else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(this, "The amount field cannot be blank..", Toasty.LENGTH_SHORT).show();


                        }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(this, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();


                        }
                    }

                }


                FormaPago formaPago = new FormaPago();
                formaPago.parcial = true;
                formaPago.valor = valor;
                formaPago.observaciones = observaciones.getText().toString().trim();
                formaPago.observacionesMotivo = spinner2.getSelectedItem().toString();

                if (valor != 0) {



                        Gson gson = new Gson();
                        String jsonStringObject = gson.toJson(formaPago);
                        PreferencesFormaPago.guardarFormaSeleccionada(formaPagoActivity.this, jsonStringObject);

                        guardarVistaParcial();
                        Intent vistaClienteActivity = new Intent(this, MetodosDePagoActivity.class);
                        startActivity(vistaClienteActivity);



                   /** if (valor > valorComparar) {


                        if (lenguajeElegido == null) {

                        }else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Toasty.warning(this, "The value of the collection cannot be greater than the amount to be collected...", Toasty.LENGTH_SHORT).show();


                            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Toasty.warning(this, "El valor del recaudo no puede ser mayor al del monto a recaudar...", Toasty.LENGTH_SHORT).show();


                            }
                        }

                    }**/

                }

                if (valor == 0) {

                    if (lenguajeElegido == null) {

                    }else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(this, "The value entered cannot be 0..", Toasty.LENGTH_SHORT).show();


                        }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(this, "El valor ingresado no puede ser 0..", Toasty.LENGTH_SHORT).show();


                        }
                    }

                }


            }

        } else if (!rbPagoTotalCartera.isChecked() || !rbPagoParcialCartera.isChecked()) {

            FormaPago formaPago = new FormaPago();
            formaPago.parcial = Boolean.parseBoolean(null);
            formaPago.observacionesMotivo = spinner2.getSelectedItem().toString();
            formaPago.valor = 0;

            Gson gson = new Gson();
            String jsonStringObject = gson.toJson(formaPago);
            PreferencesFormaPago.guardarFormaSeleccionada(formaPagoActivity.this, jsonStringObject);
        }
    }

    /**
     * METODO PARA CANCELAR LA FORMA DE PAGO, RECIBE COLLECTION FACTURAS,FORMA PAGO,BORRA SHARED PREFERENCE
     *
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

                Alert.vistaDialogoCerrarSesion(formaPagoActivity.this, "¿Are you sure you want to cancel the collection?", "Cancel Collection", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        List<Cartera> carteraS = new ArrayList<>();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(formaPagoActivity.this);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

                        final List<String> claseDocumento = new ArrayList<>();
                        List<String> codigoUs = new ArrayList<>();
                        final List<String> documentt = new ArrayList<>();
                        String claseDocument = "";
                        String codigoU = "";
                        String nombreU = "";
                        double precioTotal = 0;
                        String document = "";

                        for (Cartera cartera1 : facCollection) {
                            document = cartera1.getDocumento();
                            precioTotal += cartera1.getSaldo();

                            claseDocument = cartera1.getConcepto();
                            claseDocumento.add(claseDocument);
                            documentt.add(document);

                        }


                        if (!documentt.equals("")) {

                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_FormaPago", true);
                            editor1.remove("estado_FormaPago");
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();

                        } else if (documentt.equals("")) {
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_FormaPago", true);
                            editor1.remove("estado_FormaPago");
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();
                        }
                        Intent vistaClienteActivity = new Intent(getApplicationContext(), CarteraActivity.class);
                        startActivity(vistaClienteActivity);

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

                Alert.vistaDialogoCerrarSesion(formaPagoActivity.this, "¿Esta seguro que desea cancelar el recaudo?", "Cancelar Recaudo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        List<Cartera> carteraS = new ArrayList<>();
                        Type collectionType = new TypeToken<Collection<Cartera>>() {
                        }.getType();
                        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(formaPagoActivity.this);

                        Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

                        final List<String> claseDocumento = new ArrayList<>();
                        List<String> codigoUs = new ArrayList<>();
                        final List<String> documentt = new ArrayList<>();
                        String claseDocument = "";
                        String codigoU = "";
                        String nombreU = "";
                        double precioTotal = 0;
                        String document = "";

                        for (Cartera cartera1 : facCollection) {
                            document = cartera1.getDocumento();
                            precioTotal += cartera1.getSaldo();

                            claseDocument = cartera1.getConcepto();
                            claseDocumento.add(claseDocument);
                            documentt.add(document);

                        }


                        if (!documentt.equals("")) {

                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_FormaPago", true);
                            editor1.remove("estado_FormaPago");
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();

                        } else if (documentt.equals("")) {
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_FacturasSeleccionadas", true);
                            editor1.remove("estado_FacturasSeleccionadas");
                            editor1.putBoolean("estado_FormaPago", true);
                            editor1.remove("estado_FormaPago");
                            PreferencesCartera.vaciarPreferencesCarteraSeleccionada(getApplicationContext());
                            editor1.commit();
                            finish();
                            Alert.dialogo.cancel();
                        }
                        Intent vistaClienteActivity = new Intent(getApplicationContext(), CarteraActivity.class);
                        startActivity(vistaClienteActivity);

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
     * METODO PARA GUARDAR EL ESTADO DE LA VISTA
     */
    private void guardarVistaTotal() {
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FormaPagoTotal", estado);
        editor1.commit();
    }

    private void guardarVistaParcial() {
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FormaPagoParcial", estado);
        editor1.commit();
    }

    /**
     * INTERFACE LIST<CARTERA> CARTERA
     *
     * @param cartera
     * @return
     */
    @Override
    public Serializable facturaCartera(List<Cartera> cartera) {
        return null;
    }


}



