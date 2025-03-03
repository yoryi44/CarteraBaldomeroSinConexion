package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.com.celuweb.carterabaldomero.R;
import dataobject.ClienteSincronizado;
import dataobject.Dias;

public class AdapterSpinnerDias extends RecyclerView.Adapter<AdapterSpinnerDias.ViewHolder> implements View.OnClickListener {

    public Spinner spinner;
    public List<Dias> dias;
    public Context context;
    private View.OnClickListener listener;


    public String color;

    public AdapterSpinnerDias(List<Dias> dias, Context context) {
        this.context = context;
        this.dias = dias;

    }

    @NonNull
    @Override
    public AdapterSpinnerDias.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_spinner_item_custom, parent, false);
        view.setOnClickListener(this);
        return new AdapterSpinnerDias.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterSpinnerDias.ViewHolder holder, int position) {


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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Spinner diasSe, martes, miercoles,jueves,viernes;


        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            diasSe = itemView.findViewById(R.id.spinnerDiasRuta);

        }



    }


}
