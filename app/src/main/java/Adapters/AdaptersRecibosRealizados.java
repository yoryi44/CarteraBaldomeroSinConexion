package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.FacturasRealizadasSeleccionadasActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.FacturasRealizadas;
import dataobject.Lenguaje;
import sharedpreferences.PreferencesFacRealizadasSeleccionadas;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Utilidades;

public class AdaptersRecibosRealizados extends RecyclerView.Adapter<AdaptersRecibosRealizados.ViewHolder> implements View.OnClickListener {

    public Context context;
    private View.OnClickListener listener;
    public List<FacturasRealizadas> facturas;
    private AdaptersRecibosRealizados.facturaCarteraRealizados facturaCarteraRealizados;
    private List<FacturasRealizadas> cargarFacturasRealizadas;
    private Lenguaje lenguajeElegido;

    public AdaptersRecibosRealizados(List<FacturasRealizadas> facturas, Context context) {
        this.context = context;
        this.facturas = facturas;

        try {
            this.facturaCarteraRealizados = (AdaptersRecibosRealizados.facturaCarteraRealizados) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_facturas_realizadas, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView documento, tipo, fecha, dias, monto, tituloNumeroReciboRealizadas,
                tituloCodClienteRealizadas, tituloFechaCreacionRealizadas, tituloMontoRealizados, tvMontoFactuasRealizadasSigno;
        private Button seleccion;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            documento = itemView.findViewById(R.id.tvDocumentoFacturasRealizadas);
            tipo = itemView.findViewById(R.id.tvTipoFacturasRealizadas);
            fecha = itemView.findViewById(R.id.tvFechaVenciFacturasRealizadas);
            monto = itemView.findViewById(R.id.tvMontoFactuasRealizadas);
            seleccion = itemView.findViewById(R.id.btnSeleccionPendientes);
            tituloNumeroReciboRealizadas = itemView.findViewById(R.id.tituloNumeroReciboRealizadas);
            tituloCodClienteRealizadas = itemView.findViewById(R.id.tituloCodClienteRealizadas);
            tituloFechaCreacionRealizadas = itemView.findViewById(R.id.tituloFechaCreacionRealizadas);
            tituloMontoRealizados = itemView.findViewById(R.id.tituloMontoRealizados);
            tvMontoFactuasRealizadasSigno = itemView.findViewById(R.id.tvMontoFactuasRealizadasSigno);


        }

        void binData(final FacturasRealizadas item) {
            documento.setText(item.getNumeroRecibo());
            tipo.setText(item.getCodigoCliente());
            fecha.setText(item.getFechaCierre());

            Gson gson223 = new Gson();
            String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    tituloNumeroReciboRealizadas.setText("Consecutive:");
                    tituloCodClienteRealizadas.setText("Customer Code:");
                    tituloFechaCreacionRealizadas.setText("Creation Date:");
                    tituloMontoRealizados.setText("Amount:");

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                }
            }

            String empresa = "";
            empresa = DataBaseBO.cargarEmpresa(context);
            final String finalEmpresa = empresa;

            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                monto.setText(formatoNumero.format(item.getMontoPendientes()));

            } else {

                if (finalEmpresa.equals("AGUC"))
                {
                    tvMontoFactuasRealizadasSigno.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvMontoFactuasRealizadasSigno.setVisibility(View.GONE);
                }

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                monto.setText(formatoNumero.format(item.getMontoPendientes()));


            }

            final ArrayList<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<FacturasRealizadas>();

            listaFacturasRealizadas.add(item);

            seleccion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = itemView.getVerticalScrollbarPosition();
                    FacturasRealizadas realizadas = listaFacturasRealizadas.get(position);

                    facturaCarteraRealizados.facturaCarteraRealizados(listaFacturasRealizadas);


                    cargarFacturasRealizadas = DataBaseBO.cargarFacturasRealizadasCompletaInformes(realizadas.getNumeroRecibo(), context);

                    Gson gson = new Gson();
                    String jsonStringObject = gson.toJson(cargarFacturasRealizadas);
                    PreferencesFacRealizadasSeleccionadas.guardarFacRealizadasSeleccionadas(context, jsonStringObject);

                    Intent vistaClienteActivity = new Intent(context, FacturasRealizadasSeleccionadasActivity.class);
                    context.startActivity(vistaClienteActivity);
                }
            });
        }

    }

    public static interface facturaCarteraRealizados {


        Serializable facturaCarteraRealizados(List<FacturasRealizadas> facturasRealizadas);
    }


}
