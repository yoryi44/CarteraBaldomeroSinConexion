package co.com.celuweb.carterabaldomero;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;

import com.pdfview.PDFView;

import java.io.File;
import java.util.Objects;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import utilidades.Constantes;
import utilidades.Utilidades;

public class PDFViewActivity extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar barVista = getSupportActionBar();
        Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
        barVista.setTitle(Utilidades.tituloFormato(this, "PDF"));

        setContentView(R.layout.ver_pdf);
        pdfView = findViewById(R.id.vistapdf);
        File file = new File(Utilidades.dirAppPDF().getPath(), Constantes.NOMBRE_DIRECTORIO_PDF + "/" + "Comprobante.pdf");

        //File file = new File(Environment.getExternalStorageDirectory(),"Comprobante.pdf");

        pdfView.fromFile(file);
        pdfView.isZoomEnabled();
        pdfView.show();

    }

    /**
     * METODO PARA REGRESAR
     *
     * @param item
     * @return
     */
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
}
