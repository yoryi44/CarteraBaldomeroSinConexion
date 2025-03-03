package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.FacturasSeleccionadasActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Cartera;
import utilidades.Utilidades;

public class AdapterFacturasSeleccionMultiple extends RecyclerView.Adapter<AdapterFacturasSeleccionMultiple.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mInflater;
    public List<Cartera> cartera;
    public List<Cartera> carteraSelec;
    public Context context;
    private View.OnClickListener listener;
    private AdapterFacturasSeleccionMultiple.facturaCartera facturaCartera;
    private int tipo;
    private ArrayList seleccionCartera;
    private Context contexto;

    public AdapterFacturasSeleccionMultiple(List<Cartera> cartera, Context context) {

        this.context = context;
        this.cartera = cartera;

        try {
            this.facturaCartera = (AdapterFacturasSeleccionMultiple.facturaCartera) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_facturas_selec_multp, parent, false);
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

        private TextView documentoCartera, conceptoCartera, fechaVencimientoCartera, saldoCartera, diasCartera,simboloCheq;
        CheckBox cbDocumentoCarteraSeleccionado;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentoCartera = itemView.findViewById(R.id.tvNumeroDocumentoCartera);
            conceptoCartera = itemView.findViewById(R.id.tvConceptCartera);
            saldoCartera = itemView.findViewById(R.id.tvSaldoCartera);
            simboloCheq = itemView.findViewById(R.id.simboloformas1);

        }


        void binData(final Cartera item) {
            documentoCartera.setText(item.getDocumento());
            conceptoCartera.setText(item.getConcepto());

            String empresa = "";
            empresa = DataBaseBO.cargarEmpresa();
            final String finalEmpresa = empresa;

            if (finalEmpresa.equals("AABR")) {
                simboloCheq.setText("$");

            }
            if (finalEmpresa.equals("ADHB")) {
                simboloCheq.setText("$");

            }
            if (finalEmpresa.equals("AGSC")) {
                simboloCheq.setText("$");

            }
            if (finalEmpresa.equals("AGGC")) {
                simboloCheq.setText("Q");
            }
            if (finalEmpresa.equals("AFPN")) {
                simboloCheq.setText("C$");

            }
            if (finalEmpresa.equals("AFPZ")) {
                simboloCheq.setText("₡");

            }
            if (finalEmpresa.equals("AGCO")) {
                simboloCheq.setText("$");

            }
            if (finalEmpresa.equals("AGAH")) {
                simboloCheq.setText("₡");

            }
            if (finalEmpresa.equals("AGDP")) {
                simboloCheq.setText("Q");


            }
            if (finalEmpresa.equals("AGUC")) {
                simboloCheq.setText("$");

            }

            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                saldoCartera.setText(formatoNumero.format(item.saldo));

            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                saldoCartera.setText(formatoNumero.format(item.saldo));

            }

            List<Cartera> carteraS = new ArrayList<>();
            seleccionCartera = new ArrayList<Cartera>();
            facturaCartera.facturaCartera(item.saldo);

        }
    }

    public static interface facturaCartera {
        Cartera facturaCartera(double cartera);

    }


}
