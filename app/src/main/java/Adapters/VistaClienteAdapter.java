package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import co.com.celuweb.carterabaldomero.R;
import dataobject.Cliente;
import dataobject.Main;
import utilidades.Utilidades;

public class VistaClienteAdapter extends RecyclerView.Adapter<VistaClienteAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Cliente> listaClientes;
    private HashMap<String, Cliente> listaClientesSeleccionados;
    private TextView lblContadorClientes;


    // data is passed into the constructor
    public VistaClienteAdapter(Context contexto, List<Cliente> listaClientes, HashMap<String,
            Cliente> listaClientesSeleccionados,TextView lblContadorClientes) {

        this.lblContadorClientes = lblContadorClientes;
        this.mInflater = LayoutInflater.from(contexto);
        this.listaClientes = listaClientes;
        this.listaClientesSeleccionados = listaClientesSeleccionados;
    }

    public void borrarLista() {
        listaClientes.clear();
        listaClientesSeleccionados.clear();
        notifyDataSetChanged();
    }

    public void actualizarLista(List<Cliente> listaClientes, HashMap<String, Cliente> listaClientesSeleccionados) {
        this.listaClientesSeleccionados = listaClientesSeleccionados;
        this.listaClientes = listaClientes;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.vista_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Cliente clientePosLista = listaClientes.get(position);

        /*En el caso de que sea por la opcion de agregar cliente
         * se debe buscar si algun cliente del resultado de la busqueda
         * pertenece al conjunto de clientes que ya estaban cargados en el rutero anteriormente*/
        if (listaClientesSeleccionados != null && listaClientesSeleccionados.size() > 0) {
            if (listaClientesSeleccionados.containsKey(clientePosLista.codigo.trim())) {
                clientePosLista.seleccionado = true;
            }
        }

        holder.tvCodigoSapCliente.setText(clientePosLista.codigo);
        holder.tvNitCliente.setText(clientePosLista.nit);
        holder.tvNombreCliente.setText(clientePosLista.razonSocial);
        holder.tvRazonSocialeCliente.setText(clientePosLista.nombre);
        holder.tvDireccionCliente.setText(clientePosLista.direccion);
        holder.tvTelefonoCliente.setText(clientePosLista.telefono);


    }

    @Override
    public int getItemCount() {

        return listaClientes.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCodigoSapCliente;
        TextView tvNitCliente;
        TextView tvNombreCliente;
        TextView tvRazonSocialeCliente;
        TextView tvDireccionCliente;
        TextView tvTelefonoCliente;


        ViewHolder(View itemView) {

            super(itemView);

            tvCodigoSapCliente = itemView.findViewById(R.id.tvCodigoSapClienteSinc);
            tvNitCliente = itemView.findViewById(R.id.tvNitCliente);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvRazonSocialeCliente = itemView.findViewById(R.id.tvRazonSocialeCliente);
            tvDireccionCliente = itemView.findViewById(R.id.tvDireccionCliente);

        }
    }

}
