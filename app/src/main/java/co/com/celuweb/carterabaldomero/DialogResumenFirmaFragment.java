package co.com.celuweb.carterabaldomero;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.ByteArrayOutputStream;

import businessObject.DataBaseBO;
import sharedpreferences.PreferencesNombreFirma;

public class DialogResumenFirmaFragment extends DialogFragment {

    String idPago;
    Bitmap bitmap;
    String empresa;
    String vendedor;
    String lenguajeElegido;
    ImageView ivPreviewFirma;
    private DialogResumenFirmaFragment.OnSuccesSignatureDialogListener listener;

    //ELEMENTOS VISTA
    EditText etNombreFirma;
    Button saveButton;
    TextView tvTitleFirmaDialogResumenFirma;
    TextView tvLabelPrintName;

    public interface OnSuccesSignatureDialogListener {
        void onSuccesSignatureDialogListener();
    }

    public DialogResumenFirmaFragment(String idPago, Bitmap signatureBitmap, String empresa, String vendedor, String lenguajeElegido) {
        this.idPago = idPago;
        this.empresa = empresa;
        this.bitmap = signatureBitmap;
        this.vendedor = vendedor;
        this.lenguajeElegido = lenguajeElegido;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogResumenFirmaFragment.OnSuccesSignatureDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MyDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_resumen_firma_fragment, container, false);
        ivPreviewFirma = view.findViewById(R.id.ivPreviewFirma);
        ivPreviewFirma.setImageBitmap(bitmap);

        init(view);

        return  view;
    }

    private void init(View view){
        initViewElements(view);
        language();
    }

    private void initViewElements(View view) {

        tvTitleFirmaDialogResumenFirma = view.findViewById(R.id.tvTitleFirmaDialogResumenFirma);
        tvLabelPrintName = view.findViewById(R.id.tvLabelPrintName);

        etNombreFirma = view.findViewById(R.id.etNombreFirma);
        etNombreFirma.setText(!PreferencesNombreFirma.obtenerNombreFirma(view.getContext()).isEmpty() ? PreferencesNombreFirma.obtenerNombreFirma(view.getContext()) : DataBaseBO.cargarUsuarioApp(getContext()));

        saveButton = view.findViewById(R.id.btnAceptarFirma);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EVITAR DOBLE CLICK
                saveButton.setEnabled(false);

                String nombreFirma = etNombreFirma.getText().toString();

                //CONVERTIR FIRMA A ARRAY DE BITS
                byte[] signatureBytes = convertBitmapToByteArray(bitmap);

                //GUARDAR NOMBRE FIRMA EN SHARED PREFERENCES
                PreferencesNombreFirma.guardarNombreFirma(view.getContext(),nombreFirma);

                //GUARDAR FIRMA EN BASE DE DATOS
                DataBaseBO.guardarFirma(idPago, signatureBytes, empresa, vendedor, nombreFirma, getContext());

                listener.onSuccesSignatureDialogListener();
            }
        });
    }

    private void language() {
        if(lenguajeElegido.equals("USA"))
        {
            tvTitleFirmaDialogResumenFirma.setText("Signature");
            tvLabelPrintName.setText("Print Name");
            etNombreFirma.setHint("Name");
            saveButton.setText("Accept");
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}