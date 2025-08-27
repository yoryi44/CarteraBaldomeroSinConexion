package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;
import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.MetodosDePagoActivity;
import co.com.celuweb.carterabaldomero.R;
import co.com.celuweb.carterabaldomero.VistaClienteActivity;
import dataobject.Cartera;
import dataobject.Facturas;
import dataobject.FormaPago;
import dataobject.Lenguaje;
import metodosPago.MetodoDePagoBitcoinUpdate;
import metodosPago.MetodoDePagoChequeUpdate;
import metodosPago.MetodoDePagoEfectivoUpdate;
import metodosPago.MetodoDePagoTarjetaUpdate;
import metodosPago.MetodoDePagoTransferenciaUpdate;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesParcial;

import utilidades.Alert;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class AdapterFacturas extends RecyclerView.Adapter<AdapterFacturas.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mInflater;
    public List<Facturas> cartera;
    public List<Facturas> carteraSelec;
    public Context context;
    private View.OnClickListener listener;
    private AdapterFacturas.facturaCartera facturaCartera;
    private Cartera carteraS;
    private ArrayList<Facturas> seleccionCarteraMultp;
    private FormaPago formaPago;
    private Lenguaje lenguajeElegido;


    private int tipo;
    private Context contexto;

    public AdapterFacturas(List<Facturas> cartera, Context context) {

        this.context = context;
        this.cartera = cartera;

        try {
            this.facturaCartera = (AdapterFacturas.facturaCartera) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_facturas, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binData(cartera.get(position));

    }


    @Override
    public int getItemCount() {
        return cartera.size();

    }


    @Override
    public long getItemId(int position) {
        return cartera.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTotalFormasPagoVista, tvSaldoFacturaVista;
        private TextView tituloFormaPagoFactGeneral, tituloValorPagadoGeneral;
        private String conse;
        ImageView editar, eliminar;
        RadioButton rbEfectvo, rbCheq, rbTrans, rbTarjeta, rbBitcoin;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTotalFormasPagoVista = itemView.findViewById(R.id.tvTotalFormasPagoVista);
            tvSaldoFacturaVista = itemView.findViewById(R.id.tvSaldoFacturaVista);

            tituloValorPagadoGeneral = itemView.findViewById(R.id.tituloValorPagadoGeneral);
            tituloFormaPagoFactGeneral = itemView.findViewById(R.id.tituloFormaPagoFactGeneral);

            editar = itemView.findViewById(R.id.editar);
            eliminar = itemView.findViewById(R.id.eliminar);
            rbEfectvo = itemView.findViewById(R.id.rbEfectivo);
            rbCheq = itemView.findViewById(R.id.rbCheque);
            rbTrans = itemView.findViewById(R.id.rbTrasnferencia);
            rbTarjeta = itemView.findViewById(R.id.rbTarjetaCredito);
            rbBitcoin = itemView.findViewById(R.id.rbBitcoin);


        }


        void binData(final Facturas item) {

            Gson gson = new Gson();
            List<Cartera> carteraS = new ArrayList<>();
            Type collectionType = new TypeToken<Collection<Cartera>>() {
            }.getType();
            String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);
            Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

            Gson gson223 = new Gson();
            String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);

            String tipoUsuario="";
            tipoUsuario = DataBaseBO.cargarTipoUsuarioApp(context);

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    tituloFormaPagoFactGeneral.setText("Payment Method ");
                    tituloValorPagadoGeneral.setText("Amount Paid");

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {



                }
            }

            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(context);
            formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);

            List<String> facturasList = new ArrayList<>();

           String stringJsonObject22 = (PreferencesParcial.obteneParcialSeleccionada(context));
            facturasList.add(String.valueOf(stringJsonObject22));

            List<String> listaIds = new ArrayList<>();
            List<Facturas> listaIdenFotos = new ArrayList<>();
            String identiFotos="";



            listaIds.add(item.idPago);

            listaIdenFotos = DataBaseBO.cargaridFotos(listaIds, context);

            for (Facturas facturas: listaIdenFotos) {
                identiFotos = facturas.idenFoto;
            }


            String precioTotal2 = "";

            if (facturasList != null) {

                for (String cartera1 : facturasList) {
                    precioTotal2+= cartera1;
                }
            }

            double precioTotal = 0;

            if (facCollection != null) {

                for (Cartera cartera1 : facCollection) {
                    precioTotal += cartera1.getSaldo();
                }
            }

            if (formaPago != null) {

                if (formaPago.parcial == true) {

                    String input ="";
                    input = String.valueOf(item.getValor());
                    double valor = 0;
                    String empresa = "";
                    empresa = DataBaseBO.cargarEmpresa(context);
                    final String finalEmpresa = empresa;


                    if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                            || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                        tvSaldoFacturaVista.setText(formatoNumero.format(item.getValor()));

                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                        tvSaldoFacturaVista.setText(formatoNumero.format(item.getValor()));

                    }

                } else if (formaPago.parcial == false) {
                    String input ="";
                    input = String.valueOf(item.getValor());
                    double valor = 0;
                    String empresa = "";
                    empresa = DataBaseBO.cargarEmpresa(context);
                    final String finalEmpresa = empresa;

                    if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                            || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                        tvSaldoFacturaVista.setText(formatoNumero.format(item.getValor()));

                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                        tvSaldoFacturaVista.setText(formatoNumero.format(item.getValor()));

                    }

                }
            }else if (formaPago == null) {

                String input ="";
                input = String.valueOf(item.getValor());
                double valor = 0;
                String empresa = "";
                empresa = DataBaseBO.cargarEmpresa(context);
                final String finalEmpresa = empresa;

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getValor()));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getValor()));

                }

            }

                conse = item.getConsecutivo();

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    if (item.getFormaPago().equals("A")) {
                        tvTotalFormasPagoVista.setText("CASH");
                    }
                    if (item.getFormaPago().equals("B")) {
                        tvTotalFormasPagoVista.setText("CHECK");
                    }
                    if (item.getFormaPago().equals("6")) {
                        tvTotalFormasPagoVista.setText("TRANSFER");
                    }
                    if (item.getFormaPago().equals("O")) {
                        tvTotalFormasPagoVista.setText("CARD");
                    }
                    if (item.getFormaPago().equals("4")) {
                        tvTotalFormasPagoVista.setText("BITCOIN");
                    }


                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    if (item.getFormaPago().equals("A")) {
                        tvTotalFormasPagoVista.setText("EFECTIVO");
                    }
                    if (item.getFormaPago().equals("B")) {
                        tvTotalFormasPagoVista.setText("CHEQUE");
                    }
                    if (item.getFormaPago().equals("6")) {
                        tvTotalFormasPagoVista.setText("TRANSFERENCIA");
                    }
                    if (item.getFormaPago().equals("O")) {
                        tvTotalFormasPagoVista.setText("TARJETA");
                    }
                    if (item.getFormaPago().equals("4")) {
                        tvTotalFormasPagoVista.setText("BITCOIN");
                    }

                }
            }





            List<Facturas> finalListaIdenFotos1 = listaIdenFotos;
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    v.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 600);


                    for (int i = 0; i < cartera.size(); i++) {
                        String documento = String.valueOf(cartera.get(i));
                        if (documento.equals(item.getDocumento())) {
                            cartera.remove(i);



                        }
                    }

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Alert.vistaDialogoCerrarSesion(v.getContext(), "¿Are you sure to delete the payment method?", "Delete Payment Method", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String vendedor = "";

                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);

                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);
                                    DataBaseBO.eliminarRecaudos(item.idPago, context);
                                    DataBaseBO.eliminarFotoID(finalListaIdenFotos1, context);
                                    DataBaseBO.eliminarRecaudosPendientes(item.idPago, context);
                                    Intent login = new Intent(context.getApplicationContext(), MetodosDePagoActivity.class);
                                    context.startActivity(login);
                                    ((MetodosDePagoActivity) context).finish();
                                    notifyDataSetChanged();


                                    Alert.dialogo.cancel();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Alert.dialogo.cancel();
                                }
                            });

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Alert.vistaDialogoCerrarSesion(v.getContext(), "¿Esta seguro de eliminar la forma de pago?", "Eliminar Forma Pago", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String vendedor = "";
                                    vendedor = DataBaseBO.cargarVendedorConsecutivo(context);
                                    DataBaseBO.eliminarConsecutivoId(vendedor, context);
                                    DataBaseBO.eliminarRecaudos(item.idPago, context);
                                    DataBaseBO.eliminarFotoID(finalListaIdenFotos1, context);
                                    DataBaseBO.eliminarRecaudosPendientes(item.idPago, context);
                                    Intent login = new Intent(context.getApplicationContext(), MetodosDePagoActivity.class);
                                    context.startActivity(login);
                                    ((MetodosDePagoActivity) context).finish();
                                    notifyDataSetChanged();


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
            });

            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    v.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 600);

                    ProgressView.getInstance().Dismiss();
                    if (item.formaPago.equals("A") && item.idPago.equals(item.getIdPago())) {

                        ArrayList<Facturas> listafacturas = new ArrayList<Facturas>();
                        listafacturas.add(item);
                        facturaCartera.facturaCartera(listafacturas);

                        MetodoDePagoEfectivoUpdate.vistaDialogoUpdateEfectivo(v.getContext(), "Exito", "Información enviada correctamente",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        MetodoDePagoEfectivoUpdate.dialogo.cancel();


                                    }
                                }, null);

                    } else if (item.formaPago.equals("B") && item.idPago.equals(item.getIdPago())) {

                        ArrayList<Facturas> listafacturas = new ArrayList<Facturas>();
                        listafacturas.add(item);

                        facturaCartera.facturaCartera(listafacturas);

                        MetodoDePagoChequeUpdate.vistaDialogoUpdateCheque(v.getContext(), "Exito", "Información enviada correctamente",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        MetodoDePagoChequeUpdate.dialogo.cancel();


                                    }
                                }, null);

                    } else if (item.formaPago.equals("6") && item.idPago.equals(item.getIdPago())) {

                        ArrayList<Facturas> listafacturas = new ArrayList<Facturas>();
                        listafacturas.add(item);
                        facturaCartera.facturaCartera(listafacturas);

                        MetodoDePagoTransferenciaUpdate.vistaDialogoUpdateTransferencia(v.getContext(), "Exito", "Información enviada correctamente",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        MetodoDePagoTransferenciaUpdate.dialogo.cancel();


                                    }
                                }, null);
                    } else if (item.formaPago.equals("O") && item.idPago.equals(item.getIdPago())) {

                        ArrayList<Facturas> listafacturas = new ArrayList<Facturas>();
                        listafacturas.add(item);
                        facturaCartera.facturaCartera(listafacturas);

                        MetodoDePagoTarjetaUpdate.vistaDialogoUpdateTarjeta(v.getContext(), "Exito", "Información enviada correctamente",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        MetodoDePagoTarjetaUpdate.dialogo.cancel();


                                    }
                                }, null);
                    } else if (item.formaPago.equals("4") && item.idPago.equals(item.getIdPago())) {

                        ArrayList<Facturas> listafacturas = new ArrayList<Facturas>();
                        listafacturas.add(item);
                        facturaCartera.facturaCartera(listafacturas);

                        MetodoDePagoBitcoinUpdate.vistaDialogoUpdateBitcoin(v.getContext(), "Exito", "Información enviada correctamente",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        MetodoDePagoBitcoinUpdate.dialogo.cancel();


                                    }
                                }, null);

                    }


                }
            });



        }


    }


    public static interface facturaCartera {


        Serializable facturaCartera(List<Facturas> facturas);
    }


}
