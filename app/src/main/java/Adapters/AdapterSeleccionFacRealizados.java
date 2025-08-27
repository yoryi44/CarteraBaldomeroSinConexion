package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Cartera;
import dataobject.FacturasRealizadas;
import utilidades.Utilidades;

public class AdapterSeleccionFacRealizados extends RecyclerView.Adapter<AdapterSeleccionFacRealizados.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mInflater;
    public List<FacturasRealizadas> cartera;
    public List<FacturasRealizadas> carteraSelec;
    public Context context;
    private View.OnClickListener listener;
    private AdapterSeleccionFacRealizados.facturaCarteraRealizadosSelecc facturaCarteraRealizadosSelecc;
    private int tipo;
    private ArrayList seleccionCartera;
    private Context contexto;

    public AdapterSeleccionFacRealizados(List<FacturasRealizadas> cartera, Context context) {

        this.context = context;
        this.cartera = cartera;

        try {
            this.facturaCarteraRealizadosSelecc = (AdapterSeleccionFacRealizados.facturaCarteraRealizadosSelecc) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }


    @NonNull
    @Override
    public AdapterSeleccionFacRealizados.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_facturas_selec_multp, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterSeleccionFacRealizados.ViewHolder holder, int position) {

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

        private TextView documentoCartera, conceptoCartera, fechaVencimientoCartera, saldoCartera, diasCartera;
        CheckBox cbDocumentoCarteraSeleccionado;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentoCartera = itemView.findViewById(R.id.tvNumeroDocumentoCartera);
            conceptoCartera = itemView.findViewById(R.id.tvConceptCartera);
            saldoCartera = itemView.findViewById(R.id.tvSaldoCartera);

        }


        void binData(final FacturasRealizadas item) {
            documentoCartera.setText(item.getDocumentoFactura());
            conceptoCartera.setText(item.getClaseDocumento());

            String empresa = "";
            empresa = DataBaseBO.cargarEmpresa(context);
            final String finalEmpresa = empresa;

            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                saldoCartera.setText(formatoNumero.format(item.getMontoPendientes()));

            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                saldoCartera.setText(formatoNumero.format(item.getMontoPendientes()));


            }

            seleccionCartera = new ArrayList<FacturasRealizadas>();


            facturaCarteraRealizadosSelecc.facturaRealizados (item.montoPendientes);



        }
    }

    public static interface facturaCarteraRealizadosSelecc {
        FacturasRealizadas facturaRealizados(double cartera);

    }



}
