package Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import businessObject.PrinterBO;
import co.com.celuweb.carterabaldomero.FacturasRealizadasSeleccionadasActivity;
import co.com.celuweb.carterabaldomero.FacturasSeleccionadasPendientesActivity;
import co.com.celuweb.carterabaldomero.LoginActivity;
import co.com.celuweb.carterabaldomero.MetodosDePagoActivity;
import co.com.celuweb.carterabaldomero.MetodosDePagoPendientesActivity;
import co.com.celuweb.carterabaldomero.PendientesActivity;
import co.com.celuweb.carterabaldomero.PrincipalActivity;
import co.com.celuweb.carterabaldomero.R;
import configuracion.Synchronizer;
import dataobject.Bancos;
import dataobject.Facturas;
import dataobject.Lenguaje;
import dataobject.Pendientes;
import dataobject.Usuario;
import es.dmoral.toasty.Toasty;
import servicio.Sync;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import sharedpreferences.PreferencesUsuario;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.Utilidades;

public class AdaptersRecibosPendientes extends RecyclerView.Adapter<AdaptersRecibosPendientes.ViewHolder> implements View.OnClickListener, Synchronizer {

    public Context context;
    private View.OnClickListener listener;
    public List<Pendientes> pendientes;
    private AdaptersRecibosPendientes.facturaCarteraPendientes pendientesFacturas;
    private List<Pendientes> cargarFacturasPendientesCompleta;
    private ArrayList seleccionCarteraCheckbox;
    private ArrayList<Pendientes> seleccionPendientesMultiple;
    private ArrayList<Pendientes> multiplesMarcarChek;
    private Usuario usuarioApp;
    private boolean envioInformacion = false;
    ProgressDialog progressDoalog;
    Lenguaje lenguajeElegido;
    private String textoObservacion = "";

    public AdaptersRecibosPendientes(List<Pendientes> pendientes, Context context) {
        this.context = context;
        this.pendientes = pendientes;

        try {
            this.pendientesFacturas = (AdaptersRecibosPendientes.facturaCarteraPendientes) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @NonNull
    @Override
    public AdaptersRecibosPendientes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_facturas_pendientes, parent, false);
        view.setOnClickListener(this);
        return new AdaptersRecibosPendientes.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdaptersRecibosPendientes.ViewHolder holder, int position) {
        holder.binData(pendientes.get(position));
    }

    @Override
    public int getItemCount() {
        return pendientes.size();
    }

