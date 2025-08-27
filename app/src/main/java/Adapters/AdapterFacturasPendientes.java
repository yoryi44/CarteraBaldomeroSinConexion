package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.MetodosDePagoPendientesActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Facturas;
import dataobject.Lenguaje;
import dataobject.MultiplesEstado;
import dataobject.Pendientes;
import es.dmoral.toasty.Toasty;
import metodosPago.MetodoDePagoChequePendientes;
import metodosPago.MetodoDePagoEfectivoPendientes;
import sharedpreferences.PreferencesFacturaPendientesSeleccionada;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesFotos;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class AdapterFacturasPendientes extends RecyclerView.Adapter<AdapterFacturasPendientes.ViewHolder> implements View.OnClickListener {

    public Context context;
    private View.OnClickListener listener;
    public List<Pendientes> pendientes;
    private MultiplesEstado multipleestado;
    private AdapterFacturasPendientes.facturaCarteraPendientes pendientesFacturas;
    private List<Pendientes> cargarFacturasPendientesCompleta;
    private List<Pendientes> cargarFacturasPendientesCompletaMultiples;
    private Lenguaje lenguajeElegido;

    public AdapterFacturasPendientes(List<Pendientes> pendientes, Context context) {
        this.context = context;
        this.pendientes = pendientes;

        try {
            this.pendientesFacturas = (AdapterFacturasPendientes.facturaCarteraPendientes) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @NonNull
    @Override
    public AdapterFacturasPendientes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_facturas_pendientes_pen, parent, false);
        view.setOnClickListener(this);
        return new AdapterFacturasPendientes.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterFacturasPendientes.ViewHolder holder, int position) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTotalFormasPagoVista, tvSaldoFacturaVista, tvNumchequePend,
                tvfechaConsignacionpe, tvpendiDescripcion, tvclaseDocumento, tvSociedad, tvcodigoCliente, tvcodvendedor, tvfechacierr, tvvalorDocumento, tvmoneda, tvvalorpagado, tvdoctofinan, tvnrorecibo, tvusuario, tvidpago;
        private String conse;
        ImageView editar, eliminar, AceptarPen;
        RadioButton rbEfectvo, rbCheq, rbTrans, rbTarjeta, rbBitcoin;
        TextView textoFormaPago, textoValorPagado;


        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTotalFormasPagoVista = itemView.findViewById(R.id.tvTotalFormasPagoVista);
            tvSaldoFacturaVista = itemView.findViewById(R.id.tvSaldoFacturaVista);
            tvNumchequePend = itemView.findViewById(R.id.tvNumchequePend);
            tvfechaConsignacionpe = itemView.findViewById(R.id.tvfechaConsignacionpe);
            tvpendiDescripcion = itemView.findViewById(R.id.tvpendiDescripcion);
            tvclaseDocumento = itemView.findViewById(R.id.tvclaseDocumento);
            tvSociedad = itemView.findViewById(R.id.tvSociedad);
            tvcodigoCliente = itemView.findViewById(R.id.tvcodigoCliente);
            tvfechacierr = itemView.findViewById(R.id.tvfechacierr);
            tvvalorDocumento = itemView.findViewById(R.id.tvvalorDocumento);
            tvmoneda = itemView.findViewById(R.id.tvmoneda);
            tvvalorpagado = itemView.findViewById(R.id.tvvalorpagado);
            tvdoctofinan = itemView.findViewById(R.id.tvdoctofinan);
            tvnrorecibo = itemView.findViewById(R.id.tvnrorecibo);
            tvusuario = itemView.findViewById(R.id.tvusuario);
            tvidpago = itemView.findViewById(R.id.tvidpago);
            AceptarPen = itemView.findViewById(R.id.AceptarPen);
            editar = itemView.findViewById(R.id.editar);
            textoFormaPago = itemView.findViewById(R.id.textoFormaDePagoPendiente);
            textoValorPagado = itemView.findViewById(R.id.textoValorPagadoPen);

        }

        void binData(final Pendientes item) {

            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


            if (item.getViaPago().equals("A")) {

                tvfechaConsignacionpe.setText(item.getFechaCreacion());
                tvNumchequePend.setText(item.getNumeroCheqe());
                tvpendiDescripcion.setText(item.getObservaciones());
                tvclaseDocumento.setText(item.getClaseDocumento());
                tvSociedad.setText(item.getSociedad());
                tvcodigoCliente.setText(item.getCodigoCliente());
                tvfechacierr.setText(item.getFechaCierre());
                tvmoneda.setText(item.getMoneda());
                tvdoctofinan.setText(item.getDoctoFinanciero());
                tvnrorecibo.setText(item.getNumeroRecibo());
                tvusuario.setText(item.getUsuario());
                tvidpago.setText(item.getIdPago());


                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        textoValorPagado.setText("Amount paid:");
                        textoFormaPago.setText("Form payment:");
                        tvTotalFormasPagoVista.setText("CASH");

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        tvTotalFormasPagoVista.setText("EFECTIVO");
                    }
                }


                String empresa = "";
                empresa = DataBaseBO.cargarEmpresa(context);
                final String finalEmpresa = empresa;


                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                }


                LinearLayout lienarNumCheq = itemView.findViewById(R.id.layoutNumchequePend);
                lienarNumCheq.setVisibility(View.INVISIBLE);
                LinearLayout lienarFechaCon = itemView.findViewById(R.id.layoutfechaConsignacionpe);
                lienarFechaCon.setVisibility(View.INVISIBLE);
                LinearLayout lienarDescrip = itemView.findViewById(R.id.layoutpendiDescripcion);
                lienarDescrip.setVisibility(View.INVISIBLE);
                LinearLayout layoutclaseDocu = itemView.findViewById(R.id.layoutclaseDocu);
                layoutclaseDocu.setVisibility(View.INVISIBLE);
                LinearLayout layoutsociedad = itemView.findViewById(R.id.layoutsociedad);
                layoutsociedad.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodigoCliente = itemView.findViewById(R.id.layoutcodigoCliente);
                layoutcodigoCliente.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodvendedor = itemView.findViewById(R.id.layoutcodvendedor);
                layoutcodvendedor.setVisibility(View.INVISIBLE);
                LinearLayout layoutfechacierr = itemView.findViewById(R.id.layoutfechacierr);
                layoutfechacierr.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorDocumento = itemView.findViewById(R.id.layoutvalorDocumento);
                layoutvalorDocumento.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoneda = itemView.findViewById(R.id.layoutmoneda);
                layoutmoneda.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorpagado = itemView.findViewById(R.id.layoutvalorpagado);
                layoutvalorpagado.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoendaconsig = itemView.findViewById(R.id.layoutmoendaconsig);
                layoutmoendaconsig.setVisibility(View.INVISIBLE);
                LinearLayout layoutdoctofinan = itemView.findViewById(R.id.layoutdoctofinan);
                layoutdoctofinan.setVisibility(View.INVISIBLE);
                LinearLayout layoutnrorecibo = itemView.findViewById(R.id.layoutnrorecibo);
                layoutnrorecibo.setVisibility(View.INVISIBLE);
                LinearLayout layoutusuario = itemView.findViewById(R.id.layoutusuario);
                layoutusuario.setVisibility(View.INVISIBLE);
                LinearLayout layoutidpago = itemView.findViewById(R.id.layoutidpago);
                layoutidpago.setVisibility(View.INVISIBLE);
                LinearLayout layoutbotonAceptar = itemView.findViewById(R.id.layoutbotonAceptar);
                layoutbotonAceptar.setVisibility(View.INVISIBLE);


            }
            if (item.getViaPago().equals("B")) {


                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        textoValorPagado.setText("Amount paid:");
                        textoFormaPago.setText("Form payment:");
                        tvTotalFormasPagoVista.setText("CHECK");

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        tvTotalFormasPagoVista.setText("CHEQUE");
                    }
                }


                tvfechaConsignacionpe.setText(item.getFechaCreacion());
                tvNumchequePend.setText(item.getNumeroCheqe());
                tvpendiDescripcion.setText(item.getObservaciones());
                tvclaseDocumento.setText(item.getClaseDocumento());
                tvSociedad.setText(item.getSociedad());
                tvcodigoCliente.setText(item.getCodigoCliente());
                tvfechacierr.setText(item.getFechaCierre());
                tvmoneda.setText(item.getMoneda());
                tvdoctofinan.setText(item.getDoctoFinanciero());
                tvnrorecibo.setText(item.getNumeroRecibo());
                tvusuario.setText(item.getUsuario());
                tvidpago.setText(item.getIdPago());

                String empresa = "";
                empresa = DataBaseBO.cargarEmpresa(context);
                final String finalEmpresa = empresa;

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                }


                LinearLayout layoutbotonAceptar = itemView.findViewById(R.id.layoutbotonAceptar);
                layoutbotonAceptar.setVisibility(View.INVISIBLE);
                LinearLayout lienarNumCheq = itemView.findViewById(R.id.layoutNumchequePend);
                lienarNumCheq.setVisibility(View.INVISIBLE);
                LinearLayout lienarFechaCon = itemView.findViewById(R.id.layoutfechaConsignacionpe);
                lienarFechaCon.setVisibility(View.INVISIBLE);
                LinearLayout lienarDescrip = itemView.findViewById(R.id.layoutpendiDescripcion);
                lienarDescrip.setVisibility(View.INVISIBLE);
                LinearLayout layoutclaseDocu = itemView.findViewById(R.id.layoutclaseDocu);
                layoutclaseDocu.setVisibility(View.INVISIBLE);
                LinearLayout layoutsociedad = itemView.findViewById(R.id.layoutsociedad);
                layoutsociedad.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodigoCliente = itemView.findViewById(R.id.layoutcodigoCliente);
                layoutcodigoCliente.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodvendedor = itemView.findViewById(R.id.layoutcodvendedor);
                layoutcodvendedor.setVisibility(View.INVISIBLE);
                LinearLayout layoutfechacierr = itemView.findViewById(R.id.layoutfechacierr);
                layoutfechacierr.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorDocumento = itemView.findViewById(R.id.layoutvalorDocumento);
                layoutvalorDocumento.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoneda = itemView.findViewById(R.id.layoutmoneda);
                layoutmoneda.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorpagado = itemView.findViewById(R.id.layoutvalorpagado);
                layoutvalorpagado.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoendaconsig = itemView.findViewById(R.id.layoutmoendaconsig);
                layoutmoendaconsig.setVisibility(View.INVISIBLE);
                LinearLayout layoutdoctofinan = itemView.findViewById(R.id.layoutdoctofinan);
                layoutdoctofinan.setVisibility(View.INVISIBLE);
                LinearLayout layoutnrorecibo = itemView.findViewById(R.id.layoutnrorecibo);
                layoutnrorecibo.setVisibility(View.INVISIBLE);
                LinearLayout layoutusuario = itemView.findViewById(R.id.layoutusuario);
                layoutusuario.setVisibility(View.INVISIBLE);
                LinearLayout layoutidpago = itemView.findViewById(R.id.layoutidpago);
                layoutidpago.setVisibility(View.INVISIBLE);
            }
            if (item.getViaPago().equals("6")) {

                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        textoValorPagado.setText("Amount paid:");
                        textoFormaPago.setText("Form payment:");
                        tvTotalFormasPagoVista.setText("TRANSFER");

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        tvTotalFormasPagoVista.setText("TRANSFERENCIA");

                    }
                }


                tvfechaConsignacionpe.setText(item.getFechaCreacion());
                tvNumchequePend.setText(item.getNumeroCheqe());
                tvpendiDescripcion.setText(item.getObservaciones());
                tvclaseDocumento.setText(item.getClaseDocumento());
                tvSociedad.setText(item.getSociedad());
                tvcodigoCliente.setText(item.getCodigoCliente());
                tvfechacierr.setText(item.getFechaCierre());
                tvmoneda.setText(item.getMoneda());
                tvdoctofinan.setText(item.getDoctoFinanciero());
                tvnrorecibo.setText(item.getNumeroRecibo());
                tvusuario.setText(item.getUsuario());
                tvidpago.setText(item.getIdPago());

                String empresa = "";
                empresa = DataBaseBO.cargarEmpresa(context);
                final String finalEmpresa = empresa;

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                }

                LinearLayout layaoutEditar = itemView.findViewById(R.id.layaoutEditar);
                layaoutEditar.setVisibility(View.INVISIBLE);
                LinearLayout lienarNumCheq = itemView.findViewById(R.id.layoutNumchequePend);
                lienarNumCheq.setVisibility(View.INVISIBLE);
                LinearLayout lienarFechaCon = itemView.findViewById(R.id.layoutfechaConsignacionpe);
                lienarFechaCon.setVisibility(View.INVISIBLE);
                LinearLayout lienarDescrip = itemView.findViewById(R.id.layoutpendiDescripcion);
                lienarDescrip.setVisibility(View.INVISIBLE);
                LinearLayout layoutclaseDocu = itemView.findViewById(R.id.layoutclaseDocu);
                layoutclaseDocu.setVisibility(View.INVISIBLE);
                LinearLayout layoutsociedad = itemView.findViewById(R.id.layoutsociedad);
                layoutsociedad.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodigoCliente = itemView.findViewById(R.id.layoutcodigoCliente);
                layoutcodigoCliente.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodvendedor = itemView.findViewById(R.id.layoutcodvendedor);
                layoutcodvendedor.setVisibility(View.INVISIBLE);
                LinearLayout layoutfechacierr = itemView.findViewById(R.id.layoutfechacierr);
                layoutfechacierr.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorDocumento = itemView.findViewById(R.id.layoutvalorDocumento);
                layoutvalorDocumento.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoneda = itemView.findViewById(R.id.layoutmoneda);
                layoutmoneda.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorpagado = itemView.findViewById(R.id.layoutvalorpagado);
                layoutvalorpagado.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoendaconsig = itemView.findViewById(R.id.layoutmoendaconsig);
                layoutmoendaconsig.setVisibility(View.INVISIBLE);
                LinearLayout layoutdoctofinan = itemView.findViewById(R.id.layoutdoctofinan);
                layoutdoctofinan.setVisibility(View.INVISIBLE);
                LinearLayout layoutnrorecibo = itemView.findViewById(R.id.layoutnrorecibo);
                layoutnrorecibo.setVisibility(View.INVISIBLE);
                LinearLayout layoutusuario = itemView.findViewById(R.id.layoutusuario);
                layoutusuario.setVisibility(View.INVISIBLE);
                LinearLayout layoutidpago = itemView.findViewById(R.id.layoutidpago);
                layoutidpago.setVisibility(View.INVISIBLE);
            }
            if (item.getViaPago().equals("O")) {

                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        textoValorPagado.setText("Amount paid:");
                        textoFormaPago.setText("Form payment:");
                        tvTotalFormasPagoVista.setText("CARD");

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        tvTotalFormasPagoVista.setText("TARJETA");
                    }
                }


                tvfechaConsignacionpe.setText(item.getFechaCreacion());
                tvNumchequePend.setText(item.getNumeroCheqe());
                tvpendiDescripcion.setText(item.getObservaciones());
                tvclaseDocumento.setText(item.getClaseDocumento());
                tvSociedad.setText(item.getSociedad());
                tvcodigoCliente.setText(item.getCodigoCliente());
                tvfechacierr.setText(item.getFechaCierre());
                tvmoneda.setText(item.getMoneda());
                tvdoctofinan.setText(item.getDoctoFinanciero());
                tvnrorecibo.setText(item.getNumeroRecibo());
                tvusuario.setText(item.getUsuario());
                tvidpago.setText(item.getIdPago());

                String empresa = "";
                empresa = DataBaseBO.cargarEmpresa(context);
                final String finalEmpresa = empresa;

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                }

                LinearLayout layaoutEditar = itemView.findViewById(R.id.layaoutEditar);
                layaoutEditar.setVisibility(View.INVISIBLE);
                LinearLayout lienarNumCheq = itemView.findViewById(R.id.layoutNumchequePend);
                lienarNumCheq.setVisibility(View.INVISIBLE);
                LinearLayout lienarFechaCon = itemView.findViewById(R.id.layoutfechaConsignacionpe);
                lienarFechaCon.setVisibility(View.INVISIBLE);
                LinearLayout lienarDescrip = itemView.findViewById(R.id.layoutpendiDescripcion);
                lienarDescrip.setVisibility(View.INVISIBLE);
                LinearLayout layoutclaseDocu = itemView.findViewById(R.id.layoutclaseDocu);
                layoutclaseDocu.setVisibility(View.INVISIBLE);
                LinearLayout layoutsociedad = itemView.findViewById(R.id.layoutsociedad);
                layoutsociedad.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodigoCliente = itemView.findViewById(R.id.layoutcodigoCliente);
                layoutcodigoCliente.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodvendedor = itemView.findViewById(R.id.layoutcodvendedor);
                layoutcodvendedor.setVisibility(View.INVISIBLE);
                LinearLayout layoutfechacierr = itemView.findViewById(R.id.layoutfechacierr);
                layoutfechacierr.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorDocumento = itemView.findViewById(R.id.layoutvalorDocumento);
                layoutvalorDocumento.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoneda = itemView.findViewById(R.id.layoutmoneda);
                layoutmoneda.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorpagado = itemView.findViewById(R.id.layoutvalorpagado);
                layoutvalorpagado.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoendaconsig = itemView.findViewById(R.id.layoutmoendaconsig);
                layoutmoendaconsig.setVisibility(View.INVISIBLE);
                LinearLayout layoutdoctofinan = itemView.findViewById(R.id.layoutdoctofinan);
                layoutdoctofinan.setVisibility(View.INVISIBLE);
                LinearLayout layoutnrorecibo = itemView.findViewById(R.id.layoutnrorecibo);
                layoutnrorecibo.setVisibility(View.INVISIBLE);
                LinearLayout layoutusuario = itemView.findViewById(R.id.layoutusuario);
                layoutusuario.setVisibility(View.INVISIBLE);
                LinearLayout layoutidpago = itemView.findViewById(R.id.layoutidpago);
                layoutidpago.setVisibility(View.INVISIBLE);
            }
            if (item.getViaPago().equals("4")) {
                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        textoValorPagado.setText("Amount paid:");
                        textoFormaPago.setText("Form payment:");
                        tvTotalFormasPagoVista.setText("BITCOIN");

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        tvTotalFormasPagoVista.setText("BITCOIN");
                    }
                }


                tvfechaConsignacionpe.setText(item.getFechaCreacion());
                tvNumchequePend.setText(item.getNumeroCheqe());
                tvpendiDescripcion.setText(item.getObservaciones());
                tvclaseDocumento.setText(item.getClaseDocumento());
                tvSociedad.setText(item.getSociedad());
                tvcodigoCliente.setText(item.getCodigoCliente());
                tvfechacierr.setText(item.getFechaCierre());
                tvmoneda.setText(item.getMoneda());
                tvdoctofinan.setText(item.getDoctoFinanciero());
                tvnrorecibo.setText(item.getNumeroRecibo());
                tvusuario.setText(item.getUsuario());
                tvidpago.setText(item.getIdPago());

                String empresa = "";
                empresa = DataBaseBO.cargarEmpresa(context);
                final String finalEmpresa = empresa;

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvSaldoFacturaVista.setText(formatoNumero.format(item.getMontoPendientes()));
                    tvvalorDocumento.setText(formatoNumero.format(item.getValorDocumento()));
                    tvvalorpagado.setText(formatoNumero.format(item.getMontoPendientes()));

                }

                LinearLayout layaoutEditar = itemView.findViewById(R.id.layaoutEditar);
                layaoutEditar.setVisibility(View.INVISIBLE);
                LinearLayout lienarNumCheq = itemView.findViewById(R.id.layoutNumchequePend);
                lienarNumCheq.setVisibility(View.INVISIBLE);
                LinearLayout lienarFechaCon = itemView.findViewById(R.id.layoutfechaConsignacionpe);
                lienarFechaCon.setVisibility(View.INVISIBLE);
                LinearLayout lienarDescrip = itemView.findViewById(R.id.layoutpendiDescripcion);
                lienarDescrip.setVisibility(View.INVISIBLE);
                LinearLayout layoutclaseDocu = itemView.findViewById(R.id.layoutclaseDocu);
                layoutclaseDocu.setVisibility(View.INVISIBLE);
                LinearLayout layoutsociedad = itemView.findViewById(R.id.layoutsociedad);
                layoutsociedad.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodigoCliente = itemView.findViewById(R.id.layoutcodigoCliente);
                layoutcodigoCliente.setVisibility(View.INVISIBLE);
                LinearLayout layoutcodvendedor = itemView.findViewById(R.id.layoutcodvendedor);
                layoutcodvendedor.setVisibility(View.INVISIBLE);
                LinearLayout layoutfechacierr = itemView.findViewById(R.id.layoutfechacierr);
                layoutfechacierr.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorDocumento = itemView.findViewById(R.id.layoutvalorDocumento);
                layoutvalorDocumento.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoneda = itemView.findViewById(R.id.layoutmoneda);
                layoutmoneda.setVisibility(View.INVISIBLE);
                LinearLayout layoutvalorpagado = itemView.findViewById(R.id.layoutvalorpagado);
                layoutvalorpagado.setVisibility(View.INVISIBLE);
                LinearLayout layoutmoendaconsig = itemView.findViewById(R.id.layoutmoendaconsig);
                layoutmoendaconsig.setVisibility(View.INVISIBLE);
                LinearLayout layoutdoctofinan = itemView.findViewById(R.id.layoutdoctofinan);
                layoutdoctofinan.setVisibility(View.INVISIBLE);
                LinearLayout layoutnrorecibo = itemView.findViewById(R.id.layoutnrorecibo);
                layoutnrorecibo.setVisibility(View.INVISIBLE);
                LinearLayout layoutusuario = itemView.findViewById(R.id.layoutusuario);
                layoutusuario.setVisibility(View.INVISIBLE);
                LinearLayout layoutidpago = itemView.findViewById(R.id.layoutidpago);
                layoutidpago.setVisibility(View.INVISIBLE);
            }

            final ArrayList<Pendientes> listaPendientes = new ArrayList<Pendientes>();


            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressView.getInstance().Dismiss();

                    v.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 600);


                    PreferencesFotos.vaciarPreferencesFotoSeleccionada(context);


                    if (item.viaPago.equals("A") && item.idPago.equals(item.getIdPago())) {

                        ArrayList<Pendientes> listafacturasPendientes = new ArrayList<Pendientes>();
                        listaPendientes.add(item);
                        pendientesFacturas.facturaCartera(listaPendientes);

                        int position = itemView.getVerticalScrollbarPosition();
                        Pendientes pendientesSeleccionada = listaPendientes.get(position);

                        Gson gson = new Gson();
                        String jsonStringObject = gson.toJson(pendientesSeleccionada);
                        PreferencesFacturaPendientesSeleccionada.guardarFacturasPendiSeleccionado(context, jsonStringObject);

                        MetodoDePagoEfectivoPendientes.vistaDialogoEfectivo(v.getContext(), "Exito", "Información enviada correctamente",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        MetodoDePagoEfectivoPendientes.dialogo.cancel();


                                    }
                                }, null);

                    } else if (item.viaPago.equals("B") && item.idPago.equals(item.getIdPago())) {

                        ArrayList<Facturas> listafacturas = new ArrayList<Facturas>();
                        listaPendientes.add(item);
                        pendientesFacturas.facturaCartera(listaPendientes);


                        int position = itemView.getVerticalScrollbarPosition();
                        Pendientes pendientesSeleccionada = listaPendientes.get(position);
                        Gson gson = new Gson();
                        String jsonStringObject = gson.toJson(pendientesSeleccionada);
                        PreferencesFacturaPendientesSeleccionada.guardarFacturasPendiSeleccionado(context, jsonStringObject);

                        MetodoDePagoChequePendientes.vistaDialogoCheque(v.getContext(), "Exito", "Información enviada correctamente",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        MetodoDePagoChequePendientes.dialogo.cancel();


                                    }
                                }, null);

                    }


                }
            });

            AceptarPen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    view.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    }, 600);


                    ArrayList<Facturas> listafacturas = new ArrayList<Facturas>();
                    listaPendientes.add(item);

                    pendientesFacturas.facturaCartera(listaPendientes);
                    int position = itemView.getVerticalScrollbarPosition();
                    Pendientes pendientesSeleccionada = listaPendientes.get(position);
                    pendientesSeleccionada.getIdPago();
                    final List<String> numeroRecibosMultiples = new ArrayList<>();
                    final List<Integer> consecutivosMultiples = new ArrayList<>();
                    final List<String> idPagosUnicosElminiar = new ArrayList<>();

                    Gson gson1 = new Gson();
                    String stringJsonObject1 = PreferencesFacturasMultiplesPendientes.obtenerFacturasMultiplesPendientesSeleccionado(context);
                    multipleestado = gson1.fromJson(stringJsonObject1, MultiplesEstado.class);

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


                    if (multipleestado == null) {
                        for (Pendientes fac : facCollection) {
                            numeroRecibosMultiples.add(fac.getNumeroRecibo());
                            consecutivosMultiples.add(fac.getConsecutivo());
                            idPagosUnicosElminiar.add(fac.getIdPago());

                        }

                        cargarFacturasPendientesCompleta = DataBaseBO.cargarFacturasPendientesCompletaPorid(pendientesSeleccionada.getIdPago(), context);
                    } else if (multipleestado != null) {
                        for (Pendientes fac : facCollectionPendientes) {
                            numeroRecibosMultiples.add(fac.getNumeroRecibo());
                            consecutivosMultiples.add(fac.getConsecutivo());
                            idPagosUnicosElminiar.add(fac.getIdPago());

                        }

                        pendientesSeleccionada.getViaPago();

                        cargarFacturasPendientesCompletaMultiples = DataBaseBO.cargarFacturasPendientesCompletaPoridMultiples(numeroRecibosMultiples, context);
                    }


                    String claseDocumento, idPago = "", sociedad = "", codigoCliente = "", codigoVendedor = "", doctoFinanciero, numeroRecibo = "",
                            referencia = "", fecha, nroAnulacion, idenFoto = "";
                    int consecutivo = 0;
                    double valorDocumento;
                    String fechaCreacion = "", fechaCierre = "";
                    String fechaRecibo = "";
                    double montoPendientes = 0;
                    double valorConsignado = 0;
                    double saldoAfavor = 0;
                    String cuentaBancaria = "", monedaConsiganada = "", moneda = "", comprobanteFiscal = "", observaciones = "", observacioneMotivo;
                    String viaPago = "", usuario = "", operacionCME = "", sincronizado = "", bancoPendientes = "", numeroCheqe = "", nombrePropietario = "";
                    String consec = "", numeroAnulacion = "";
                    String negocio = "";
                    String vendedor = "";

                    String consecId = "", numeroAnulacionId = "";
                    String negocioId = "";
                    String vendedorId = "";
                    String consecutivoidFInal = "";
                    double preciosPendientesMultiples = 0;


                    List<String> claseDocumentos = new ArrayList<>();
                    List<String> sociedades = new ArrayList<>();
                    List<String> codigoClientes = new ArrayList<>();
                    List<String> codigoVendedores = new ArrayList<>();
                    List<String> doctoFinancieros = new ArrayList<>();
                    List<String> numeroRecibos = new ArrayList<>();
                    List<Integer> consecutivos = new ArrayList<>();
                    List<String> valorDocumentos = new ArrayList<>();
                    List<String> idsPagos = new ArrayList<>();
                    final ArrayList<Pendientes> listaPendientes = new ArrayList<Pendientes>();
                    final List<String> fechaCreacions = new ArrayList<>();
                    final List<String> fechaCierres = new ArrayList<>();
                    final List<String> fechasRecibos = new ArrayList<>();
                    final List<String> montoPendientess = new ArrayList<>();
                    final List<String> referencias = new ArrayList<>();
                    final List<String> valorConsignados = new ArrayList<>();
                    final List<String> saldosAfavor = new ArrayList<>();
                    final List<String> cuentaBancarias = new ArrayList<>();
                    final List<String> monedaConsiganadas = new ArrayList<>();
                    final List<String> monedas = new ArrayList<>();
                    final List<String> comprobanteFiscals = new ArrayList<>();
                    final List<String> observacioness = new ArrayList<>();
                    final List<String> observacionesMotivos = new ArrayList<>();
                    final List<String> viaPagos = new ArrayList<>();
                    final List<String> usuarios = new ArrayList<>();
                    final List<String> operacionCMEs = new ArrayList<>();
                    final List<String> sincronizados = new ArrayList<>();
                    final List<String> bancoPendientess = new ArrayList<>();
                    final List<String> numeroCheqes = new ArrayList<>();
                    final List<String> nombrePropietarios = new ArrayList<>();
                    final List<String> consecutivoidLista = new ArrayList<>();
                    final List<String> preciosMultiples = new ArrayList<>();
                    final List<String> idenFotos = new ArrayList<>();
                    final List<String> numeroAnulacionIds = new ArrayList<>();


                    if (multipleestado == null) {
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
                            consecutivo = pendientes1.getConsecutivo();
                            consecutivos.add(consecutivo);
                            valorDocumento = pendientes1.getValorDocumento();
                            valorDocumentos.add(String.valueOf(valorDocumento));
                            idPago = pendientes1.getIdPago();
                            idsPagos.add(idPago);
                            referencia = pendientes1.getReferencia();
                            referencias.add(pendientes1.getReferencia());
                            fechaCreacion = pendientes1.getFechaCreacion();
                            fechaCreacions.add(fechaCreacion);
                            fechaCierre = pendientes1.getFechaCierre();
                            fechaCierres.add(fechaCierre);
                            fechaRecibo = pendientes1.getFechaRecibo();
                            fechasRecibos.add(fechaRecibo);
                            moneda = pendientes1.getMoneda();
                            monedas.add(moneda);
                            montoPendientes = pendientes1.getMontoPendientes();
                            montoPendientess.add(String.valueOf(montoPendientes));
                            valorConsignado = pendientes1.getValorConsignado();
                            valorConsignados.add(String.valueOf(valorConsignado));
                            saldosAfavor.add(String.valueOf(pendientes1.saldoAfavor));
                            cuentaBancaria = pendientes1.getCuentaBancaria();
                            cuentaBancarias.add(cuentaBancaria);
                            monedaConsiganada = pendientes1.getMonedaConsiganada();
                            monedaConsiganadas.add(monedaConsiganada);
                            comprobanteFiscal = pendientes1.getComprobanteFiscal();
                            comprobanteFiscals.add(comprobanteFiscal);
                            operacionCME = pendientes1.getOperacionCME();
                            operacionCMEs.add(operacionCME);
                            observaciones = pendientes1.getObservaciones();
                            observacioneMotivo = pendientes1.getObservacionesMotivo();
                            viaPago = pendientes1.getViaPago();
                            usuario = pendientes1.getUsuario();
                            observacioness.add(observaciones);
                            observacionesMotivos.add(observacioneMotivo);
                            bancoPendientess.add(pendientes1.getBanco());
                            viaPagos.add(viaPago);
                            usuarios.add(usuario);
                            idenFoto = pendientes1.getIdenFoto();
                            idenFotos.add(pendientes1.getIdenFoto());
                            consecutivoidLista.add(pendientes1.getConsecutivoidFac());
                            numeroAnulacionIds.add(pendientes1.getConsecutivoidFac());

                        }

                    } else if (multipleestado != null) {

                        preciosPendientesMultiples = DataBaseBO.cargarFacturasParametroPendientesTransferenciaMultiplesValorPendiente(numeroRecibosMultiples, context);

                        for (Pendientes pendientes1 : cargarFacturasPendientesCompletaMultiples) {
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
                            consecutivo = pendientes1.getConsecutivo();
                            consecutivos.add(consecutivo);
                            valorDocumento = pendientes1.getValorDocumento();
                            valorDocumentos.add(String.valueOf(valorDocumento));
                            idPago = pendientes1.getIdPago();
                            idsPagos.add(idPago);
                            referencia = pendientes1.getReferencia();
                            referencias.add(pendientes1.getReferencia());
                            fechaCreacion = pendientes1.getFechaCreacion();
                            fechaCreacions.add(fechaCreacion);
                            fechaCierre = pendientes1.getFechaCierre();
                            fechaCierres.add(fechaCierre);
                            fechaRecibo = pendientes1.getFechaRecibo();
                            fechasRecibos.add(fechaRecibo);
                            moneda = pendientes1.getMoneda();
                            monedas.add(moneda);
                            montoPendientes = pendientes1.getMontoPendientes();
                            montoPendientess.add(String.valueOf(montoPendientes));
                            valorConsignado = pendientes1.getValorConsignado();
                            valorConsignados.add(String.valueOf(valorConsignado));
                            saldosAfavor.add(String.valueOf(pendientes1.saldoAfavor));
                            cuentaBancaria = pendientes1.getCuentaBancaria();
                            cuentaBancarias.add(cuentaBancaria);
                            monedaConsiganada = pendientes1.getMonedaConsiganada();
                            monedaConsiganadas.add(monedaConsiganada);
                            comprobanteFiscal = pendientes1.getComprobanteFiscal();
                            comprobanteFiscals.add(comprobanteFiscal);
                            operacionCME = pendientes1.getOperacionCME();
                            operacionCMEs.add(operacionCME);
                            observaciones = pendientes1.getObservaciones();
                            observacioneMotivo = pendientes1.getObservacionesMotivo();
                            viaPago = pendientes1.getViaPago();
                            usuario = pendientes1.getUsuario();
                            observacioness.add(observaciones);
                            observacionesMotivos.add(observacioneMotivo);
                            bancoPendientess.add(pendientes1.getBanco());
                            viaPagos.add(viaPago);
                            usuarios.add(usuario);
                            idenFotos.add(pendientes1.getIdenFoto());
                            numeroAnulacionIds.add(pendientes1.getConsecutivoidFac());
                            idenFoto = pendientes1.getIdenFoto();
                            consecutivoidLista.add(pendientes1.getConsecutivoidFac());
//                            preciosPendientesMultiples += pendientes1.getMontoPendientes();

                        }
                        for (int i = 0; i < cargarFacturasPendientesCompletaMultiples.size(); i++) {
                            preciosMultiples.add(String.valueOf(preciosPendientesMultiples));
                        }

                    }


                    /***   DataBaseBO.guardarFormaPagPendientes(idPago, claseDocumentos,
                     sociedad, codigoCliente, codigoVendedor,
                     referencia, fechaCreacions,
                     pendientesSeleccionada.getFechaCierre(), valorDocumentos,
                     pendientesSeleccionada.getMoneda(), montoPendientess, valorConsignados, cuentaBancaria,
                     pendientesSeleccionada.getMonedaConsiganada(), pendientesSeleccionada.getComprobanteFiscal(), doctoFinancieros,
                     numeroRecibo,
                     pendientesSeleccionada.getObservaciones(), pendientesSeleccionada.getViaPago(), pendientesSeleccionada.getUsuario(), pendientesSeleccionada.getOperacionCME(),
                     0, bancoPendientes, "0",
                     "0",idenFoto);**/


                    if (multipleestado == null) {

                        //CARGAR CONSUCUTIVO ID
                        final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                        String consecId1 = "", numeroAnulacionId1 = "";
                        String negocioId1 = "";
                        String vendedorId1 = "";

                        consecId1 = DataBaseBO.cargarConsecutivoId(context);
                        negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(context);
                        vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(context);

                        int consec1Id = Integer.parseInt(consecId1);
                        int vendedorsumId = Integer.parseInt(vendedorId1);
                        int contadorId = 1;
                        consec1Id = consec1Id + contadorId;
                        numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                          DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, context);

                        if (DataBaseBO.guardarFormaPagPendientes(idPago, claseDocumentos,
                                sociedad, codigoCliente, codigoVendedor,
                                referencia, fechaCreacions,
                                pendientesSeleccionada.getFechaCierre(), valorDocumentos,
                                pendientesSeleccionada.getMoneda(), montoPendientess, valorConsignados,saldosAfavor, cuentaBancaria,
                                pendientesSeleccionada.getMonedaConsiganada(),
                                pendientesSeleccionada.getComprobanteFiscal(), doctoFinancieros,
                                numeroRecibo,
                                pendientesSeleccionada.getObservaciones(), pendientesSeleccionada.getViaPago(),
                                pendientesSeleccionada.getUsuario(), operacionCMEs,
                                0, bancoPendientes, "0",
                                "0", idenFoto, numeroAnulacionId1, consecutivo, pendientesSeleccionada.observacionesMotivo,fechaRecibo, context)) {

                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(context, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(context, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                }
                            }

                        } else {

                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(context, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(context, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                }
                            }
                        }

                    } else if (multipleestado != null) {

                        final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                        consec = DataBaseBO.cargarConsecutivoPaquete(context);
                        negocio = DataBaseBO.cargarNegocioConsecutivoId(context);
                        vendedor = DataBaseBO.cargarVendedorConsecutivoId(context);

                        int consec1 = Integer.parseInt(consec);
                        int vendedorsum = Integer.parseInt(vendedor);
                        int contador = 1;
                        consec1 = consec1 + contador;
                        numeroAnulacion = String.valueOf(negocio + vendedor + consec1);

                        //CARGAR CONSUCUTIVO ID
                        consecId = DataBaseBO.cargarConsecutivoId(context);
                        negocioId = DataBaseBO.cargarNegocioConsecutivoPaquete(context);
                        vendedorId = DataBaseBO.cargarVendedorConsecutivoPaquete(context);

                        int consec1Id = Integer.parseInt(consecId);
                        int vendedorsumId = Integer.parseInt(vendedor);
                        int contadorId = 1;
                        consec1Id = consec1Id + contadorId;

                        numeroAnulacionId = String.valueOf(negocioId + vendedorId + consec1Id);


                        // DataBaseBO.guardarConsecutivoPaquete(negocio, vendedorsum, consec1, fechacon);

                        //GUARDAR CONSECUTIVO ID
                         DataBaseBO.guardarConsecutivoId(negocioId, vendedorsumId, consec1Id, fechacon, context);

                        if (DataBaseBO.guardarFormaPagPendientesMultiplesReferencias(idsPagos, claseDocumentos,
                                sociedad, codigoClientes, codigoVendedor,
                                referencias, fechaCreacions,
                                pendientesSeleccionada.getFechaCierre(), valorDocumentos,
                                pendientesSeleccionada.getMoneda(), montoPendientess, preciosMultiples,saldosAfavor, cuentaBancarias,
                                pendientesSeleccionada.getMonedaConsiganada(), pendientesSeleccionada.getComprobanteFiscal(), doctoFinancieros,
                                numeroRecibos, observacioness, viaPagos,
                                pendientesSeleccionada.getUsuario(), operacionCMEs,
                                0, bancoPendientess, "0",
                                "0", idenFotos, numeroAnulacion, numeroAnulacionIds,valorConsignados, consecutivos,fechasRecibos, observacionesMotivos, context)) {


                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(context, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();

                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(context, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                }
                            }

                        } else {
                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(context, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(context, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                }
                            }
                        }

                    }

                    //   DataBaseBO.eliminarRecaudosTotalPendientes(idsPagos);
                    Intent login = new Intent(context.getApplicationContext(), MetodosDePagoPendientesActivity.class);
                    context.startActivity(login);
                    ((MetodosDePagoPendientesActivity) context).finish();


                }
            });

            pendientesFacturas.facturaCartera(listaPendientes);

        }

    }

    public static interface facturaCarteraPendientes {


        Serializable facturaCartera(List<Pendientes> pendientes);
    }

}
