package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.FacturasSeleccionadasPendientesActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Cartera;
import dataobject.Pendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;

public class AdapterFacturasSeleccionMultiplePendientes extends RecyclerView.Adapter<AdapterFacturasSeleccionMultiplePendientes.ViewHolder> implements View.OnClickListener {

    public Context context;
    public List<Pendientes> pendientes;
    private View.OnClickListener listener;

    public AdapterFacturasSeleccionMultiplePendientes(List<Pendientes> pendientes, Context context) {

        this.context = context;
        this.pendientes = pendientes;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_adapter_multiples_pendientes, parent, false);
        view.setOnClickListener(this);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

        private TextView numeroRecibo, nombreComercial, totalCancelado, metodoDePago;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numeroRecibo = itemView.findViewById(R.id.tvNumeroReciboPendientes);
            nombreComercial = itemView.findViewById(R.id.tvNombreComercialPendientes);
            totalCancelado = itemView.findViewById(R.id.tvTotalCanceladoPendientes);
            metodoDePago = itemView.findViewById(R.id.tvMetodoPagoPendientes);
        }

        void binData(final Pendientes item) {


            nombreComercial.setText(item.getNombrePropietario());


            numeroRecibo.setText(item.getNumeroRecibo());


            String empresa = "";
            empresa = DataBaseBO.cargarEmpresa(context);
            final String finalEmpresa = empresa;

            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                totalCancelado.setText(formatoNumero.format(item.getMontoPendientes()));

            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                totalCancelado.setText(formatoNumero.format(item.getMontoPendientes()));

            }

            metodoDePago.setText(item.getViaPago());

        }
    }

}
