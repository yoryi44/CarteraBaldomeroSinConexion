package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.com.celuweb.carterabaldomero.R;
import dataobject.Cartera;
import utilidades.Utilidades;

public class VistaCarteraAdapter extends RecyclerView.Adapter<VistaCarteraAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Cartera> listaCarteraCliente;
    private ItemClickListener mClickListener;
    private int tipo;
    private Context contexto;

    // data is passed into the constructor
    public VistaCarteraAdapter(Context contexto, List<Cartera> listaCarteraCliente, int tipo) {

        this.mInflater = LayoutInflater.from(contexto);
        this.listaCarteraCliente = listaCarteraCliente;
        this.contexto = contexto;
        this.tipo = tipo;
    }

    @NonNull
    @Override
    public VistaCarteraAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.vista_cartera_cliente, parent, false);
        return new VistaCarteraAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VistaCarteraAdapter.ViewHolder holder, final int position) {

        final Cartera facturaCartera = listaCarteraCliente.get(position);

        if(tipo == 1) {

            holder.llIconoGestion.setVisibility(View.GONE);
        }

        if(facturaCartera.indicador != 1 || facturaCartera.registrado) {

            // CON GESTION
            holder.ivIndicadorMarcaCartera.setImageResource(R.mipmap.iconoprogramado);
        } else {

            // SIN GESTION
            holder.ivIndicadorMarcaCartera.setImageResource(R.mipmap.icononoprogramado);

        }

        holder.tvNumeroDocumentoCartera.setText(facturaCartera.documento);
        holder.tvDiasCartera.setText("" + facturaCartera.dias);
        holder.tvVencCartera.setText(facturaCartera.fechaVencto);
        holder.tvCondicionPagoCartera.setText(facturaCartera.condPago);
        holder.tvDescripcionCartera.setText(facturaCartera.descripcion);
        holder.tvValorFactura.setText(Utilidades.separarMilesSinDecimal(String.valueOf(facturaCartera.saldo)));
    }

    @Override
    public int getItemCount() {

        return listaCarteraCliente.size();
    }

    public void eliminarElemento(int position){

        listaCarteraCliente.remove(position);
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivIndicadorMarcaCartera;
        TextView tvNumeroDocumentoCartera;
        TextView tvDiasCartera;
        TextView tvVencCartera;
        TextView tvCondicionPagoCartera;
        TextView tvDescripcionCartera;
        TextView tvValorFactura;

        LinearLayout llIconoGestion;

        ViewHolder(View itemView) {

            super(itemView);

            ivIndicadorMarcaCartera  = itemView.findViewById(R.id.ivIndicadorMarcaCartera);
            tvNumeroDocumentoCartera = itemView.findViewById(R.id.tvNumeroDocumentoCartera);
            tvDiasCartera            = itemView.findViewById(R.id.tvDiasCartera);
            tvVencCartera            = itemView.findViewById(R.id.tvVencCartera);
            tvCondicionPagoCartera   = itemView.findViewById(R.id.tvCondicionPagoCartera);
            tvDescripcionCartera     = itemView.findViewById(R.id.tvDescripcionCartera);
            tvValorFactura           = itemView.findViewById(R.id.tvValorFactura);
            llIconoGestion           = itemView.findViewById(R.id.llIconoGestion);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {

        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {

        void onItemClick(View view, int position);
    }
}
