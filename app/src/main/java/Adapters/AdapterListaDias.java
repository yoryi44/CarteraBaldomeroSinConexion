package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import co.com.celuweb.carterabaldomero.R;
import dataobject.ClienteSincronizado;
import dataobject.Dias;
import dataobject.Lenguaje;
import sharedpreferences.PreferencesLenguaje;

public class AdapterListaDias extends RecyclerView.Adapter<AdapterListaDias.ViewHolder> implements View.OnClickListener {


    public List<Dias> dias;
    public Context context;
    private View.OnClickListener listener;
    private Lenguaje lenguajeElegido;


    public String color;

    public AdapterListaDias(List<Dias> dias, Context context) {
        this.context = context;
        this.dias = dias;

    }

    @NonNull
    @Override
    public AdapterListaDias.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_cliente, parent, false);
        view.setOnClickListener(this);
        return new AdapterListaDias.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterListaDias.ViewHolder holder, int position) {
        holder.binData(dias.get(position));

    }


    @Override
    public int getItemCount() {
        return dias.size();

    }


    @Override
    public long getItemId(int position) {
        return dias.size();
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


        @SuppressLint("ResourceType")
        private TextView codigoCLientes,nombreClientes,razonsocialClientes,nitClientes,emailcliente,telefonoClientes;
        private TextView textcodigoCLientes, textnombreClientes, textrazonsocialClientes, textnitClientes, textemailcliente, texttelefonoClientes;


        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            codigoCLientes = itemView.findViewById(R.id.tvCodigoSapClienteSinc);
            nombreClientes = itemView.findViewById(R.id.tvNombreCliente);
            emailcliente = itemView.findViewById(R.id.tvEmailCliente);
            razonsocialClientes = itemView.findViewById(R.id.tvRazonSocialeCliente);
            nitClientes = itemView.findViewById(R.id.tvNitCliente);
            telefonoClientes = itemView.findViewById(R.id.tvDireccionCliente);

            textcodigoCLientes = itemView.findViewById(R.id.tvCodigoSapTitulo);
            textnombreClientes = itemView.findViewById(R.id.tvNombreTitulo);
            textemailcliente = itemView.findViewById(R.id.tvEmailClienteText);
            textrazonsocialClientes = itemView.findViewById(R.id.tvRazonSocialTitulo);
            textnitClientes = itemView.findViewById(R.id.tvNitTitulo);
            texttelefonoClientes = itemView.findViewById(R.id.tvDireccionTitulo);


        }

        void binData(final Dias item) {
            codigoCLientes.setText(item.getCodigo());
            nombreClientes.setText(item.getNombre());
            emailcliente.setText(item.getEmail());
            telefonoClientes.setText(item.getTelefono());
            razonsocialClientes.setText(item.getRazonSocial());
            nitClientes.setText(item.getNit());

            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


            if (lenguajeElegido == null) {

            }else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    textcodigoCLientes.setText("Customer Code");
                    textnombreClientes.setText("Customer Name");
                    textemailcliente.setText("Email");
                    textrazonsocialClientes.setText("Social Reason");
                    textnitClientes.setText("Nit");
                    texttelefonoClientes.setText("Phone");

                }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    textcodigoCLientes.setText("Codigo SAP");
                    textnombreClientes.setText("Nombre Comercial");
                    textemailcliente.setText("Correo Electronico");
                    textrazonsocialClientes.setText("Razon Social");
                    textnitClientes.setText("Nit");
                    texttelefonoClientes.setText("Contacto");

                }
            }

        }

    }


}
