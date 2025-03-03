package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.CarteraActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Cartera;
import dataobject.Lenguaje;
import dataobject.Pendientes;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Utilidades;

public class AdapterCartera extends RecyclerView.Adapter<AdapterCartera.ViewHolder> implements View.OnClickListener {

    private LayoutInflater mInflater;
    public List<Cartera> cartera;
    public List<Cartera> carteraSelec;
    public Context context;
    private View.OnClickListener listener;
    private AdapterCartera.facturaCartera facturaCartera;
    private Lenguaje lenguajeElegido;

    private int tipo;
    private ArrayList seleccionCartera;
    private ArrayList<Cartera> seleccionCarteraMultp;
    private ArrayList<Cartera> multiplesMarcarChek;

    private ArrayList<Cartera> seleccionFacturasGestionadas;
    private Context contexto;

    public AdapterCartera(List<Cartera> cartera, Context context) {

        this.context = context;
        this.cartera = cartera;

        try {
            this.facturaCartera = (AdapterCartera.facturaCartera) context;

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_cartera_cliente, parent, false);
        view.setOnClickListener(this);
        return new AdapterCartera.ViewHolder(view);
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

        private TextView documentoCartera, conceptoCartera, fechaVencimientoCartera, saldoCartera, diasCartera, simboloCheq;
        private TextView titulotvNumeroDocumentoCartera, titulotvConceptCartera, titulotvFechaVencCartera, titulotvDiasCartera, titulotvSaldoCartera;
        private int idUnicoFactura;
        CheckBox cbDocumentoCarteraSeleccionado;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentoCartera = itemView.findViewById(R.id.tvNumeroDocumentoCartera);
            conceptoCartera = itemView.findViewById(R.id.tvConceptCartera);
            fechaVencimientoCartera = itemView.findViewById(R.id.tvFechaVencCartera);
            saldoCartera = itemView.findViewById(R.id.tvSaldoCartera);
            diasCartera = itemView.findViewById(R.id.tvDiasCartera);
            cbDocumentoCarteraSeleccionado = itemView.findViewById(R.id.cbCarteraFactura);
            simboloCheq = itemView.findViewById(R.id.simboloformas1);

            titulotvNumeroDocumentoCartera = itemView.findViewById(R.id.titulotvNumeroDocumentoCartera);
            titulotvConceptCartera = itemView.findViewById(R.id.titulotvConceptCartera);
            titulotvFechaVencCartera = itemView.findViewById(R.id.titulotvFechaVencCartera);
            titulotvDiasCartera = itemView.findViewById(R.id.titulotvDiasCartera);
            titulotvSaldoCartera = itemView.findViewById(R.id.titulotvSaldoCartera);


        }


