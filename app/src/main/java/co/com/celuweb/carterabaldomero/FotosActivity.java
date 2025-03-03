package co.com.celuweb.carterabaldomero;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import businessObject.DataBaseBO;
import dataobject.Anticipo;
import dataobject.ClienteSincronizado;
import dataobject.Facturas;
import dataobject.FormaPago;
import dataobject.Fotos;

public class FotosActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static final int PHOTO_PICKER_REQUEST_CODE = 3;

    private static final int PHOTO_PICKER_MULTI_SELECT_REQUEST_CODE = 2;
    private static final int REQUEST_PHOTO_PICKER_SINGLE_SELECT =1 ;
    private static final int REQUEST_PHOTO_PICKER_MULTI_SELECT = 2;
    ListView lstFotos;
    List<String> listaFotoString;
    List<Fotos> listaFotos;
    Fotos fotos;
    String nroRecibo;
    ClienteSincronizado clienteSel;
    FormaPago formaPago;
    Anticipo anticipo;
    List<Facturas> listaFacturas2;
    final List<Facturas> listaFacturas3 = new ArrayList<>();
    String acert = "";
    private static int PHOTO_SELECTED = 1;
    ImageButton fotoH;
    ImageView foto_gallery;
    private static final int PICK_IMAGE = 400;
    Uri imageUri;
    Uri imageUri2;
    public static final String EXTRA_PICK_IMAGES_MAX = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_fotos);
        nroRecibo ="";
        fotoH = (ImageButton) findViewById(R.id.imageButton);
        foto_gallery = (ImageView)findViewById(R.id.foto_gallery);
        lstFotos = (ListView) findViewById(R.id.lstFotos);
        listar();




        foto_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });



        //Obtiene el indice de la foto selecionada a travez de la etiqueta definida (tag).
        PHOTO_SELECTED = Integer.parseInt(String.valueOf(fotoH.getTag()));

        fotoH.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openGallery();
            }
        });


    }


    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);



    /***   Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona Imágen."), SELECT_PICTURE);**/

     /**   Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);**/


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



            if (resultCode != Activity.RESULT_OK) {
                // Handle error
                return;
            }

            switch(requestCode) {
                case REQUEST_PHOTO_PICKER_SINGLE_SELECT:
                    // Get photo picker response for single select.
                    Uri currentUri = data.getData();

                    // Do stuff with the photo/video URI.
                    return;
                case REQUEST_PHOTO_PICKER_MULTI_SELECT:
                    // Get photo picker response for multi select
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri currentUri2 = data.getClipData().getItemAt(i).getUri();

                        // Do stuff with each photo/video URI.
                    }
                    return;
            }


    }

  /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageUri2 = data.getData();
            foto_gallery.setImageURI(imageUri);
            fotoH.setImageURI(imageUri2);
        }
    }

**/

///10782400

    private void listar() {
        List<Integer> myList = new ArrayList<Integer>();
        listaFotoString = DataBaseBO.listarString();

        for (String s:listaFotoString) {
            acert = s;
        }


   /**     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listaFotoString);
        lstFotos.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        lstFotos.setOnItemClickListener
                (new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {

                        DataBaseBO.buscar(acert);
                        Intent intent = new Intent(v.getContext(), MostrarFotoActivity.class);
                        startActivity(intent);

                        finish();
                    }
                }); **/
    }



}
