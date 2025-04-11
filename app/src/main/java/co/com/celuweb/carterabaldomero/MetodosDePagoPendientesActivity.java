package co.com.celuweb.carterabaldomero;

import static co.com.celuweb.carterabaldomero.LoginActivity.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.AdapterFacturasPendientes;
import Adapters.AdaptersRecibosPendientes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.Anticipo;
import dataobject.ClienteSincronizado;
import dataobject.Facturas;
import dataobject.FormaPago;
import dataobject.Lenguaje;
import dataobject.MultiplesEstado;
import dataobject.Pendientes;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesFacturaPendientesSeleccionada;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class MetodosDePagoPendientesActivity extends AppCompatActivity implements AdaptersRecibosPendientes.facturaCarteraPendientes, AdapterFacturasPendientes.facturaCarteraPendientes, Synchronizer, Runnable {

    private TextView tvMontoFactura, tvDiferenciaMetodosPago, tvTotalFormasPago;
    private List<Pendientes> listaFacturas;
    private List<Pendientes> listaFacturas2;
    private List<Pendientes> listaFacturas3;
    private List<Pendientes> listaFacturasPendientesTemporal;
    private FormaPago formaPago;
    private Anticipo anticipo;
    private Usuario usuarioApp;
    private ClienteSincronizado clienteSel;
    private boolean envioInformacion = false;
    private RecyclerView rvListaPendientes;
    ProgressDialog progressDoalog;
    private MultiplesEstado pendientesSeleccionada;
    boolean estadoEnviadoRespuesta = false;
    private Lenguaje lenguajeElegido;
    TextView textTotalRecaudos, textRecaudos, textTotalFormasPago, textDiferencia, textFormaPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.metodos_pago_pendientes_activity);

        textTotalRecaudos = findViewById(R.id.txtTotalesPendientes);
        textRecaudos = findViewById(R.id.txtRecaudoPen);
        textTotalFormasPago = findViewById(R.id.txtTotalForPendientes);
        textDiferencia = findViewById(R.id.txtDiferenciaPendientes);
        textFormaPago = findViewById(R.id.txtFormasPagoPendientes);


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(MetodosDePagoPendientesActivity.this);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {

            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Payment Method (PENDING)"));

                textTotalRecaudos.setText("Total Collections Earrings");
                textRecaudos.setText("Total Collections");
                textTotalFormasPago.setText("Forms of Payment");
                textDiferencia.setText("Difference");
                textFormaPago.setText("Payments");


            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Metodos de Pago (PENDIENTES)"));

            }
        }


        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesFacturasMultiplesPendientes.obtenerFacturasMultiplesPendientesSeleccionado(this);
        pendientesSeleccionada = gson1.fromJson(stringJsonObject1, MultiplesEstado.class);


        formasPagoPendientesTotalParcial();
        metodosPago();

    }


    private void metodosPago() {


        double precioTotal = 0;
        String idPago = "";
        String numeroRecibo = "";
        String doctoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObject = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gsonPen = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);


        listaFacturas3 = new ArrayList<>();
        final List<String> doctoFinancieros = new ArrayList<>();
        final List<String> numeroRecibos = new ArrayList<>();
        final List<String> idPagos = new ArrayList<>();
        final Vector<String> listaItems = new Vector<>();

        if (facCollectionPendientes != null) {

            for (Pendientes pendientes : facCollectionPendientes) {

                doctoFinanciero = pendientes.getDoctoFinanciero();
                doctoFinancieros.add(doctoFinanciero);
                numeroRecibo = pendientes.getNumeroRecibo();
                numeroRecibos.add(numeroRecibo);

                idPago = pendientes.getIdPago();
                idPagos.add(idPago);
                precioTotal += pendientes.getMontoPendientes();


            }
        }


        if (pendientesSeleccionada == null) {

            if (facCollection != null) {

                for (Pendientes pendientes : facCollection) {

                    doctoFinanciero = pendientes.getDoctoFinanciero();
                    doctoFinancieros.add(doctoFinanciero);
                    numeroRecibo = pendientes.getNumeroRecibo();
                    numeroRecibos.add(numeroRecibo);

                    idPago = pendientes.getIdPago();
                    idPagos.add(idPago);
                    precioTotal += pendientes.getMontoPendientes();


                }
            }

            listaFacturasPendientesTemporal = DataBaseBO.cargarFacturasParametroPendientesTemporal(numeroRecibos);

            String idPagoTemporal = "";
            List<String> idPagosTemporal = new ArrayList<>();
            if (listaFacturasPendientesTemporal.size() >= 1) {
                for (Pendientes pendi : listaFacturasPendientesTemporal) {

                    idPagosTemporal.add(pendi.getIdPago());
                }
            }


            if (idPagosTemporal.size() == 0) {
                idPagosTemporal.add("0");
            }
            listaFacturas = DataBaseBO.cargarFacturasParametroPendientes(idPagos, numeroRecibo, idPagosTemporal);


        } else if (pendientesSeleccionada != null) {

            listaFacturasPendientesTemporal = DataBaseBO.cargarFacturasParametroPendientesTemporalVarias(idPagos, numeroRecibos);

            String idPagoTemporal = "";
            List<String> idPagosTemporal = new ArrayList<>();
            if (listaFacturasPendientesTemporal.size() >= 1) {
                for (Pendientes pendi : listaFacturasPendientesTemporal) {

                    idPagosTemporal.add(pendi.getIdPago());
                }
            }


            if (idPagosTemporal.size() == 0) {
                idPagosTemporal.add("0");
            }
            listaFacturas = DataBaseBO.cargarFacturasParametroPendientesMultiples(idPagos, numeroRecibos, idPagosTemporal);

        }


        listaFacturas2 = DataBaseBO.cargarFacturasParametroPendientesEnRecaudos(idPagos, numeroRecibo);

        String acert = "";
        for (Pendientes pendientes : listaFacturas2) {
            acert = pendientes.getIdPago();

        }

        rvListaPendientes = findViewById(R.id.rvListaFacturasPendientes);
        rvListaPendientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final AdapterFacturasPendientes adapter = new AdapterFacturasPendientes(listaFacturas, MetodosDePagoPendientesActivity.this);
        rvListaPendientes.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    private void formasPagoPendientesTotalParcial() {

        tvMontoFactura = findViewById(R.id.tvMontoCarteraFPPendientes);
        tvTotalFormasPago = findViewById(R.id.tvTotalFormasPagoPendientes);
        tvDiferenciaMetodosPago = findViewById(R.id.tvDiferenciaMetodosPagoPendientes);

        double precioTotal = 0;
        String idPago = "";
        String numeroRecibo = "";
        String doctoFinanciero = "";

        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObject = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gsonPen = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);


        listaFacturas3 = new ArrayList<>();
        final List<String> doctoFinancieros = new ArrayList<>();
        final List<String> numeroRecibos = new ArrayList<>();
        final List<String> idPagos = new ArrayList<>();
        final Vector<String> listaItems = new Vector<>();
        final List<Double> valoresFac = new ArrayList<>();


        double DiferenciaFormasPago = 0;
        double TotalFormasPago = 000.0f;
        double TotalFormasPagoE = 0;
        double DiferenciaFormasPagoE = 0;


        if (pendientesSeleccionada == null) {

            if (facCollection != null) {

                for (Pendientes pendientes : facCollection) {

                    doctoFinanciero = pendientes.getDoctoFinanciero();
                    doctoFinancieros.add(doctoFinanciero);
                    numeroRecibo = pendientes.getNumeroRecibo();
                    numeroRecibos.add(numeroRecibo);

                    idPago = pendientes.getIdPago();
                    idPagos.add(idPago);
                    valoresFac.add(pendientes.getValorDocumento());
                    precioTotal += pendientes.getMontoPendientes();


                }
            }

            listaFacturasPendientesTemporal = DataBaseBO.cargarFacturasParametroPendientesTemporal(numeroRecibos);

            String idPagoTemporal = "";
            List<String> idPagosTemporal = new ArrayList<>();

            if (listaFacturasPendientesTemporal.size() >= 1) {
                for (Pendientes pendi : listaFacturasPendientesTemporal) {

                    idPagosTemporal.add(pendi.getIdPago());
                }
            }


            if (idPagosTemporal.size() == 0) {
                idPagosTemporal.add("0");
            }

            TotalFormasPagoE = DataBaseBO.TotalFormasPagoPendientes(idPagos, numeroRecibo, idPagosTemporal);
            DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoPendientes(idPagos, numeroRecibo, idPagosTemporal);


            DiferenciaFormasPago = (DiferenciaFormasPagoE);
            TotalFormasPago = (TotalFormasPagoE);

            try {

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formating = NumberFormat.getInstance(new Locale("es"));


                    if (TotalFormasPago == 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                    } else if (TotalFormasPago < -1) {
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvMontoFactura.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                    } else if (TotalFormasPago > 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (DiferenciaFormasPago)));
                    }
                    tvMontoFactura.setText(formating.format(TotalFormasPago));

                } else {

                    NumberFormat formating = NumberFormat.getInstance(new Locale("en"));


                    if (TotalFormasPago == 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                    } else if (TotalFormasPago < -1) {
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvMontoFactura.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                    } else if (TotalFormasPago > 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (DiferenciaFormasPago)));
                    }
                    tvMontoFactura.setText(formating.format(TotalFormasPago));

                }


            } catch (Exception exception) {
                System.out.println("Error en la forma de pago parcial " + exception);
            }


        } else if (pendientesSeleccionada != null) {

            if (facCollectionPendientes != null) {

                for (Pendientes pendientes : facCollectionPendientes) {

                    doctoFinanciero = pendientes.getDoctoFinanciero();
                    doctoFinancieros.add(doctoFinanciero);
                    numeroRecibo = pendientes.getNumeroRecibo();
                    numeroRecibos.add(numeroRecibo);

                    idPago = pendientes.getIdPago();
                    idPagos.add(idPago);
                    valoresFac.add(pendientes.getValorDocumento());
                    precioTotal += pendientes.getMontoPendientes();


                }
            }


            listaFacturasPendientesTemporal = DataBaseBO.cargarFacturasParametroPendientesTemporalVarias(idPagos, numeroRecibos);


            String idPagoTemporal = "";
            List<String> idPagosTemporal = new ArrayList<>();
            for (Pendientes pendi : listaFacturasPendientesTemporal) {
                idPagosTemporal.add(pendi.getIdPago());
            }

            if (idPagosTemporal.size() == 0) {
                idPagosTemporal.add("0");
            }

            double valorEfectivo = DataBaseBO.TotalFormasPagoPendientesEfectivoMultiples(numeroRecibos, idPagosTemporal);
            double valorCheques = DataBaseBO.TotalFormasPagoPendientesChequesDataMultiples(numeroRecibos, idPagosTemporal);
            double valorTransferencias = DataBaseBO.TotalFormasPagoPendientesTransferenciaMultiples(numeroRecibos, idPagosTemporal);


            TotalFormasPagoE = DataBaseBO.TotalFormasPagoPendientesMultiples(idPagos, numeroRecibos, idPagosTemporal);
            DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoPendientesMultiples(idPagos, numeroRecibos, idPagosTemporal);


            DiferenciaFormasPago = (valorEfectivo + valorCheques);
            TotalFormasPago = (valorEfectivo + valorCheques + valorTransferencias);


            try {

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formating = NumberFormat.getInstance(new Locale("es"));


                    if (TotalFormasPago == 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                    } else if (TotalFormasPago < -1) {
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvMontoFactura.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                    } else if (TotalFormasPago > 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (DiferenciaFormasPago)));
                    }
                    tvMontoFactura.setText(formating.format(TotalFormasPago));

                } else {

                    NumberFormat formating = NumberFormat.getInstance(new Locale("en"));


                    if (TotalFormasPago == 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(DiferenciaFormasPago));
                    } else if (TotalFormasPago < -1) {
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvMontoFactura.setText(formating.format(TotalFormasPago - (-(DiferenciaFormasPago))));
                    } else if (TotalFormasPago > 0) {
                        tvTotalFormasPago.setText(formating.format(TotalFormasPago));
                        tvDiferenciaMetodosPago.setText(formating.format(TotalFormasPago - (DiferenciaFormasPago)));
                    }
                    tvMontoFactura.setText(formating.format(TotalFormasPago));

                }


            } catch (Exception exception) {
                System.out.println("Error en la forma de pago parcial " + exception);
            }

        }


    }


    public void CancelarPendientes(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObject = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gsonPen = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {

            if (lenguajeElegido.lenguaje.equals("USA")) {

                Alert.vistaDialogoCerrarSesion(MetodosDePagoPendientesActivity.this, "¿Are you sure you want to cancel the collection?", "Cancel Collection", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        double precioTotal = 0;
                        String idPago = "";
                        String numeroRecibo = "";
                        String doctoFinanciero = "";

                        listaFacturas3 = new ArrayList<>();
                        final List<String> doctoFinancieros = new ArrayList<>();
                        final List<String> numeroRecibos = new ArrayList<>();
                        final List<String> idPagos = new ArrayList<>();
                        final Vector<String> listaItems = new Vector<>();

                        String vendedor = "";


                        vendedor = DataBaseBO.cargarVendedorConsecutivo();

                        if (facCollection != null) {

                            for (Pendientes pendientes : facCollection) {

                                doctoFinanciero = pendientes.getDoctoFinanciero();
                                doctoFinancieros.add(doctoFinanciero);
                                numeroRecibo = pendientes.getNumeroRecibo();
                                numeroRecibos.add(numeroRecibo);

                                List<String> idPagosNumeroRecibo = DataBaseBO.cargarIdPagosNumeroRecibo(numeroRecibo);
                                idPagos.addAll(idPagosNumeroRecibo);


                                precioTotal += pendientes.getMontoPendientes();


                            }

                            DataBaseBO.eliminarConsecutivoId(vendedor);
                        }

                        if (facCollectionPendientes != null) {

                            for (Pendientes pendientes : facCollectionPendientes) {

                                doctoFinanciero = pendientes.getDoctoFinanciero();
                                doctoFinancieros.add(doctoFinanciero);
                                numeroRecibo = pendientes.getNumeroRecibo();
                                numeroRecibos.add(numeroRecibo);


                                List<String> idPagosNumeroRecibo = DataBaseBO.cargarIdPagosNumeroRecibo(numeroRecibo);
                                idPagos.addAll(idPagosNumeroRecibo);

                            }

                            DataBaseBO.eliminarConsecutivoPaquete(vendedor);
                            DataBaseBO.eliminarConsecutivoId(vendedor);
                        }

                        List<Facturas> listaIdenFotos = new ArrayList<>();
                        listaIdenFotos = DataBaseBO.cargaridFotos(idPagos);
                        DataBaseBO.eliminarFotoID(listaIdenFotos);

                        DataBaseBO.eliminarFoto(numeroRecibos);
                        DataBaseBO.eliminarRecaudosTotalPendientesRecaudos(numeroRecibos);
                        DataBaseBO.eliminarRecaudosRealizadosPendientesRecaudos(numeroRecibos);
                        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = settings.edit();
                        editor1.putBoolean("estado_MetodosDePagoPendientes", true);
                        editor1.remove("estado_MetodosDePagoPendientes");
                        editor1.putBoolean("estado_FacturasSeleccMultiplesPendientes", true);
                        editor1.remove("estado_FacturasSeleccMultiplesPendientes");
                        editor1.commit();
                        finish();


                        PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
                        PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
                        PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(MetodosDePagoPendientesActivity.this);


                        Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                        startActivity(vistaClienteActivity);
                        finish();
                        Alert.dialogo.cancel();


                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alert.dialogo.cancel();

                    }
                });


            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Alert.vistaDialogoCerrarSesion(MetodosDePagoPendientesActivity.this, "¿Esta seguro que desea cancelar el recaudo?", "Cancelar Recaudo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        double precioTotal = 0;
                        String idPago = "";
                        String numeroRecibo = "";
                        String doctoFinanciero = "";

                        listaFacturas3 = new ArrayList<>();
                        final List<String> doctoFinancieros = new ArrayList<>();
                        final List<String> numeroRecibos = new ArrayList<>();
                        final List<String> idPagos = new ArrayList<>();
                        final Vector<String> listaItems = new Vector<>();

                        String vendedor = "";


                        vendedor = DataBaseBO.cargarVendedorConsecutivo();

                        if (facCollection != null) {

                            for (Pendientes pendientes : facCollection) {

                                doctoFinanciero = pendientes.getDoctoFinanciero();
                                doctoFinancieros.add(doctoFinanciero);
                                numeroRecibo = pendientes.getNumeroRecibo();
                                numeroRecibos.add(numeroRecibo);

                                List<String> idPagosNumeroRecibo = DataBaseBO.cargarIdPagosNumeroRecibo(numeroRecibo);
                                idPagos.addAll(idPagosNumeroRecibo);


                                precioTotal += pendientes.getMontoPendientes();


                            }

                            DataBaseBO.eliminarConsecutivoId(vendedor);
                        }

                        if (facCollectionPendientes != null) {

                            for (Pendientes pendientes : facCollectionPendientes) {

                                doctoFinanciero = pendientes.getDoctoFinanciero();
                                doctoFinancieros.add(doctoFinanciero);
                                numeroRecibo = pendientes.getNumeroRecibo();
                                numeroRecibos.add(numeroRecibo);

                                List<String> idPagosNumeroRecibo = DataBaseBO.cargarIdPagosNumeroRecibo(numeroRecibo);
                                idPagos.addAll(idPagosNumeroRecibo);


                            }

                            DataBaseBO.eliminarConsecutivoPaquete(vendedor);
                            DataBaseBO.eliminarConsecutivoId(vendedor);
                        }
                        List<Facturas> listaIdenFotos = new ArrayList<>();
                        listaIdenFotos = DataBaseBO.cargaridFotos(idPagos);
                        DataBaseBO.eliminarFotoID(listaIdenFotos);

                        DataBaseBO.eliminarFoto(numeroRecibos);
                        DataBaseBO.eliminarRecaudosTotalPendientesRecaudos(numeroRecibos);
                        DataBaseBO.eliminarRecaudosRealizadosPendientesRecaudos(numeroRecibos);
                        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = settings.edit();
                        editor1.putBoolean("estado_MetodosDePagoPendientes", true);
                        editor1.remove("estado_MetodosDePagoPendientes");
                        editor1.putBoolean("estado_FacturasSeleccMultiplesPendientes", true);
                        editor1.remove("estado_FacturasSeleccMultiplesPendientes");
                        editor1.commit();
                        finish();


                        PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
                        PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
                        PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(MetodosDePagoPendientesActivity.this);


                        Intent vistaClienteActivity = new Intent(getApplicationContext(), RutaActivity.class);
                        startActivity(vistaClienteActivity);
                        finish();
                        Alert.dialogo.cancel();


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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {


            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {

            case R.id.menu_forward:

                //MOSTRAR PROGRES DIALOG
                progressDoalog = new ProgressDialog(this);
                if (lenguajeElegido == null) {
                    progressDoalog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {
                        progressDoalog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                        progressDoalog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                    }
                }
                progressDoalog.setCancelable(false);
                progressDoalog.show();

                ExecutorService executor = Executors.newFixedThreadPool(2);

                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);

                //EJECUTAR LA TAREA EN UN HILO
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        guardarTotalFinal();
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        CancelarPendientes(textTotalRecaudos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fw, menu);
        return true;
    }

    public void guardarTotalFinal() {

        tvMontoFactura = findViewById(R.id.tvMontoCarteraFP);
        tvTotalFormasPago = findViewById(R.id.tvTotalFormasPago);
        tvDiferenciaMetodosPago = findViewById(R.id.tvDiferenciaMetodosPago);

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObject = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gsonPen = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesUsuario.obtenerUsuario(this);
        usuarioApp = gson2.fromJson(stringJsonObject2, Usuario.class);

        final List<String> documentosFinanciero = new ArrayList<>();

        double precioTotal = 0;
        String idPago = "";
        String numeroRecibo = "";
        String doctoFinanciero = "";

        listaFacturas3 = new ArrayList<>();
        final List<String> doctoFinancieros = new ArrayList<>();
        final List<String> numeroRecibos = new ArrayList<>();
        final List<String> idPagos = new ArrayList<>();
        final Vector<String> listaItems = new Vector<>();

        if (facCollection != null) {

            for (Pendientes pendientes : facCollection) {

                doctoFinanciero = pendientes.getDoctoFinanciero();
                doctoFinancieros.add(doctoFinanciero);
                numeroRecibo = pendientes.getNumeroRecibo();
                numeroRecibos.add(numeroRecibo);
                idPago = pendientes.getIdPago();
                idPagos.add(idPago);
                precioTotal += pendientes.getMontoPendientes();

            }
        }

        if (pendientesSeleccionada != null) {

            if (facCollectionPendientes != null) {

                for (Pendientes pendientes : facCollectionPendientes) {

                    doctoFinanciero = pendientes.getDoctoFinanciero();
                    doctoFinancieros.add(doctoFinanciero);
                    numeroRecibo = pendientes.getNumeroRecibo();
                    numeroRecibos.add(numeroRecibo);

                    idPago = pendientes.getIdPago();
                    idPagos.add(idPago);

                    precioTotal += pendientes.getMontoPendientes();


                }
            }
        }

        double DiferenciaFormasPago;
        double TotalFormasPago = 000.0f;
        double TotalFormasPagoE = 0;
        double DiferenciaFormasPagoE = 0;


        if (facCollection != null) {

            listaFacturasPendientesTemporal = DataBaseBO.cargarFacturasParametroPendientesTemporal(numeroRecibos);

            String idPagoTemporal = "";
            List<String> idPagosTemporal = new ArrayList<>();
            for (Pendientes pendi : listaFacturasPendientesTemporal) {
                idPagosTemporal.add(pendi.getIdPago());
            }

            if (idPagosTemporal.size() == 0) {
                idPagosTemporal.add("0");
            }
            TotalFormasPagoE = DataBaseBO.TotalFormasPagoPendientes(idPagos, numeroRecibo, idPagosTemporal);
            DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoPendientes(idPagos, numeroRecibo, idPagosTemporal);


            DiferenciaFormasPago = (DiferenciaFormasPagoE);
            TotalFormasPago = (TotalFormasPagoE);

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {

                if (lenguajeElegido.lenguaje.equals("USA")) {

                    if (Utilidades.formatearDecimales(TotalFormasPago, 2) != DiferenciaFormasPago) {
                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "Total forms of payment must be equal to the amount collected").show();
                            }
                        });

                    }

                    if (TotalFormasPago == 0) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(MetodosDePagoPendientesActivity.this, "¿Are you sure you want to finish the collection? ", "Collect",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();


                                                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                                                String consec = "";
                                                String negocio = "";
                                                String vendedor = "";
                                                int contador = 1;
                                                int Position = 6;
                                                //    consec = DataBaseBO.cargarConsecutivo();
                                                //    negocio = DataBaseBO.cargarNegocioConsecutivo();
                                                //   vendedor = DataBaseBO.cargarVendedorConsecutivo();
                                                //   int consec1 = Integer.parseInt(consec);
                                                //   int vendedorsum = Integer.parseInt(vendedor);
                                                //   consec1 = consec1 + contador;
                                                // DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha);
                                                DataBaseBO.eliminarFacturaCartera(documentosFinanciero);
                                                DataBaseBO.eliminarRecaudosFinalizados();

                                                if (Utilidades.verificarNetwork(MetodosDePagoPendientesActivity.this)) {
                                                    if (DataBaseBO.hayInformacionXEnviar()) {

                                                        progressDoalog = new ProgressDialog(MetodosDePagoPendientesActivity.this);
                                                        progressDoalog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                                                        progressDoalog.setCancelable(false);
                                                        progressDoalog.show();

                                                        enviarInformacion();

                                                    } else {
                                                        Toasty.warning(getApplicationContext(), "You have no information to send....", Toasty.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    volverPantallaPrincipal();
                                                }
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });

                    }

                    if (TotalFormasPago != 0) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "Please fill in the pending fields in the payment method.").show();
                            }
                        });
                        return;
                    }


                } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                    if (TotalFormasPago != DiferenciaFormasPago) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "El total de formas de pago tiene que ser igual al monto del recaudo").show();
                            }
                        });

                    }

                    if (TotalFormasPago == 0) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(MetodosDePagoPendientesActivity.this, "¿Esta seguro que desea terminar el recaudo? ", "Terminar Recaudo",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();


                                                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                                                String consec = "";
                                                String negocio = "";
                                                String vendedor = "";
                                                int contador = 1;
                                                int Position = 6;
                                                //    consec = DataBaseBO.cargarConsecutivo();
                                                //    negocio = DataBaseBO.cargarNegocioConsecutivo();
                                                //   vendedor = DataBaseBO.cargarVendedorConsecutivo();
                                                //   int consec1 = Integer.parseInt(consec);
                                                //   int vendedorsum = Integer.parseInt(vendedor);
                                                //   consec1 = consec1 + contador;
                                                // DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha);
                                                DataBaseBO.eliminarFacturaCartera(documentosFinanciero);
                                                DataBaseBO.eliminarRecaudosFinalizados();

                                                if (Utilidades.verificarNetwork(MetodosDePagoPendientesActivity.this)) {
                                                    if (DataBaseBO.hayInformacionXEnviar()) {

                                                        progressDoalog = new ProgressDialog(MetodosDePagoPendientesActivity.this);
                                                        progressDoalog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                                                        progressDoalog.setCancelable(false);
                                                        progressDoalog.show();

                                                        enviarInformacion();

                                                    } else {

                                                        Toasty.warning(getApplicationContext(), "No tiene informacion por enviar....", Toasty.LENGTH_SHORT).show();

                                                    }
                                                } else {
                                                    volverPantallaPrincipal();
                                                }

                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });

                    }

                    if (TotalFormasPago != 0) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "Favor diligencie los campos pendientes en los métodos de pago.").show();
                            }
                        });
                        return;
                    }

                }
            }
        }


        if (facCollectionPendientes != null) {

            listaFacturasPendientesTemporal = DataBaseBO.cargarFacturasParametroPendientesTemporal(numeroRecibos);
            //  listaFacturasPendientesTemporal= DataBaseBO.cargarFacturasParametroPendientesEfectivoMultiples(numeroRecibos);


            String idPagoTemporal = "";
            List<String> idPagosTemporal = new ArrayList<>();
            for (Pendientes pendi : listaFacturasPendientesTemporal) {
                idPagosTemporal.add(pendi.getIdPago());
            }
            if (idPagosTemporal.size() == 0) {
                idPagosTemporal.add("0");
            }


            double valorEfectivo = DataBaseBO.TotalFormasPagoPendientesEfectivoMultiples(numeroRecibos, idPagosTemporal);
            double valorCheques = DataBaseBO.TotalFormasPagoPendientesChequesDataMultiples(numeroRecibos, idPagosTemporal);
            double valorTransferencias = DataBaseBO.TotalFormasPagoPendientesTransferenciaMultiples(numeroRecibos, idPagosTemporal);


            TotalFormasPagoE = DataBaseBO.TotalFormasPagoPendientesMultiples(idPagos, numeroRecibos, idPagosTemporal);
            DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoPendientesMultiples(idPagos, numeroRecibos, idPagosTemporal);


            DiferenciaFormasPago = (valorEfectivo + valorCheques);
            TotalFormasPago = (valorEfectivo + valorCheques + valorTransferencias);

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {

                if (lenguajeElegido.lenguaje.equals("USA")) {

                    if (Utilidades.formatearDecimales(TotalFormasPago, 2) != DiferenciaFormasPago) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "Total forms of payment must be equal to the amount collected").show();
                            }
                        });

                    }

                    if (TotalFormasPago == 0) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(MetodosDePagoPendientesActivity.this, "¿Are you sure you want to finish the collection? ", "Collect",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();


                                                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                                                String consec = "";
                                                String negocio = "";
                                                String vendedor = "";
                                                int contador = 1;
                                                int Position = 6;
                                                //    consec = DataBaseBO.cargarConsecutivo();
                                                //    negocio = DataBaseBO.cargarNegocioConsecutivo();
                                                //   vendedor = DataBaseBO.cargarVendedorConsecutivo();
                                                //   int consec1 = Integer.parseInt(consec);
                                                //   int vendedorsum = Integer.parseInt(vendedor);
                                                //   consec1 = consec1 + contador;
                                                // DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha);
                                                DataBaseBO.eliminarFacturaCartera(documentosFinanciero);
                                                DataBaseBO.eliminarRecaudosFinalizados();

                                                if (Utilidades.verificarNetwork(MetodosDePagoPendientesActivity.this)) {
                                                    if (DataBaseBO.hayInformacionXEnviar()) {

                                                        progressDoalog = new ProgressDialog(MetodosDePagoPendientesActivity.this);
                                                        progressDoalog.setMessage(getResources().getString(R.string.realizando_calculos_eng));
                                                        progressDoalog.setCancelable(false);
                                                        progressDoalog.show();

                                                        enviarInformacion();

                                                    } else {
                                                        Toasty.warning(getApplicationContext(), "You have no information to send....", Toasty.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    volverPantallaPrincipal();
                                                }
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });

                    } else {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "Please fill in the pending fields in the payment method.").show();
                            }
                        });
                    }


                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    if (TotalFormasPago != DiferenciaFormasPago) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "El total de formas de pago tiene que ser igual al monto del recaudo").show();
                            }
                        });

                    }

                    if (TotalFormasPago == 0) {

                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert.vistaDialogoCerrarSesion(MetodosDePagoPendientesActivity.this, "¿Esta seguro que desea terminar el recaudo? ", "Terminar Recaudo",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();


                                                final String fecha = Utilidades.fechaActual("yyyy-MM-dd");
                                                String consec = "";
                                                String negocio = "";
                                                String vendedor = "";
                                                int contador = 1;
                                                int Position = 6;
                                                //    consec = DataBaseBO.cargarConsecutivo();
                                                //    negocio = DataBaseBO.cargarNegocioConsecutivo();
                                                //   vendedor = DataBaseBO.cargarVendedorConsecutivo();
                                                //   int consec1 = Integer.parseInt(consec);
                                                //   int vendedorsum = Integer.parseInt(vendedor);
                                                //   consec1 = consec1 + contador;
                                                // DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fecha);
                                                DataBaseBO.eliminarFacturaCartera(documentosFinanciero);
                                                DataBaseBO.eliminarRecaudosFinalizados();

                                                if (Utilidades.verificarNetwork(MetodosDePagoPendientesActivity.this)) {
                                                    if (DataBaseBO.hayInformacionXEnviar()) {

                                                        progressDoalog = new ProgressDialog(MetodosDePagoPendientesActivity.this);
                                                        progressDoalog.setMessage(getResources().getString(R.string.realizando_calculos_esp));
                                                        progressDoalog.setCancelable(false);
                                                        progressDoalog.show();

                                                        enviarInformacion();

                                                    } else {

                                                        Toasty.warning(getApplicationContext(), "No tiene informacion por enviar....", Toasty.LENGTH_SHORT).show();

                                                    }
                                                } else {
                                                    volverPantallaPrincipal();
                                                }
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alert.dialogo.cancel();
                                            }
                                        });
                            }
                        });

                    } else {
                        if (progressDoalog != null)
                            progressDoalog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.warning(getApplicationContext(), "Favor diligencie los campos pendientes en los métodos de pago.").show();
                            }
                        });
                    }

                }
            }
        }
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(4);
        }
    };


    @Override
    public Serializable facturaCartera(List<Pendientes> pendientes) {
        try {
            Gson gson = new Gson();
            String jsonStringObject = gson.toJson(pendientes);
            PreferencesFacturaPendientesSeleccionada.guardarFacturasPendiSeleccionado(MetodosDePagoPendientesActivity.this, jsonStringObject);


        } catch (Exception e) {
            System.out.println("error serializable" + e);
        }

        return null;
    }

    private void guardarVista() {


        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FinalPrincipal", estado);
        editor1.commit();
    }


    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        try {

            switch (codeRequest) {

                case Constantes.ENVIARINFORMACION:
                    enviarInfo(ok, respuestaServer, msg);

                    System.out.println("respuesta server " + respuestaServer);

                    break;

                case Constantes.DESCARGARINFO:
                    descargarInfo(ok, respuestaServer, msg);
                    System.out.println("respuesta server " + respuestaServer);

                    break;

            }

        } catch (Exception exception) {
            System.out.println("Error en el repSync en el modulo Metodos de pago" + exception);
        }

    }

    private void enviarInfo(boolean ok, String respuestaServer, String msg) {


        if (progressDoalog != null)
            progressDoalog.dismiss();

        MetodosDePagoPendientesActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (respuestaServer.equals("listo")) {


                    if (respuestaServer.equals("listo") || respuestaServer.equals("ok")) {

                        progressDoalog = new ProgressDialog(MetodosDePagoPendientesActivity.this);
                        progressDoalog.setCancelable(false);
                        progressDoalog.setMax(100);

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {

                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Alert.alertGeneral(MetodosDePagoPendientesActivity.this, null, "The information is correctly recorded", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        DataBaseBO.borrarInfoTemp();

                                        estadoEnviadoRespuesta = true;
                                        Alert.dialogo.cancel();

                                        sincronizar2();


                                    }
                                }, null);

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Alert.alertGeneral(MetodosDePagoPendientesActivity.this, null, "Se registro correctamente la información", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        DataBaseBO.borrarInfoTemp();

                                        estadoEnviadoRespuesta = true;
                                        Alert.dialogo.cancel();

                                        sincronizar2();


                                    }
                                }, null);
                            }
                        }


                    } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {
                        estadoEnviadoRespuesta = false;
                    }


                } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {

                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.alertGeneral(MetodosDePagoPendientesActivity.this, null, "Could not Register Information", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    estadoEnviadoRespuesta = false;
                                    Alert.dialogo.cancel();


                                }
                            }, null);


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.alertGeneral(MetodosDePagoPendientesActivity.this, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    estadoEnviadoRespuesta = false;
                                    Alert.dialogo.cancel();


                                }
                            }, null);
                        }
                    }


                }
            }
        });


    }

    private void sincronizar2() {
        Sync sync1 = new Sync(MetodosDePagoPendientesActivity.this, Constantes.DESCARGARINFO);
        sync1.user = usuarioApp.codigo;
        sync1.password = usuarioApp.contrasena;
        sync1.start();
        envioInformacion = true;

        progressDoalog.setMessage("Downloading information....");
        progressDoalog.setTitle("Downloading");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDoalog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (progressDoalog.getProgress() <= progressDoalog
                            .getMax()) {
                        Thread.sleep(200);
                        handle.sendMessage(handle.obtainMessage());
                        if (progressDoalog.getProgress() == progressDoalog
                                .getMax()) {
                            progressDoalog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void descargarInfo(boolean ok, String respuestaServer, String msg) {

        //Progress.hide();

        try {

            if (ok) {

                MetodosDePagoPendientesActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        if (msg.contains("Unable to resolve host")) {

                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(MetodosDePagoPendientesActivity.this, "No internet connection.", Toasty.LENGTH_SHORT).show();


                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(MetodosDePagoPendientesActivity.this, "No tiene conexión a internet.", Toasty.LENGTH_SHORT).show();

                                }
                            }
                        } else {
                            volverPantallaPrincipal();
                        }
                    }
                });

            } else {
                MetodosDePagoPendientesActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        String mensaje = "";
                        if (lenguajeElegido.lenguaje.equals("USA"))
                            mensaje = "Error downloading the database, please restart the day.";
                        else
                            mensaje = "Error descargando la base de datos, inicie día nuevamente";

                        ProgressView.getInstance().Dismiss();
                        Alert.alertGeneral(MetodosDePagoPendientesActivity.this, null, mensaje, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Alert.dialogo.cancel();

                                volverPantallaLogin();

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

    public void volverPantallaPrincipal() {
        guardarVista();
        Intent vistaInforme = new Intent(MetodosDePagoPendientesActivity.this, PrincipalActivity.class);
        startActivity(vistaInforme);
    }

    public void volverPantallaLogin() {

        PreferencesUsuario.vaciarPreferencesUsuario(MetodosDePagoPendientesActivity.this);

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        settings.edit().clear().apply();
        getSharedPreferences("session", 0).edit().clear().apply();
        getSharedPreferences("session", 0).edit().clear().apply();
        Alert.dialogo.cancel();

        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();

    }

    @Override
    public void run() {

    }

    public void CancelarPago() {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObject = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gsonPen = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);

        double precioTotal = 0;
        String idPago = "";
        String numeroRecibo = "";
        String doctoFinanciero = "";

        listaFacturas3 = new ArrayList<>();
        final List<String> doctoFinancieros = new ArrayList<>();
        final List<String> numeroRecibos = new ArrayList<>();
        final List<String> idPagos = new ArrayList<>();
        final Vector<String> listaItems = new Vector<>();

        String vendedor = "";


        vendedor = DataBaseBO.cargarVendedorConsecutivo();

        if (facCollection != null) {

            for (Pendientes pendientes : facCollection) {

                doctoFinanciero = pendientes.getDoctoFinanciero();
                doctoFinancieros.add(doctoFinanciero);
                numeroRecibo = pendientes.getNumeroRecibo();
                numeroRecibos.add(numeroRecibo);

                idPago = pendientes.getIdPago();
                idPagos.add(idPago);


                precioTotal += pendientes.getMontoPendientes();


            }

            DataBaseBO.eliminarConsecutivoId(vendedor);
        }

        if (facCollectionPendientes != null) {

            for (Pendientes pendientes : facCollectionPendientes) {

                doctoFinanciero = pendientes.getDoctoFinanciero();
                doctoFinancieros.add(doctoFinanciero);
                numeroRecibo = pendientes.getNumeroRecibo();
                numeroRecibos.add(numeroRecibo);

                idPago = pendientes.getIdPago();
                idPagos.add(idPago);


            }

            DataBaseBO.eliminarConsecutivoPaquete(vendedor);
            DataBaseBO.eliminarConsecutivoId(vendedor);
        }
        List<Facturas> listaIdenFotos = new ArrayList<>();
        listaIdenFotos = DataBaseBO.cargaridFotos(idPagos);
        DataBaseBO.eliminarFotoID(listaIdenFotos);

        //  PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(this);
        PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
        PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(getApplicationContext());

        // PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(this);

        // PreferencesPendientesFacturas.vaciarPreferencesPendientesFacturaSeleccionada(MetodosDePagoPendientesActivity.this);
        //PreferencesFacturasMultiplesPendientesVarias.vaciarPreferencesFacturasMultiplesPendientesVariasSeleccionado(MetodosDePagoPendientesActivity.this);
        //   PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(MetodosDePagoPendientesActivity.this);


        DataBaseBO.eliminarFoto(numeroRecibos);
        DataBaseBO.eliminarRecaudosTotalPendientesRecaudos(numeroRecibos);
        DataBaseBO.eliminarRecaudosRealizadosPendientesRecaudos(numeroRecibos);
        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_MetodosDePagoPendientes", true);
        editor1.remove("estado_MetodosDePagoPendientes");
        editor1.putBoolean("estado_FacturasSeleccMultiplesPendientes", true);
        editor1.remove("estado_FacturasSeleccMultiplesPendientes");
        editor1.commit();
        finish();
    }

    private void enviarInformacion() {

        final String empresa;
        empresa = DataBaseBO.cargarCodigo();

        Sync sync = new Sync(MetodosDePagoPendientesActivity.this, Constantes.ENVIARINFORMACION);
        sync.user = empresa;
        sync.start();
        Alert.dialogo.cancel();

        envioInformacion = true;

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(MetodosDePagoPendientesActivity.this);
        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL
    }
}
