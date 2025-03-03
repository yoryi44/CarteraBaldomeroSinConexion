package co.com.celuweb.carterabaldomero;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Lenguaje;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Utilidades;

public class InformesActivity extends AppCompatActivity {
    private Lenguaje lenguajeElegido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(this);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);

    }

    @Override
    protected void onResume() {

        super.onResume();
        configurarVista();
    }


    private void configurarVista() {


        TextView tituloRecibosRealizadosInformes = findViewById(R.id.tituloRecibosRealizadosInformes);
        TextView tituloInformes = findViewById(R.id.tituloInformes);

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Reports"));

                tituloInformes.setText("Reports");
                tituloRecibosRealizadosInformes.setText("Payments Collected");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Informes"));

            }
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            item.setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    item.setEnabled(true);
                }
            }, 600);


            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_Informes", true);
        editor1.remove("estado_Informes");
        editor1.commit();
        finish();
        guardarVista();
        Intent vistaClienteActivity = new Intent(this, PrincipalActivity.class);

        startActivity(vistaClienteActivity);

    }


    public void onClickRealizados(View v) {


        v.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 600);


        Intent vistaClienteActivity = new Intent(this, RealizadosActivity.class);
        startActivity(vistaClienteActivity);

    }


    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_Principal", estado);
        editor1.commit();
    }


}
