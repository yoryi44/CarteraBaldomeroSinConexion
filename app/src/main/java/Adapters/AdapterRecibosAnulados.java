package Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.AnuladosActivity;
import co.com.celuweb.carterabaldomero.MetodosDePagoPendientesActivity;
import co.com.celuweb.carterabaldomero.R;
import configuracion.Synchronizer;
import dataobject.Bancos;
import dataobject.FacturasRealizadas;
import dataobject.Lenguaje;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.Utilidades;

public class AdapterRecibosAnulados extends RecyclerView.Adapter<AdapterRecibosAnulados.ViewHolder> implements View.OnClickListener, Synchronizer {

    public Context context;
    private View.OnClickListener listener;
    public List<FacturasRealizadas> facturas;
    private AdapterRecibosAnulados.facturaCarteraAnulados facturaCarteraAnulados;
    private List<FacturasRealizadas> cargarFacturasRealizadas;
    private Usuario usuarioApp;
    private boolean envioInformacion = false;
    ProgressDialog progressDoalog;
    private Lenguaje lenguajeElegido;
    private String textoObservacion = "";

    public AdapterRecibosAnulados(List<FacturasRealizadas> facturas, Context context) {
        this.context = context;
        this.facturas = facturas;

        try {
            this.facturaCarteraAnulados = (AdapterRecibosAnulados.facturaCarteraAnulados) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_facturas_anuladas, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterRecibosAnulados.ViewHolder holder, int position) {
        holder.binData(facturas.get(position));
    }

    @Override
    public int getItemCount() {
        return facturas.size();
    }

    @Override
    public long getItemId(int position) {
        return facturas.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(v);
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
        try {

            switch (codeRequest) {

                case Constantes.ENVIARINFORMACION:
                    enviarInfo(ok, respuestaServer, msg);
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

        if(ok)
        {
            Intent login = new Intent(context.getApplicationContext(), AnuladosActivity.class);
            context.startActivity(login);
            ((AnuladosActivity) context).finish();

            if (Alert.dialogo != null)
                Alert.dialogo.cancel();
        }
        else
        {
            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {
                    Alert.alertGeneral(context, null, "Error", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Alert.dialogo.cancel();
                        }
                    }, null);
                }
                else if (lenguajeElegido.lenguaje.equals("ESP")) {
                    Alert.alertGeneral(context, null, "Error", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Alert.dialogo.cancel();
                        }
                    }, null);
                }

            }
        }


    }

    private void enviarInfo(boolean ok, String respuestaServer, String msg) {

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(context);
        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);

        ((AnuladosActivity) context).runOnUiThread(new Runnable() {
            public void run() {
                if (respuestaServer.equals("listo")) {

                    if (respuestaServer.equals("listo") || respuestaServer.equals("ok")) {

                        progressDoalog = new ProgressDialog(context);
                        progressDoalog.setMax(100);


                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Alert.alertGeneral(context, null, "The information is correctly recorded", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Alert.dialogo.cancel();

                                        Sync sync1 = new Sync(AdapterRecibosAnulados.this::respSync, Constantes.DESCARGARINFO);
                                        sync1.user = usuarioApp.codigo;
                                        sync1.password = usuarioApp.contrasena;
                                        sync1.start();
                                        envioInformacion = true;

                                        progressDoalog = new ProgressDialog(context);
                                        progressDoalog.setMessage("Downloading information....");
                                        progressDoalog.setTitle("Downloading");
                                        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progressDoalog.show();

                                    }
                                }, null);

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Alert.alertGeneral(context, null, "Se registro correctamente la información", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Alert.dialogo.cancel();

                                        Sync sync1 = new Sync(AdapterRecibosAnulados.this::respSync, Constantes.DESCARGARINFO);
                                        sync1.user = usuarioApp.codigo;
                                        sync1.password = usuarioApp.contrasena;
                                        sync1.start();
                                        envioInformacion = true;

                                    }
                                }, null);


                            }
                        }

                    } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Alert.alertGeneral(context, null, "Could not Register Information", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (context instanceof Activity) {
                                            ((Activity) context).recreate();
                                        }

                                    }
                                }, null);

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Alert.alertGeneral(context, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (context instanceof Activity) {
                                            ((Activity) context).recreate();
                                        }

                                    }
                                }, null);

                            }
                        }
                    }


                } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.alertGeneral(context, null, "Could not Register Information", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (context instanceof Activity) {
                                        ((Activity) context).recreate();
                                    }

                                }
                            }, null);

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.alertGeneral(context, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (context instanceof Activity) {
                                        ((Activity) context).recreate();
                                    }

                                }
                            }, null);

                        }
                    }
                }
                else {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.alertGeneral(context, null, "Could not Register Information", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    if (context instanceof Activity) {
                                        ((Activity) context).recreate();
                                    }


                                }
                            }, null);

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.alertGeneral(context, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (context instanceof Activity) {
                                        ((Activity) context).recreate();
                                    }

                                }
                            }, null);

                        }
                    }
                }
            }
        });
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(3);
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView documento, tipo, fecha, dias, monto, tituloNumreciboAnulados, tituloFechaCreaciomAnulados, tituloMontoAnulados;
        private Button seleccion;
        private Spinner spinnerAnulacion;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            documento = itemView.findViewById(R.id.tvDocumentoFacturasRealizadas);
            //   tipo = itemView.findViewById(R.id.tvTipoFacturasRealizadas);
            fecha = itemView.findViewById(R.id.tvFechaVenciFacturasRealizadas);
            monto = itemView.findViewById(R.id.tvMontoFactuasRealizadas);
            spinnerAnulacion = itemView.findViewById(R.id.spinnerMotivosAnulacion);

            tituloNumreciboAnulados = itemView.findViewById(R.id.tituloNumreciboAnulados);
            tituloFechaCreaciomAnulados = itemView.findViewById(R.id.tituloFechaCreaciomAnulados);
            tituloMontoAnulados = itemView.findViewById(R.id.tituloMontoAnulados);


        }

        void binData(final FacturasRealizadas item) {

            Gson gson223 = new Gson();
            String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);


            documento.setText(item.getNumeroRecibo());
            //  tipo.setText(item.getClaseDocumento());
            fecha.setText(item.getFechaCierre());

            String empresa = "";
            empresa = DataBaseBO.cargarEmpresa();
            final String finalEmpresa = empresa;

            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                monto.setText(formatoNumero.format(item.montoPendientes));

            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                monto.setText(formatoNumero.format(item.montoPendientes));


            }

            final ArrayList<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<FacturasRealizadas>();
            Vector<Bancos> listaParametrosBancosSpinner;
            listaFacturasRealizadas.add(item);

            spinnerAnulacion.setVisibility(View.VISIBLE);

            String[] items;
            final Vector<String> listaItems = new Vector<String>();

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {
                    tituloNumreciboAnulados.setText("Number of receipt:");
                    tituloFechaCreaciomAnulados.setText("Date of Creation:");
                    tituloMontoAnulados.setText("Amount:");
                    listaItems.addElement("Grounds for annulment");
                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    listaItems.addElement("Motivos Anulacion");
                }
            }

            listaParametrosBancosSpinner = DataBaseBO.cargarMotivosAnulacion(listaItems);

            if (listaItems.size() > 0) {
                items = new String[listaItems.size()];
                listaItems.copyInto(items);

            } else {
                items = new String[]{};

                if (listaParametrosBancosSpinner != null)
                    listaParametrosBancosSpinner.removeAllElements();
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(context.getApplicationContext(), android.R.layout.simple_spinner_item, items);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAnulacion.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();


            String finalEmpresa1 = empresa;
            spinnerAnulacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                    if(finalEmpresa1.equals("AGUC"))
                    {
                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                if (!spinnerAnulacion.getSelectedItem().toString().equals("Grounds for annulment")) {

                                    Alert.alertAnular(context, null, "Are you sure you want to cancel the collection??", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    int position = itemView.getVerticalScrollbarPosition();
                                                    FacturasRealizadas realizadas = listaFacturasRealizadas.get(position);
                                                    realizadas.getIdPago();
                                                    facturaCarteraAnulados.facturaCarteraAnulados(listaFacturasRealizadas);

                                                    cargarFacturasRealizadas = DataBaseBO.cargarFacturasRealizadasCompleta(realizadas.getNumeroRecibo());

                                                    String claseDocumento, sociedad = "", codigoCliente = "", codigoVendedor = "", doctoFinanciero, numeroRecibo = "",
                                                            referencia = "", codigoCausal, numeroAnulacion, idPago = "";
                                                    double valorDocumento;

                                                    List<String> claseDocumentos = new ArrayList<>();
                                                    List<String> sociedades = new ArrayList<>();
                                                    List<String> codigoClientes = new ArrayList<>();
                                                    List<String> codigoVendedores = new ArrayList<>();
                                                    List<String> doctoFinancieros = new ArrayList<>();
                                                    List<String> numeroRecibos = new ArrayList<>();
                                                    List<String> valorDocumentos = new ArrayList<>();
                                                    List<String> idPagos = new ArrayList<>();


                                                    for (FacturasRealizadas pendientes1 : cargarFacturasRealizadas) {
                                                        claseDocumento = pendientes1.getClaseDocumento();
                                                        claseDocumentos.add(claseDocumento);
                                                        sociedad = pendientes1.getSociedad();
                                                        sociedades.add(sociedad);
                                                        idPago = pendientes1.getIdPago();
                                                        idPagos.add(idPago);
                                                        codigoCliente = pendientes1.getCodigoCliente();
                                                        codigoClientes.add(codigoCliente);
                                                        codigoVendedor = pendientes1.getCod_vendedor();
                                                        codigoVendedores.add(codigoVendedor);
                                                        doctoFinanciero = pendientes1.getDoctoFinanciero();
                                                        doctoFinancieros.add(doctoFinanciero);
                                                        numeroRecibo = pendientes1.getNumeroRecibo();
                                                        numeroRecibos.add(numeroRecibo);
                                                        valorDocumento = pendientes1.getValorDocumento();
                                                        valorDocumentos.add(String.valueOf(valorDocumento));
                                                        valorDocumento = pendientes1.getValorDocumento();
                                                        valorDocumentos.add(String.valueOf(valorDocumento));


                                                    }
                                                    codigoCausal = spinnerAnulacion.getSelectedItem().toString();

                                                    Bancos bancos = new Bancos();
                                                    listaParametrosBancosSpinner.get(position);
                                                    for (Bancos bancos1 : listaParametrosBancosSpinner) {
                                                        bancos.codigo_Banco = bancos1.codigo_Banco;
                                                    }

                                                    int indice = spinnerAnulacion.getSelectedItemPosition();

                                                    String causal = String.valueOf(indice);

                                                    DataBaseBO.guardarFormaPagAnuladosSolicitud(claseDocumentos, sociedad, codigoCliente, codigoVendedor, doctoFinancieros, valorDocumentos
                                                            , numeroRecibo, null, causal, null, null, null, textoObservacion);

                                                    if (Utilidades.verificarNetwork(context)) {

                                                        Alert.dialogo.cancel();

                                                        DataBaseBO.eliminarRecaudosRealziadosNumRe(idPagos);
                                                        final String empresa;
                                                        empresa = DataBaseBO.cargarCodigo();
                                                        Sync sync = new Sync(AdapterRecibosAnulados.this::respSync, Constantes.ENVIARINFORMACION);
                                                        sync.user = empresa;
                                                        sync.start();

                                                        envioInformacion = true;

                                                    }
                                                    else
                                                    {
                                                        Alert.dialogo.cancel();
                                                        enviarInfo(false, "", "");
                                                    }


                                                }
                                            }, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Alert.dialogo.cancel();
                                                }
                                            },
                                            observacion -> {
                                                textoObservacion = observacion.getText().toString();
                                            });


                                }

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                if (!spinnerAnulacion.getSelectedItem().toString().equals("Motivos Anulacion")) {

                                    Alert.alertAnular(context, null, "Esta seguro que quiere Anular el Recaudo?", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    int position = itemView.getVerticalScrollbarPosition();
                                                    FacturasRealizadas realizadas = listaFacturasRealizadas.get(position);
                                                    realizadas.getIdPago();
                                                    facturaCarteraAnulados.facturaCarteraAnulados(listaFacturasRealizadas);

                                                    cargarFacturasRealizadas = DataBaseBO.cargarFacturasRealizadasCompleta(realizadas.getNumeroRecibo());

                                                    String claseDocumento, sociedad = "", codigoCliente = "", codigoVendedor = "", doctoFinanciero, numeroRecibo = "",
                                                            referencia = "", codigoCausal, numeroAnulacion, idPago = "";
                                                    double valorDocumento;

                                                    List<String> claseDocumentos = new ArrayList<>();
                                                    List<String> sociedades = new ArrayList<>();
                                                    List<String> codigoClientes = new ArrayList<>();
                                                    List<String> codigoVendedores = new ArrayList<>();
                                                    List<String> doctoFinancieros = new ArrayList<>();
                                                    List<String> numeroRecibos = new ArrayList<>();
                                                    List<String> valorDocumentos = new ArrayList<>();
                                                    List<String> idPagos = new ArrayList<>();


                                                    for (FacturasRealizadas pendientes1 : cargarFacturasRealizadas) {
                                                        claseDocumento = pendientes1.getClaseDocumento();
                                                        claseDocumentos.add(claseDocumento);
                                                        sociedad = pendientes1.getSociedad();
                                                        sociedades.add(sociedad);
                                                        idPago = pendientes1.getIdPago();
                                                        idPagos.add(idPago);
                                                        codigoCliente = pendientes1.getCodigoCliente();
                                                        codigoClientes.add(codigoCliente);
                                                        codigoVendedor = pendientes1.getCod_vendedor();
                                                        codigoVendedores.add(codigoVendedor);
                                                        doctoFinanciero = pendientes1.getDoctoFinanciero();
                                                        doctoFinancieros.add(doctoFinanciero);
                                                        numeroRecibo = pendientes1.getNumeroRecibo();
                                                        numeroRecibos.add(numeroRecibo);
                                                        valorDocumento = pendientes1.getValorDocumento();
                                                        valorDocumentos.add(String.valueOf(valorDocumento));
                                                        valorDocumento = pendientes1.getValorDocumento();
                                                        valorDocumentos.add(String.valueOf(valorDocumento));


                                                    }
                                                    codigoCausal = spinnerAnulacion.getSelectedItem().toString();

                                                    Bancos bancos = new Bancos();
                                                    listaParametrosBancosSpinner.get(position);
                                                    for (Bancos bancos1 : listaParametrosBancosSpinner) {
                                                        bancos.codigo_Banco = bancos1.codigo_Banco;
                                                    }

                                                    int indice = spinnerAnulacion.getSelectedItemPosition();

                                                    String causal = String.valueOf(indice);

                                                    DataBaseBO.guardarFormaPagAnuladosSolicitud(claseDocumentos, sociedad, codigoCliente, codigoVendedor, doctoFinancieros, valorDocumentos
                                                            , numeroRecibo, null, causal, null, null, null, textoObservacion);

                                                    if (Utilidades.verificarNetwork(context)) {

                                                        Alert.dialogo.cancel();

                                                        DataBaseBO.eliminarRecaudosRealziadosNumRe(idPagos);
                                                        final String empresa;
                                                        empresa = DataBaseBO.cargarCodigo();
                                                        Sync sync = new Sync(AdapterRecibosAnulados.this::respSync, Constantes.ENVIARINFORMACION);
                                                        sync.user = empresa;
                                                        sync.start();

                                                        envioInformacion = true;

                                                    }
                                                    else
                                                    {  Alert.dialogo.dismiss();
                                                        enviarInfo(false, "", "");
                                                    }

                                                }
                                            }, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Alert.dialogo.cancel();
                                                }
                                            },
                                            observacion -> {
                                                textoObservacion = observacion.getText().toString();
                                            });
                                }

                            }
                        }
                    } else
                    {
                        if(spinnerAnulacion.getSelectedItemPosition() > 0)
                        {
                            int position = itemView.getVerticalScrollbarPosition();
                            FacturasRealizadas realizadas = listaFacturasRealizadas.get(position);
                            realizadas.getIdPago();
                            facturaCarteraAnulados.facturaCarteraAnulados(listaFacturasRealizadas);

                            cargarFacturasRealizadas = DataBaseBO.cargarFacturasRealizadasCompleta(realizadas.getNumeroRecibo());

                            String claseDocumento, sociedad = "", codigoCliente = "", codigoVendedor = "", doctoFinanciero, numeroRecibo = "",
                                    referencia = "", codigoCausal, numeroAnulacion, idPago = "";
                            double valorDocumento;

                            List<String> claseDocumentos = new ArrayList<>();
                            List<String> sociedades = new ArrayList<>();
                            List<String> codigoClientes = new ArrayList<>();
                            List<String> codigoVendedores = new ArrayList<>();
                            List<String> doctoFinancieros = new ArrayList<>();
                            List<String> numeroRecibos = new ArrayList<>();
                            List<String> valorDocumentos = new ArrayList<>();
                            List<String> idPagos = new ArrayList<>();


                            for (FacturasRealizadas pendientes1 : cargarFacturasRealizadas) {
                                claseDocumento = pendientes1.getClaseDocumento();
                                claseDocumentos.add(claseDocumento);
                                sociedad = pendientes1.getSociedad();
                                sociedades.add(sociedad);
                                idPago = pendientes1.getIdPago();
                                idPagos.add(idPago);
                                codigoCliente = pendientes1.getCodigoCliente();
                                codigoClientes.add(codigoCliente);
                                codigoVendedor = pendientes1.getCod_vendedor();
                                codigoVendedores.add(codigoVendedor);
                                doctoFinanciero = pendientes1.getDoctoFinanciero();
                                doctoFinancieros.add(doctoFinanciero);
                                numeroRecibo = pendientes1.getNumeroRecibo();
                                numeroRecibos.add(numeroRecibo);
                                valorDocumento = pendientes1.getValorDocumento();
                                valorDocumentos.add(String.valueOf(valorDocumento));
                                valorDocumento = pendientes1.getValorDocumento();
                                valorDocumentos.add(String.valueOf(valorDocumento));


                            }
                            codigoCausal = spinnerAnulacion.getSelectedItem().toString();

                            Bancos bancos = new Bancos();
                            listaParametrosBancosSpinner.get(position);
                            for (Bancos bancos1 : listaParametrosBancosSpinner) {
                                bancos.codigo_Banco = bancos1.codigo_Banco;
                            }

                            int indice = spinnerAnulacion.getSelectedItemPosition();

                            String causal = String.valueOf(indice);

                            DataBaseBO.guardarFormaPagAnuladosSolicitud(claseDocumentos, sociedad, codigoCliente, codigoVendedor, doctoFinancieros, valorDocumentos
                                    , numeroRecibo, null, causal, null, null, null, textoObservacion);

                            if (Utilidades.verificarNetwork(context)) {

                                Alert.dialogo.cancel();

                                DataBaseBO.eliminarRecaudosRealziadosNumRe(idPagos);
                                final String empresa;
                                empresa = DataBaseBO.cargarCodigo();
                                Sync sync = new Sync(AdapterRecibosAnulados.this::respSync, Constantes.ENVIARINFORMACION);
                                sync.user = empresa;
                                sync.start();

                                envioInformacion = true;

                            }
                            else
                            {
                                Alert.dialogo.cancel();
                                enviarInfo(false, "", "");
                            }

                        }
                    }


/////0 activo 1 anulado 2 vencido


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {


                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {
                            if (spinnerAnulacion.getSelectedItem().toString().equals("Grounds for annulment")) {
                                Toasty.warning(context, "Reasons for Cancellation cannot be selected..", Toasty.LENGTH_SHORT).show();
                            }

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                            if (spinnerAnulacion.getSelectedItem().toString().equals("Motivos Anulacion")) {
                                Toasty.warning(context, "Los Motivos de Anulacion no pueden qedar sin seleccion..", Toasty.LENGTH_SHORT).show();
                            }

                        }
                    }


                }
            });

        }

    }

    public static interface facturaCarteraAnulados {


        Serializable facturaCarteraAnulados(List<FacturasRealizadas> facturasAnulados);
    }


}
