package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.R;
import co.com.celuweb.carterabaldomero.formaPagoActivity;
import dataobject.Cartera;
import dataobject.Lenguaje;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Utilidades;

public class AdapterFormasPagoFacturasSelecc extends RecyclerView.Adapter<AdapterFormasPagoFacturasSelecc.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mInflater;
    public List<Cartera> cartera;
    public List<Cartera> carteraSelec;
    public Context context;
    private View.OnClickListener listener;
    private AdapterFormasPagoFacturasSelecc.facturaCartera facturaCartera;
    private Lenguaje lenguajeElegido;


    private int tipo;
    private ArrayList seleccionCartera;
    private ArrayList<Cartera> seleccionCarteraMultp;
    private Context contexto;

    public AdapterFormasPagoFacturasSelecc(List<Cartera> cartera, Context context) {

        this.context = context;
        this.cartera = cartera;

        try {
            this.facturaCartera = (AdapterFormasPagoFacturasSelecc.facturaCartera) context;

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_forma_pago_facturas, parent, false);
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

        private TextView documentoCartera, ConcecutivoCarteraFP, conceptoCartera, fechaVencimientoCartera, saldoCartera, diasCartera, simboloCheq;
        private TextView titulotvNumeroDocumentoCarteraFP, titulotvConceptCarteraFP, titulotvFechaVencCarteraFP,
                titulotvDiasCarteraFP, titulotvSaldoCarteraFP, titulotvConcecutivoCarteraFP;
        CheckBox cbDocumentoCarteraSeleccionado;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentoCartera = itemView.findViewById(R.id.tvNumeroDocumentoCarteraFP);
            conceptoCartera = itemView.findViewById(R.id.tvConceptCarteraFP);
            fechaVencimientoCartera = itemView.findViewById(R.id.tvFechaVencCarteraFP);
            saldoCartera = itemView.findViewById(R.id.tvSaldoCarteraFP);
            diasCartera = itemView.findViewById(R.id.tvDiasCarteraFP);
            ConcecutivoCarteraFP = itemView.findViewById(R.id.tvConcecutivoCarteraFP);

            titulotvNumeroDocumentoCarteraFP = itemView.findViewById(R.id.titulotvNumeroDocumentoCarteraFP);
            titulotvConceptCarteraFP = itemView.findViewById(R.id.titulotvConceptCarteraFP);
            titulotvFechaVencCarteraFP = itemView.findViewById(R.id.titulotvFechaVencCarteraFP);
            titulotvDiasCarteraFP = itemView.findViewById(R.id.titulotvDiasCarteraFP);
            titulotvSaldoCarteraFP = itemView.findViewById(R.id.titulotvSaldoCarteraFP);
            titulotvConcecutivoCarteraFP = itemView.findViewById(R.id.titulotvConcecutivoCarteraFP);

        }


        void binData(final Cartera item) {
            documentoCartera.setText(item.getDocumento());
            conceptoCartera.setText(item.getConcepto());
            fechaVencimientoCartera.setText(item.getFechaVencto());

            String input = "";
            input = String.valueOf(item.saldo);
            double valor = 0;
            String empresa = "";
            empresa = DataBaseBO.cargarEmpresa(context);
            final String finalEmpresa = empresa;


            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                saldoCartera.setText(formatoNumero.format(item.saldo));

            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                saldoCartera.setText(formatoNumero.format(item.saldo));

            }

            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    titulotvNumeroDocumentoCarteraFP.setText("Document");
                    titulotvConceptCarteraFP.setText("Type");
                    titulotvFechaVencCarteraFP.setText("Expiration date");
                    titulotvDiasCarteraFP.setText("Days late");
                    titulotvSaldoCarteraFP.setText("Amount");
                    titulotvConcecutivoCarteraFP.setText("Consecutive");

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                }
            }


            diasCartera.setText(String.valueOf(item.dias));
            //   ConcecutivoCarteraFP.setText(item.consecutivo);


            String consecutivo = "";
            String consecutivoNegocio = "";
            String consecutivoVendedor = "";

            consecutivo = DataBaseBO.cargarConsecutivo(context);
            consecutivoNegocio = DataBaseBO.cargarNegocioConsecutivo(context);
            consecutivoVendedor = DataBaseBO.cargarVendedorConsecutivo(context);


            int contador = 1;
            int consec1 = Integer.parseInt(consecutivo);
            int vendedorsum = Integer.parseInt(consecutivoVendedor);
            double valorfinal = 0;

            consec1 = consec1 + contador;

            ConcecutivoCarteraFP.setText(consecutivoNegocio + vendedorsum + consec1);

        }
    }

    public static interface facturaCartera {


        Serializable facturaCartera(List<Cartera> cartera);
    }


}
