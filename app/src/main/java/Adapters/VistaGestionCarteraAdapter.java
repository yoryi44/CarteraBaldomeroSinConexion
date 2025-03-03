package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.com.celuweb.carterabaldomero.R;
import dataobject.Cartera;
import es.dmoral.toasty.Toasty;
import utilidades.Utilidades;

public class VistaGestionCarteraAdapter extends RecyclerView.Adapter<VistaGestionCarteraAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Cartera> listaCarteraCliente;
    protected List<Cartera> listaCarteraSeleccionadas;
    private VistaGestionCarteraAdapter.ItemClickListener mClickListener;
    private int tipo;
    private Context contexto;

    // data is passed into the constructor
    public VistaGestionCarteraAdapter(Context contexto, List<Cartera> listaCarteraCliente ,int tipo) {

        this.mInflater = LayoutInflater.from(contexto);
        this.listaCarteraCliente = listaCarteraCliente;
        this.contexto = contexto;
        this.tipo = tipo;
        this.listaCarteraSeleccionadas = new ArrayList<>();
    }

    @NonNull
    @Override
    public VistaGestionCarteraAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.vista_gestion_cartera_cliente, parent, false);
        return new VistaGestionCarteraAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VistaGestionCarteraAdapter.ViewHolder holder, final int position) {

        final Cartera facturaCartera = listaCarteraCliente.get(position);

        if (tipo == 1) {

            holder.llIconoGestion.setVisibility(View.GONE);
        }

        if (facturaCartera.indicador != 1 || facturaCartera.registrado) {

            // CON GESTION
            holder.ivIndicadorMarcaCartera.setImageResource(R.mipmap.iconoprogramado);
            holder.tvValorFactura.setTextColor(contexto.getResources().getColor(R.color.colorVerdeLista));
       //     holder.tvValorFactura.setText(Utilidades.separarMilesSinDecimal(Utilidades.Redondear(String.valueOf(facturaCartera.valorReacudoProgramado),0)));

        } else {

            // SIN GESTION
            holder.ivIndicadorMarcaCartera.setImageResource(R.mipmap.icononoprogramado);
            holder.tvValorFactura.setText(Utilidades.separarMilesSinDecimal(String.valueOf(facturaCartera.saldo)));
            holder.tvValorFactura.setTextColor(contexto.getResources().getColor(R.color.colorBotonNegro));

        }

        holder.tvNumeroDocumentoCartera.setText(facturaCartera.documento);
        holder.tvDiasCartera.setText(String.valueOf(facturaCartera.dias));
        holder.tvVencCartera.setText(facturaCartera.fechaVencto);
        holder.tvCondicionPagoCartera.setText(facturaCartera.condPago);
        holder.tvDescripcionCartera.setText(facturaCartera.descripcion);

        holder.cbDocumentoCarteraSeleccionado.setChecked(facturaCartera.facturaSeleccionadaGestion);
    }

    @Override
    public int getItemCount() {

        return listaCarteraCliente.size();
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
        CheckBox cbDocumentoCarteraSeleccionado;

        LinearLayout llIconoGestion;

        ViewHolder(View itemView) {

            super(itemView);

            ivIndicadorMarcaCartera = itemView.findViewById(R.id.ivIndicadorMarcaCartera);
            tvNumeroDocumentoCartera = itemView.findViewById(R.id.tvNumeroDocumentoCartera);
            tvDiasCartera = itemView.findViewById(R.id.tvDiasCartera);
            tvVencCartera = itemView.findViewById(R.id.tvVencCartera);
            tvCondicionPagoCartera = itemView.findViewById(R.id.tvCondicionPagoCartera);
            tvDescripcionCartera = itemView.findViewById(R.id.tvDescripcionCartera);
            tvValorFactura = itemView.findViewById(R.id.tvValorFactura);
            llIconoGestion = itemView.findViewById(R.id.llIconoGestion);
            cbDocumentoCarteraSeleccionado = itemView.findViewById(R.id.cbDocumentoCarteraSeleccionado);

            cbDocumentoCarteraSeleccionado.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.cbDocumentoCarteraSeleccionado) {

                agregaConjuntoGestion(listaCarteraCliente.get(getAdapterPosition()));

            } else {
                if (mClickListener != null) {

                    mClickListener.onItemClickList(listaCarteraSeleccionadas);

                }
            }

        }

        /**
         * Metodo para agregar a la lista de cobros seleccionados o los que se chequean para ser cobrados
         *
         * @param item obejto cartera que se selecciona chequeando y agregando a la lista
         */
        private void agregaConjuntoGestion(Cartera item) {

            if (cbDocumentoCarteraSeleccionado.isChecked() && !listaCarteraSeleccionadas.contains(item)) {

                if(item.indicador==0 || item.registrado){//0-> ya tiene gestión
                    Toasty.info(contexto,"El documento ya tiene gestión.").show();
                    cbDocumentoCarteraSeleccionado.setChecked(false);
                }else{
                    listaCarteraCliente.get(getAdapterPosition()).facturaSeleccionadaGestion=true;
                    listaCarteraSeleccionadas.add(item);
                }

            } else {
                cbDocumentoCarteraSeleccionado.setChecked(false);
                listaCarteraCliente.get(getAdapterPosition()).facturaSeleccionadaGestion=false;
                listaCarteraSeleccionadas.remove(item);
            }

        }
    }


    // allows clicks events to be caught
    public void setClickListener(VistaGestionCarteraAdapter.ItemClickListener itemClickListener) {

        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {

        void onItemClick(View view, int position);

        void onItemClickList(List<Cartera> listaCarteraSeleccionadas);
    }
}