        void binData(final Cartera item) {
            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    titulotvNumeroDocumentoCartera.setText("Document");
                    titulotvConceptCartera.setText("Type");
                    titulotvFechaVencCartera.setText("Expiration");
                    titulotvDiasCartera.setText("Days Late");
                    titulotvSaldoCartera.setText("Balance");

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                }
            }


            documentoCartera.setText(item.getDocumento());
            conceptoCartera.setText(item.getConcepto());
            fechaVencimientoCartera.setText(item.getFechaVencto());

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


            diasCartera.setText(String.valueOf(item.dias));
            cbDocumentoCarteraSeleccionado.setChecked(item.facturaSeleccionadaGestion);
            seleccionCartera = new ArrayList<CheckBox>();
            seleccionCarteraMultp = new ArrayList<Cartera>();
            seleccionFacturasGestionadas = new ArrayList<Cartera>();

            Gson gson = new Gson();
            List<Cartera> carteraS = new ArrayList<>();
            Type collectionType = new TypeToken<Collection<Cartera>>() {
            }.getType();
            String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(context);
            final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

            final List<String> claseDocumento = new ArrayList<>();
            final List<Integer> idsUnicos = new ArrayList<>();

            final List<Double> saldos = new ArrayList<>();
            multiplesMarcarChek = new ArrayList<Cartera>();

            String claseDocument = "";

            int IDSunicos = 0;
            double saldo = 0;
            CheckBox checkBox = new CheckBox(context);


            if (facCollection != null) {


                for (Cartera cartera1 : facCollection) {

                    seleccionCarteraMultp.add(cartera1);
                    multiplesMarcarChek.add(cartera1);


                }


                for (int i = 0; i < multiplesMarcarChek.size(); i++) {
                    String id = multiplesMarcarChek.get(i).getDocumentoFinanciero();

                    if (item.getDocumentoFinanciero().equals(id)) {

                        cbDocumentoCarteraSeleccionado.setChecked(true);


                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {


                                Toasty.warning(context, "Already managed invoice cannot be saved..", Toasty.LENGTH_SHORT).show();


                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                                Toasty.warning(context, "Factura ya gestionada no se puede guardar..", Toasty.LENGTH_SHORT).show();


                            }
                        }


                    }


                }
            }


            cbDocumentoCarteraSeleccionado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox checkBox = (CheckBox) buttonView;

                    if (seleccionCartera.size() < 18) {


                        if (checkBox.isChecked()) {

                            seleccionCartera.add(checkBox);
                            ArrayList<Cartera> cartera = new ArrayList<Cartera>();

                            seleccionCarteraMultp.add(item);
                            for (int i = 0; i < seleccionCarteraMultp.size(); i++) {
                                item.idUnicoCartera++;
                                item.facturaSeleccionadaGestion = true;

                                cartera.add(item);
                                Collections.sort(seleccionCarteraMultp, new Comparator<Cartera>() {
                                    public int compare(Cartera obj1, Cartera obj2) {
                                        if (obj1.getDocumentoFinanciero().equals(obj2.getDocumentoFinanciero())){


                                        }

                                        return obj1.getDocumentoFinanciero().compareTo(obj2.getDocumentoFinanciero());
                                    }
                                });


                                facturaCartera.facturaCartera(cartera);
                                Gson gson = new Gson();
                                String jsonStringObject = gson.toJson(seleccionCarteraMultp);
                                PreferencesCartera.guardarCarteraSeleccionada(context, jsonStringObject);
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
                            for (int i = 0; i < seleccionCarteraMultp.size(); i++) {

                                String documento = String.valueOf(seleccionCarteraMultp.get(i));

                                if (documento.equals(item.getDocumento())) {

                                    seleccionCarteraMultp.remove(i);
                                    cartera.remove(item.getDocumento());


                                    seleccionCartera.remove(checkBox);
                                    facturaCartera.facturaCartera(seleccionCarteraMultp);

                                    facturaCartera.facturaCartera(cartera);
                                    Gson gson = new Gson();
                                    String jsonStringObject = gson.toJson(seleccionCarteraMultp);
                                    PreferencesCartera.guardarCarteraSeleccionada(context, jsonStringObject);


                                }
                            }

                            for (int i = 0; i < multiplesMarcarChek.size(); i++) {

                                String numeroRecibo = "";
                                List<String> pendientesList = new ArrayList<>();


                                for (Cartera pen : multiplesMarcarChek) {

                                    pendientesList.add(pen.getDocumento());
                                }





                                if (pendientesList.get(i).equals(item.getDocumento())) {


                                    multiplesMarcarChek.remove(i);


                                    facturaCartera.facturaCartera(multiplesMarcarChek);
                                    Gson gson = new Gson();
                                    String jsonStringObject = gson.toJson(multiplesMarcarChek);
                                    PreferencesCartera.guardarCarteraSeleccionada(context, jsonStringObject);


                                }

                            }
                        }
                    }

                }
            });


        }
    }

    public static interface facturaCartera {


        Serializable facturaCartera(List<Cartera> cartera);


    }


}