    @Override
    public long getItemId(int position) {
        return pendientes.size();
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
            Intent login = new Intent(context.getApplicationContext(), PendientesActivity.class);
            context.startActivity(login);
            ((PendientesActivity) context).finish();

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

        ((PendientesActivity) context).runOnUiThread(new Runnable() {
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

                                        Sync sync1 = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.DESCARGARINFO);
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

                                        Sync sync1 = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.DESCARGARINFO);
                                        sync1.user = usuarioApp.codigo;
                                        sync1.password = usuarioApp.contrasena;
                                        sync1.start();
                                        envioInformacion = true;

                                    }
                                }, null);


                            }
                        }


                    } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {

                    }


                } else if (respuestaServer.equals("No se pudo Registrar Informacion")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.alertGeneral(context, null, "Could not Register Information", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    Alert.dialogo.cancel();


                                }
                            }, null);

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.alertGeneral(context, null, "No se pudo Registrar Informacion", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    Alert.dialogo.cancel();


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
            progressDoalog.dismiss();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView numeroRecibo, codigoCliente, fechaCreacion, fechaCierre, montoPendiente, status;
        private Button seleccion, anulado, imprimir;
        private Spinner spinnerAnulacion;
        CheckBox cbCarteraFacturaPendientes;

        String empresa;


        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            empresa = DataBaseBO.cargarEmpresa();

            numeroRecibo = itemView.findViewById(R.id.txtNumeroRecibo);
            codigoCliente = itemView.findViewById(R.id.txtCodigoCliente);
            fechaCierre = itemView.findViewById(R.id.txtFechaCierre);
            fechaCreacion = itemView.findViewById(R.id.txtFechaCreacion);
            montoPendiente = itemView.findViewById(R.id.txtMontoPendientes);
            status = itemView.findViewById(R.id.txtStatus);
            seleccion = itemView.findViewById(R.id.btnSeleccionPendientes);
            imprimir = itemView.findViewById(R.id.btnImprimir);
            anulado = itemView.findViewById(R.id.btnAnuladosPendientes);
            spinnerAnulacion = itemView.findViewById(R.id.spinnerMotivosAnulacion);
            cbCarteraFacturaPendientes = itemView.findViewById(R.id.cbCarteraFacturaPendientes);

            if (empresa.equals("ADHB")) {
                imprimir.setVisibility(View.VISIBLE);
            }

        }

        void binData(final Pendientes item) {
            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

            String empresa23 = "";
            empresa23 = DataBaseBO.cargarEmpresa();
            final String finalEmpresa23 = empresa23;

            Calendar c = Calendar.getInstance();

            cbCarteraFacturaPendientes.setVisibility(View.GONE);
            numeroRecibo.setText(item.getNumeroRecibo());
            codigoCliente.setText(item.getCodigoCliente() + " - " + item.getNombrePropietario());

            if (finalEmpresa23.equals("AGUC")) {
                fechaCreacion.setText(Utilidades.ordenarFechaHora(item.getFechaCreacion().replace("/","-")));
            }
            else
            {
                fechaCreacion.setText(item.getFechaCreacion());
            }

            try {
                String fechacierreFecha = item.getFechaCierre();
                if (finalEmpresa23.equals("AGUC")) {
                    SimpleDateFormat formato = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date dataFormateada = formato.parse(fechacierreFecha);
                    Date dataFormateada2 = formato.parse(item.getFechaCreacion());
                    String fechaActual = Utilidades.fechaActual("MM/dd/yyyy HH:mm:ss");
                    Date dataFormateadaAcatual = formato.parse(fechaActual);

                    c.setTime(dataFormateada);
                    c.add(Calendar.DATE, 1);
                    dataFormateada = (c.getTime());
                    fechaCierre.setText(formato.format(dataFormateada));

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {
                            if (dataFormateadaAcatual.before(dataFormateada)) {
                                status.setText("Active");

                            } else if (dataFormateadaAcatual.after(dataFormateada)) {
                                status.setText("Expired");
                                status.setTextColor(Color.RED);
                            }

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                            if (dataFormateadaAcatual.before(dataFormateada)) {
                                status.setText("Activo");

                            } else if (dataFormateadaAcatual.after(dataFormateada)) {
                                status.setText("Vencido");
                                status.setTextColor(Color.RED);
                            }

                        }
                    }

                } else {

                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dataFormateada = formato.parse(fechacierreFecha);
                    Date dataFormateada2 = formato.parse(item.getFechaCreacion());
                    String fechaActual = Utilidades.fechaActual("yyyy-MM-dd HH:mm:ss");
                    Date dataFormateadaAcatual = formato.parse(fechaActual);

                    c.setTime(dataFormateada);
                    c.add(Calendar.DATE, 1);
                    dataFormateada = (c.getTime());
                    fechaCierre.setText(formato.format(dataFormateada));

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {
                            if (dataFormateadaAcatual.before(dataFormateada)) {
                                status.setText("Active");

                            } else if (dataFormateadaAcatual.after(dataFormateada)) {
                                status.setText("Expired");
                                status.setTextColor(Color.RED);
                            }

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                            if (dataFormateadaAcatual.before(dataFormateada)) {
                                status.setText("Activo");

                            } else if (dataFormateadaAcatual.after(dataFormateada)) {
                                status.setText("Vencido");
                                status.setTextColor(Color.RED);
                            }

                        }
                    }


                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


            String empresa = "";
            empresa = DataBaseBO.cargarEmpresa();
            final String finalEmpresa = empresa;


            double sum = 0;
            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                montoPendiente.setText(formatoNumero.format(item.getMontoPendientes()));


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                montoPendiente.setText(formatoNumero.format(item.getMontoPendientes()));


            }
            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("ADHB") || finalEmpresa.equals("AGUC") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC")
                    || finalEmpresa.equals("AGDP") || finalEmpresa.equals("AABR")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH")) {
                cbCarteraFacturaPendientes.setVisibility(View.VISIBLE);
            }


            final ArrayList<Pendientes> listaPendientes = new ArrayList<Pendientes>();
            final List<String> fechaCreacions = new ArrayList<>();
            final List<String> fechaCierres = new ArrayList<>();
            final List<String> montoPendientess = new ArrayList<>();
            final List<String> referencias = new ArrayList<>();
            final List<String> valorConsignados = new ArrayList<>();
            final List<String> cuentaBancarias = new ArrayList<>();
            final List<String> monedaConsiganadas = new ArrayList<>();
            final List<String> monedas = new ArrayList<>();
            final List<String> comprobanteFiscals = new ArrayList<>();
            final List<String> observacioness = new ArrayList<>();
            final List<String> viaPagos = new ArrayList<>();
            final List<String> usuarios = new ArrayList<>();
            final List<String> operacionCMEs = new ArrayList<>();
            final List<String> sincronizados = new ArrayList<>();
            final List<String> bancoPendientess = new ArrayList<>();
            final List<String> numeroCheqes = new ArrayList<>();
            final List<String> nombrePropietarios = new ArrayList<>();


            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Pendientes>>() {
            }.getType();
            String stringJsonObject = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(context);
            final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

            Gson gsonPen = new Gson();
            Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
            }.getType();
            String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(context);
            final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);


            spinnerAnulacion.setVisibility(View.GONE);

            listaPendientes.add(item);


            seleccion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    view.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    }, 600);

                    if (seleccionPendientesMultiple.size() < 1 && !cbCarteraFacturaPendientes.isChecked()) {

                        int position = itemView.getVerticalScrollbarPosition();
                        Pendientes pendientesSeleccionada = listaPendientes.get(position);
                        pendientesSeleccionada.getNumeroRecibo();

                        pendientesFacturas.facturaCartera(listaPendientes);

                        cargarFacturasPendientesCompleta = DataBaseBO.cargarFacturasPendientesCompleta(pendientesSeleccionada.getNumeroRecibo());

                        Gson gson = new Gson();
                        String jsonStringObject = gson.toJson(cargarFacturasPendientesCompleta);
                        PreferencesPendientesFacturas.guardarPendientesFacturaSeleccionada(context, jsonStringObject);

                        PreferencesFacturasMultiplesPendientes.vaciarPreferencesFacturasMultiplesPendientesSeleccionado(context);


                        Intent vistaClienteActivity = new Intent(context, MetodosDePagoPendientesActivity.class);
                        context.startActivity(vistaClienteActivity);


                    } else if (seleccionPendientesMultiple.size() >= 1 || cbCarteraFacturaPendientes.isChecked()) {

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Toasty.warning(context, "Unable to go to Select if you have marked one or multiple invoices..", Toasty.LENGTH_SHORT).show();


                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Toasty.warning(context, "No se puede ir a Seleccion si tiene marcadas una o varias facturas..", Toasty.LENGTH_SHORT).show();


                            }
                        }

                    }


                }
            });

            imprimir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    view.setEnabled(false);
                    ProgressDialog progressDialog;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    }, 600);


                    SharedPreferences settings = context.getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                    String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");

                    if (macImpresora.equals("-")) {

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Alert.nutresaShow(context, "Information", "No Printer Set. Please first Configure the Printer!", "OK", null,

                                        new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {

                                                Alert.dialogo.cancel();
                                            }

                                        }, null);

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Alert.nutresaShow(context, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

                                        new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {

                                                Alert.dialogo.cancel();
                                            }

                                        }, null);

                            }
                        }


                    } else {

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                progressDoalog = ProgressDialog.show(context, "",
                                        "Please Wait...\n\nProcessing Information!", true);
                                progressDoalog.show();

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                progressDoalog = ProgressDialog.show(context, "",
                                        "Por Favor Espere...\n\nProcesando Informacion!", true);
                                progressDoalog.show();

                            }
                        }


                        SharedPreferences set = context.getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
                        String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

                        int position = getAdapterPosition();
                        if (!tipoImpresora.equals("Intermec")) {
                            ImprimirTirilla(macImpresora, position);
                            /*sewooLKP20 = new SewooLKP20(FormConfigPrinterNuevo.this);
                            imprimirSewooLKP20(macImpresora);*/
                        } else {
                            ImprimirTirilla(macImpresora, position);
                        }
                    }

                }
            });

            String finalEmpresa1 = empresa;
            anulado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    view.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    }, 600);


                    int position = itemView.getVerticalScrollbarPosition();
                    Pendientes pendientesSeleccionada = listaPendientes.get(position);
                    pendientesSeleccionada.getNumeroRecibo();
                    pendientesFacturas.facturaCartera(listaPendientes);


                    Vector<Bancos> listaParametrosBancosSpinner;
                    cargarFacturasPendientesCompleta = DataBaseBO.cargarFacturasPendientesCompleta(pendientesSeleccionada.getNumeroRecibo());

                    spinnerAnulacion.setVisibility(View.VISIBLE);

                    String[] items;
                    final Vector<String> listaItems = new Vector<String>();

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            listaItems.addElement("Select");

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            listaItems.addElement("Seleccione");

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

                    spinnerAnulacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            String claseDocumento, idPago = "", sociedad = "", codigoCliente = "", codigoVendedor = "", doctoFinanciero, numeroRecibo = "",
                                    referencia = "", fecha, nroAnulacion;
                            double valorDocumento;
                            String fechaCreacion = "", fechaCierre = "";
                            double montoPendientes = 0;
                            double valorConsignado = 0;
                            String cuentaBancaria = "", monedaConsiganada = "", moneda = "", comprobanteFiscal = "", observaciones = "";
                            String viaPago = "", usuario = "", operacionCME = "", sincronizado = "", bancoPendientes = "", numeroCheqe = "", nombrePropietario = "";

                            List<String> claseDocumentos = new ArrayList<>();
                            List<String> sociedades = new ArrayList<>();
                            List<String> codigoClientes = new ArrayList<>();
                            List<String> codigoVendedores = new ArrayList<>();
                            List<String> doctoFinancieros = new ArrayList<>();
                            List<String> numeroRecibos = new ArrayList<>();
                            List<String> valorDocumentos = new ArrayList<>();
                            List<String> idsPagos = new ArrayList<>();

                            for (Pendientes pendientes1 : cargarFacturasPendientesCompleta) {
                                claseDocumento = pendientes1.getClaseDocumento();
                                claseDocumentos.add(claseDocumento);
                                sociedad = pendientes1.getSociedad();
                                sociedades.add(sociedad);
                                codigoCliente = pendientes1.getCodigoCliente();
                                codigoClientes.add(codigoCliente);
                                codigoVendedor = pendientes1.getCod_vendedor();
                                codigoVendedores.add(codigoVendedor);
                                doctoFinanciero = pendientes1.getDoctoFinanciero();
                                doctoFinancieros.add(doctoFinanciero);
                                numeroRecibo = pendientes1.getNumeroRecibo();
                                numeroRecibos.add(numeroRecibo);
                                idPago = pendientes1.getIdPago();
                                idsPagos.add(idPago);
                                referencia = pendientes1.getReferencia();
                                fechaCreacion = pendientes1.getFechaCreacion();
                                fechaCreacions.add(fechaCreacion);
                                fechaCierre = pendientes1.getFechaCierre();
                                fechaCierres.add(fechaCierre);
                                moneda = pendientes1.getMoneda();
                                monedas.add(moneda);
                                cuentaBancaria = pendientes1.getCuentaBancaria();
                                cuentaBancarias.add(cuentaBancaria);
                                monedaConsiganada = pendientes1.getMonedaConsiganada();
                                monedaConsiganadas.add(monedaConsiganada);
                                comprobanteFiscal = pendientes1.getComprobanteFiscal();
                                comprobanteFiscals.add(comprobanteFiscal);
                                valorDocumento = pendientes1.getValorDocumento();
                                valorDocumentos.add(String.valueOf(valorDocumento));
                                montoPendientes = pendientes1.getMontoPendientes();
                                montoPendientess.add(String.valueOf(montoPendientes));
                                valorConsignado = pendientes1.getConsignadoM();
                                valorConsignados.add(String.valueOf(valorConsignado));
                                cuentaBancaria = pendientes1.getCuentaBancaria();
                                cuentaBancarias.add(cuentaBancaria);
                                monedaConsiganada = pendientes1.getMonedaConsiganada();
                                monedaConsiganadas.add(monedaConsiganada);
                                comprobanteFiscal = pendientes1.getComprobanteFiscal();
                                comprobanteFiscals.add(comprobanteFiscal);
                                observaciones = pendientes1.getObservaciones();
                                observacioness.add(observaciones);
                                viaPago = pendientes1.getViaPago();
                                viaPagos.add(viaPago);
                                usuario = pendientes1.getUsuario();
                                usuarios.add(usuario);
                                operacionCME = pendientes1.getOperacionCME();
                                operacionCMEs.add(operacionCME);


                            }

                            String finalSociedad = sociedad;
                            String finalCodigoCliente = codigoCliente;
                            String finalCodigoVendedor = codigoVendedor;
                            String finalNumeroRecibo = numeroRecibo;
                            String finalMoneda = moneda;
                            String finalFechaCierre = fechaCierre;
                            String finalMonedaConsiganada = monedaConsiganada;
                            String finalReferencia = referencia;
                            String finalIdPago = idPago;

                            String consec = "", codigoCausal = "", numeroAnulacion = "";
                            String negocio = "";
                            String vendedor = "";
                            consec = DataBaseBO.cargarConsecutivo();
                            negocio = DataBaseBO.cargarNegocioConsecutivo();
                            vendedor = DataBaseBO.cargarVendedorConsecutivo();
                            int consec1 = Integer.parseInt(consec);
                            int vendedorsum = Integer.parseInt(vendedor);
                            int contador = 1;
                            consec1 = consec1;
                            numeroAnulacion = String.valueOf(negocio + vendedorsum + consec1);
                            codigoCausal = "";
                            final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                            DataBaseBO.guardarConsecutivo(negocio, vendedorsum, consec1, fechacon);

                            Bancos bancos = new Bancos();
                            listaParametrosBancosSpinner.get(position);
                            for (Bancos bancos1 : listaParametrosBancosSpinner) {
                                bancos.codigo_Banco = bancos1.codigo_Banco;
                            }
                            int indice = spinnerAnulacion.getSelectedItemPosition();
                            String causal = "";

                            if(indice > 0)
                                causal = listaParametrosBancosSpinner.get(indice-1).codigo_Banco;


                            if(finalEmpresa1.equals("AGUC"))
                            {
                                if (lenguajeElegido == null) {

                                } else if (lenguajeElegido != null) {
                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                        if (spinnerAnulacion.getSelectedItem().toString().equals("Select")) {
                                            Toasty.warning(context, "Reasons for Cancellation cannot be selected..", Toasty.LENGTH_SHORT).show();
                                        }
                                        if (!spinnerAnulacion.getSelectedItem().toString().equals("Select")) {

                                            String finalNumeroAnulacion = numeroAnulacion;
                                            String finalObservaciones = observaciones;
                                            String finalViaPago = viaPago;
                                            String finalUsuario = usuario;
                                            String finalOperacionCME = operacionCME;
                                            String finalCausal = causal;
                                            Alert.alertAnular(context, null, "Are you sure you want to cancel the collection?", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    DataBaseBO.guardarFormaPagAnulados(claseDocumentos, finalSociedad, finalCodigoCliente, finalCodigoVendedor, doctoFinancieros, montoPendientess
                                                            , finalNumeroRecibo, finalNumeroAnulacion, finalCausal, textoObservacion);

                                                    /**    DataBaseBO.guardarFormaPagPendientesPen(idsPagos, claseDocumentos, finalSociedad, finalCodigoCliente, finalCodigoVendedor,
                                                     finalReferencia, fechaCreacions, finalFechaCierre, valorDocumentos, finalMoneda, montoPendientess, valorConsignados, "0",
                                                     finalMonedaConsiganada, "0", doctoFinancieros, finalNumeroRecibo, finalObservaciones, viaPagos, finalUsuario, finalOperacionCME,
                                                     0, "0", "0", "0", 1);**/

                                                    //    DataBaseBO.eliminarRecaudosTotalPendientesNumRe(numeroRecibos);

                                                    if (Utilidades.verificarNetwork(context)) {
                                                        final String empresa;
                                                        empresa = DataBaseBO.cargarCodigo();
                                                        Sync sync = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.ENVIARINFORMACION);
                                                        sync.user = empresa;
                                                        sync.start();
                                                        Alert.dialogo.cancel();

                                                        envioInformacion = true;

                                                        Gson gson = new Gson();
                                                        String stringJsonObject = PreferencesUsuario.obtenerUsuario(context);
                                                        usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
                                                        // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

                                                        Sync sync1 = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.DESCARGARINFO);

                                                        sync1.user = usuarioApp.codigo;
                                                        sync1.password = usuarioApp.contrasena;
                                                        sync1.start();
                                                        envioInformacion = true;
                                                        Alert.dialogo.cancel();
                                                    } else {
                                                        descargarInfo(true, "", "");
                                                    }

                                                }
                                            }, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Alert.dialogo.cancel();
                                                }
                                            }, observacion -> {
                                                textoObservacion = observacion.getText().toString();
                                            });


                                        }


                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                        if (spinnerAnulacion.getSelectedItem().toString().equals("Seleccione")) {
                                            Toasty.warning(context, "Los Motivos de Anulacion no pueden qedar sin seleccion..", Toasty.LENGTH_SHORT).show();
                                        }
                                        if (!spinnerAnulacion.getSelectedItem().toString().equals("Seleccione")) {

                                            String finalNumeroAnulacion = numeroAnulacion;
                                            String finalObservaciones = observaciones;
                                            String finalViaPago = viaPago;
                                            String finalUsuario = usuario;
                                            String finalOperacionCME = operacionCME;
                                            String finalCausal1 = causal;
                                            Alert.alertAnular(context, null, "Esta seguro que quiere Anular el Recaudo?", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            DataBaseBO.guardarFormaPagAnulados(claseDocumentos, finalSociedad, finalCodigoCliente, finalCodigoVendedor, doctoFinancieros, montoPendientess
                                                                    , finalNumeroRecibo, finalNumeroAnulacion, finalCausal1, textoObservacion);

                                                            /**    DataBaseBO.guardarFormaPagPendientesPen(idsPagos, claseDocumentos, finalSociedad, finalCodigoCliente, finalCodigoVendedor,
                                                             finalReferencia, fechaCreacions, finalFechaCierre, valorDocumentos, finalMoneda, montoPendientess, valorConsignados, "0",
                                                             finalMonedaConsiganada, "0", doctoFinancieros, finalNumeroRecibo, finalObservaciones, viaPagos, finalUsuario, finalOperacionCME,
                                                             0, "0", "0", "0", 1);**/

                                                            //    DataBaseBO.eliminarRecaudosTotalPendientesNumRe(numeroRecibos);

                                                            if (Utilidades.verificarNetwork(context)) {
                                                                final String empresa;
                                                                empresa = DataBaseBO.cargarCodigo();
                                                                Sync sync = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.ENVIARINFORMACION);
                                                                sync.user = empresa;
                                                                sync.start();
                                                                Alert.dialogo.cancel();

                                                                envioInformacion = true;

                                                                Gson gson = new Gson();
                                                                String stringJsonObject = PreferencesUsuario.obtenerUsuario(context);
                                                                usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
                                                                // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL

                                                                Sync sync1 = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.DESCARGARINFO);

                                                                sync1.user = usuarioApp.codigo;
                                                                sync1.password = usuarioApp.contrasena;
                                                                sync1.start();
                                                                envioInformacion = true;
                                                                Alert.dialogo.cancel();
                                                            } else {
                                                                descargarInfo(true, "", "");
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
                            }
                            else
                            {
                                if(spinnerAnulacion.getSelectedItemPosition() > 0)
                                {
                                    String finalNumeroAnulacion = numeroAnulacion;
                                    String finalObservaciones = observaciones;
                                    String finalViaPago = viaPago;
                                    String finalUsuario = usuario;
                                    String finalOperacionCME = operacionCME;

                                    DataBaseBO.guardarFormaPagAnulados(claseDocumentos, finalSociedad, finalCodigoCliente, finalCodigoVendedor, doctoFinancieros, montoPendientess
                                            , finalNumeroRecibo, finalNumeroAnulacion, causal, textoObservacion);

                                    /**    DataBaseBO.guardarFormaPagPendientesPen(idsPagos, claseDocumentos, finalSociedad, finalCodigoCliente, finalCodigoVendedor,
                                     finalReferencia, fechaCreacions, finalFechaCierre, valorDocumentos, finalMoneda, montoPendientess, valorConsignados, "0",
                                     finalMonedaConsiganada, "0", doctoFinancieros, finalNumeroRecibo, finalObservaciones, viaPagos, finalUsuario, finalOperacionCME,
                                     0, "0", "0", "0", 1);**/

                                    //    DataBaseBO.eliminarRecaudosTotalPendientesNumRe(numeroRecibos);

                                    if (Utilidades.verificarNetwork(context)) {
                                        final String empresa;
                                        empresa = DataBaseBO.cargarCodigo();
                                        Sync sync = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.ENVIARINFORMACION);
                                        sync.user = empresa;
                                        sync.start();

                                        envioInformacion = true;
                                    } else {
                                        descargarInfo(true, "", "");
                                    }


//                                    Gson gson = new Gson();
//                                    String stringJsonObject = PreferencesUsuario.obtenerUsuario(context);
//                                    usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
//                                    // SE CARGA LA INFORMACION DEL USUARIO EN LA VISTA PRINCIPAL
//
//                                    Sync sync1 = new Sync(AdaptersRecibosPendientes.this::respSync, Constantes.DESCARGARINFO);
//
//                                    sync1.user = usuarioApp.codigo;
//                                    sync1.password = usuarioApp.contrasena;
//                                    sync1.start();
//                                    envioInformacion = true;

                                }

                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                }
            });

            seleccionCarteraCheckbox = new ArrayList<CheckBox>();
            seleccionPendientesMultiple = new ArrayList<Pendientes>();
            multiplesMarcarChek = new ArrayList<Pendientes>();

            CheckBox checkBox = new CheckBox(context);

            if (facCollectionPendientes != null) {


                String numeroReciboPendientesMarcar = "";

                for (Pendientes pen : facCollectionPendientes) {
                    numeroReciboPendientesMarcar = pen.numeroRecibo;
                    seleccionPendientesMultiple.add(pen);
                    multiplesMarcarChek.add(pen);
                    seleccionCarteraCheckbox.add(checkBox);
                }

                for (int i = 0; i < multiplesMarcarChek.size(); i++) {
                    String documento = String.valueOf(multiplesMarcarChek.get(i).getNumeroRecibo());
                    String id = multiplesMarcarChek.get(i).getIdPago();

                    if (id.equals(item.getIdPago())) {


                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                cbCarteraFacturaPendientes.setChecked(true);
                                Toasty.warning(context, "Already managed invoice cannot be saved..", Toasty.LENGTH_SHORT).show();


                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                cbCarteraFacturaPendientes.setChecked(true);
                                Toasty.warning(context, "Factura ya gestionada no se puede guardar..", Toasty.LENGTH_SHORT).show();


                            }
                        }


                    }
                }


            }

            cbCarteraFacturaPendientes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox checkBox = (CheckBox) buttonView;

                    if (checkBox.isChecked()) {
                        seleccionCarteraCheckbox.add(checkBox);
                        ArrayList<Pendientes> facturasPendientes = new ArrayList<Pendientes>();

                        seleccionPendientesMultiple.add(item);

                        for (int i = 0; i < seleccionPendientesMultiple.size(); i++) {
                            item.idUnicoPendientes++;
                            item.facturaSeleccionadaGestion = true;
                            facturasPendientes.add(seleccionPendientesMultiple.get(i));
                            Collections.sort(facturasPendientes, new Comparator<Pendientes>() {
                                @Override
                                public int compare(Pendientes pendientes, Pendientes t1) {
                                    return t1.getFechaCreacion().compareTo(pendientes.getFechaCreacion());
                                }
                            });
                            pendientesFacturas.facturaCartera(facturasPendientes);


                            Gson gson = new Gson();
                            String jsonStringObject = gson.toJson(seleccionPendientesMultiple);
                            PreferencesFacturasMultiplesPendientesVarias.guardarFacturasMultiplesPendientesVariasSeleccionado(context, jsonStringObject);

                        }

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Toasty.warning(context, "An invoice was selected..", Toasty.LENGTH_SHORT).show();


                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Toasty.warning(context, "Se selecciono una factura..", Toasty.LENGTH_SHORT).show();


                            }
                        }


                    } else if (!checkBox.isChecked()) {
                        for (int i = 0; i < seleccionPendientesMultiple.size(); i++) {

                            String numeroRecibo = String.valueOf(seleccionPendientesMultiple.get(i).getNumeroRecibo());

                            if (numeroRecibo.equals(item.getNumeroRecibo())) {

                                seleccionCarteraCheckbox.remove(i);
                                seleccionPendientesMultiple.remove(i);


                                pendientesFacturas.facturaCartera(seleccionPendientesMultiple);

                                notifyItemMoved(i, i);

                                Gson gson = new Gson();
                                String jsonStringObject = gson.toJson(seleccionPendientesMultiple);
                                PreferencesFacturasMultiplesPendientesVarias.guardarFacturasMultiplesPendientesVariasSeleccionado(context, jsonStringObject);


                            }

                        }
                        for (int i = 0; i < seleccionPendientesMultiple.size(); i++) {

                            String numeroRecibo = "";
                            List<String> pendientesList = new ArrayList<>();


                            for (Pendientes pen : seleccionPendientesMultiple) {
                                numeroRecibo = pen.getNumeroRecibo();
                                pendientesList.add(pen.getNumeroRecibo());
                            }

                            if (pendientesList.get(i).equals(item.getNumeroRecibo())) {

                                seleccionPendientesMultiple.remove(i);
                                pendientesFacturas.facturaCartera(seleccionPendientesMultiple);

                                notifyItemMoved(i, i);
                                Gson gson = new Gson();
                                String jsonStringObject = gson.toJson(seleccionPendientesMultiple);
                                PreferencesFacturasMultiplesPendientesVarias.guardarFacturasMultiplesPendientesVariasSeleccionado(context, jsonStringObject);


                            }

                        }
                    }
                }
            });


        }

    }

    public static interface facturaCarteraPendientes {


        Serializable facturaCartera(List<Pendientes> pendientes);
    }

    private void ImprimirTirilla(final String macAddress, int position) {

        List<Facturas> listaFacturas2 = DataBaseBO.cargarIdPagoOGRecaudosPendientesDatabase(pendientes.get(position).numeroRecibo);
        List<Facturas> listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientesRecaudosDataBase(pendientes.get(position).numeroRecibo);

        List<Facturas> listaFacturas3 = new ArrayList<>();

        listaFacturas3.addAll(listaFacturas2);

        listaFacturas3.addAll(listaFacturas4);

        new Thread(new Runnable() {
            public void run() {
                ZebraPrinterConnection printerConnection = null;
                try {
                    printerConnection = new BluetoothPrinterConnection(macAddress);
                    Looper.prepare();
                    //Abre la Conexion, la conexion Fisica es establecida aqui.
                    printerConnection.open();
                    ZebraPrinter zebraPrinter = ZebraPrinterFactory.getInstance(printerConnection);
                    PrinterLanguage printerLanguage = zebraPrinter.getPrinterControlLanguage();
                    Log.w("FormEstadisticaPedidos", "ImprimirTirilla -> Lenguaje de la Impresora " + printerLanguage);
                    // Envia los datos a la Impresora como un Arreglo de bytes.

                    //        String footer = PrinterBO.formatoTirillaEntrega1(clienteSel.codigo, listaFacturas3);
                    //
                    zebraPrinter.getPrinterControlLanguage();
                    String empresas = "";
                    empresas = DataBaseBO.cargarEmpresa();
                    String cpclData = "";

                    if (empresas.equals("ADHB")) {

                        cpclData = PrinterBO.formatoTirillaEntregaEmpresaEspaRecaudosPendientesNEW(context, pendientes.get(position).codigoCliente, listaFacturas3, pendientes.get(position).numeroRecibo);

                    } else if (empresas.equals("AGUC")) {

                        cpclData = PrinterBO.formatoTirillaEntregaEmpresaUSAPendientesNEW(context, pendientes.get(position).codigoCliente, listaFacturas3, pendientes.get(position).numeroRecibo);


                    } else {
                        cpclData = PrinterBO.formatoTirillaEntrega2(pendientes.get(position).codigoCliente, listaFacturas3);

                    }


                    String cpc = cpclData + "\n";
                    printerConnection.write(cpc.getBytes());
                    // Se asegura que los datos son enviados a la Impresora antes de cerrar la conexion.
                    Thread.sleep(100);
                    handle.sendEmptyMessage(0);
                    Looper.myLooper().quit();
                } catch (Exception e) {
                    Log.e("FormEstadisticaPedidos", "ImprimirTirilla -> " + e.getMessage(), e);
                    String mensaje = "No se pudo Imprimir.\n\n" + e.getMessage();
                    Activity activity = (Activity) context;
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "No se pudo Imprimir", Toast.LENGTH_SHORT).show();

                        }
                    });
                    handlerFinish.sendEmptyMessage(0);
                    Looper.myLooper().quit();
                } finally {                        // Cierra la conexion para liberar Recursos
                    try {
                        if (printerConnection != null) printerConnection.close();
                    } catch (ZebraPrinterConnectionException e) {
                        Log.e("FormEstadisticaPedidos", "ImprimirTirilla", e);
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "No se pudo Imprimir", Toast.LENGTH_SHORT).show();
                            }
                        });
                        handlerFinish.sendEmptyMessage(0);
                        Looper.myLooper().quit();
                    }
                }
            }
        }).start();
    }

    private Handler handlerFinish = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (progressDoalog != null)
                progressDoalog.cancel();

            if (Alert.dialogo != null)
                Alert.dialogo.cancel();
        }
    };

}

