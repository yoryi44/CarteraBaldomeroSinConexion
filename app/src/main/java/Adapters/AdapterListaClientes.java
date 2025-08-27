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

import businessObject.DataBaseBO;
import businessObject.PrinterBO;
import co.com.celuweb.carterabaldomero.R;
import co.com.celuweb.carterabaldomero.RutaActivity;
import dataobject.ClienteSincronizado;
import dataobject.Lenguaje;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesLenguaje;

public class AdapterListaClientes extends RecyclerView.Adapter<AdapterListaClientes.ViewHolder> implements View.OnClickListener {


    public List<ClienteSincronizado> clientes;
    public Context context;
    private View.OnClickListener listener;
    public String color;
    private Lenguaje lenguajeElegido;

    public AdapterListaClientes(List<ClienteSincronizado> clientes, Context context) {
        this.context = context;
        this.clientes = clientes;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_cliente, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binData(clientes.get(position));

    }


    @Override
    public int getItemCount() {
        return clientes.size();

    }


    @Override
    public long getItemId(int position) {
        return clientes.size();
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

        private TextView codigoCLientes, nombreClientes, razonsocialClientes, nitClientes, emailcliente, telefonoClientes;
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

        void binData(final ClienteSincronizado item) {
            codigoCLientes.setText(item.getCodigo());
            nombreClientes.setText(item.getNombre());
            emailcliente.setText(item.getEmail());
            telefonoClientes.setText(item.getTelefono());
            razonsocialClientes.setText(item.getRazonSocial());
            nitClientes.setText(item.getNit());

            Gson gson2 = new Gson();
            String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(context);
            lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

            String empresas = "";
            empresas = DataBaseBO.cargarEmpresa(context);

            if (empresas.equals("AGUC")) {
                textrazonsocialClientes.setVisibility(View.GONE);
                razonsocialClientes.setVisibility(View.GONE);
            } else {
                textrazonsocialClientes.setVisibility(View.VISIBLE);
                razonsocialClientes.setVisibility(View.VISIBLE);
            }


            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    textcodigoCLientes.setText("Customer Code");
                    textnombreClientes.setText("Customer Name");
                    textemailcliente.setText("Email");
                    textrazonsocialClientes.setText("Social Reason");
                    textnitClientes.setText("Nit");
                    texttelefonoClientes.setText("Phone");

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

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
