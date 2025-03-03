package co.com.celuweb.carterabaldomero;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import businessObject.DataBaseBO;
import dataobject.Anticipo;
import dataobject.Cartera;
import dataobject.ClienteSincronizado;
import dataobject.Facturas;
import dataobject.FormaPago;
import dataobject.Fotos;
import dataobject.Lenguaje;
import dataobject.Pendientes;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesFotos;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import utilidades.Alert;
import utilidades.Utilidades;

public class MultiplesFotosActivity extends AppCompatActivity {

    Button select, previous, next, previous1, button,button2,btnEliminar,btnEliminarFotos;
    ImageSwitcher imageView;
    ImageView imageView2;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    TextView total;
    ArrayList<Uri> mArrayUri;
    int position = 0;
    List<String> imagesEncodedList;
    List<Fotos> listaFotos;
    /*Imagen*/
    private ImageView imagen;
    FormaPago formaPago;
    ClienteSincronizado clienteSel;
    Anticipo anticipo;
    String nroRecibo = "";
    List<Facturas> listaFacturas2;
    String str = "";
    int numero = (int) (Math.random() * 1000) + 1;
    String codigoVendedor = "";
    boolean estadoFto = false;
    String idPago = "";
    String idPagoVarias= "";
    String idUnicaElmininar = "";
    String idUnicaElmininarVarias = "";
    String idUnicaElmininarVariasUnica = "";
    final List<String> idEliminar = new ArrayList<>();
    final List<String> idEliminarVarias = new ArrayList<>();
    final List<String> idEliminarVariasUnica = new ArrayList<>();
    private Lenguaje lenguajeElegido;

    String metodo_pago = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multip);

        Intent intent = getIntent();
        if (intent != null) {
            metodo_pago = intent.getStringExtra("metodo_pago");
        }



        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(this);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);

        button= findViewById(R.id.button);
        button2= findViewById(R.id.button2);
        btnEliminar= findViewById(R.id.btnEliminar);
        btnEliminarFotos= findViewById(R.id.btnEliminarFotos);
        next = findViewById(R.id.next);
        codigoVendedor = DataBaseBO.cargarCodigo();
        select = findViewById(R.id.select);
        total = findViewById(R.id.text);
        imageView = findViewById(R.id.image);
        // imageView2 = findViewById(R.id.image2);
        previous = findViewById(R.id.previous);
        mArrayUri = new ArrayList<Uri>();
        imagen = (ImageView) findViewById(R.id.imageView);

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Photos and Gallery"));

                button.setText("TAKE PHOTO");
                button2.setText("UPLOAD PHOTO");
                btnEliminar.setText("DELETE");
                select.setText("SELECT PHOTOS");
                next.setText("NEXT");
                previous.setText("PREVIOUS");
                btnEliminarFotos.setText("DELETE PHOTOS");



            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Fotos y Galeria"));

            }
        }




        /**    for (Cartera cartera1 : facCollection) {
         documentoFacturas.add(cartera1.getDocumentoFinanciero());
         }**/
        idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + numero;

        // showing all images in imageswitcher
        imageView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView1 = new ImageView(getApplicationContext());
                return imageView1;
            }
        });
        next = findViewById(R.id.next);

        // click here to select next image
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < mArrayUri.size() - 1) {
                    // increase the position by 1
                    position++;
                    imageView.setImageURI(mArrayUri.get(position));
                } else {
                    Toast.makeText(MultiplesFotosActivity.this, "Last Image Already Shown", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // click here to view previous image
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    // decrease the position by 1
                    position--;
                    imageView.setImageURI(mArrayUri.get(position));
                }
            }
        });

        imageView = findViewById(R.id.image);

        // click here to select image
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // initialising intent
                Intent intent = new Intent();

                // setting type to select to be image
                intent.setType("image/*");

                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });


    }


    public void tomarFotos(View v) {


        v.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 600);



        final String nombreFoto = ".jpg";
        estadoFto = true;
        /*Definimos un intent para abrir la activity de fotografias*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*Definimos un file donde se creara el archivo (El parametro
        recibe el directorio donde se
        va a crear (Musica, Ringtones, etc.), si se manda null se deja
        en la raiz) ----
        Nombre del archivo a crear*/
        /*OJOOOO, esto no es la micro sd, es el archivo que se crean
        en la carpeta Android con el
        * nombre del paquete del proyecto, ahi quedan todas las fotos*/
        File foto = new File(getExternalFilesDir(null),
                nombreFoto);
        /*Se añade (Permiso para almacenar  --- el archivo que contendra
        la foto)*/
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(MultiplesFotosActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", foto));
        /*Se inicia la actitity*/
        startActivity(intent);
    }


//10051380

    public void recuperarFoto(View v) {

        v.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 600);

        final List<String> documentoFacturas = new ArrayList<>();
        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(this);
        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(this);
        formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(this);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        Gson gsonFotos = new Gson();
        String stringJsonObjectFotos = PreferencesFotos.obteneFotoSeleccionada(this);
        Fotos fotos = gsonFotos.fromJson(stringJsonObjectFotos, Fotos.class);

        Gson gsonPen = new Gson();
        Type collectionTypePend = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPend = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MultiplesFotosActivity.this);
        final Collection<Pendientes> facCollectionPend = gsonPen.fromJson(stringJsonObjectPend, collectionTypePend);

        Gson gsonMultiplePendientes = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(MultiplesFotosActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonMultiplePendientes.fromJson(stringJsonObjectPendientes, collectionTypePendientes);

        int numero = (int) (Math.random() * 1000) + 1;
        String codigoVendedor = "";
        codigoVendedor = DataBaseBO.cargarCodigo();
        final String idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + numero;
        idUnicaElmininar = idPago;

        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();


        if (facCollectionPend != null) {
            for (Pendientes pendientes : facCollectionPend) {
                nroRecibo = pendientes.numeroRecibo;

            }
            documentoFacturas.add(nroRecibo);
        }

        if (facCollectionPendientes != null) {
            for (Pendientes pendientes : facCollectionPendientes) {
                nroRecibo = pendientes.numeroRecibo;
                documentoFacturas.add(nroRecibo);
            }
        }


        if (facCollection != null) {

            nroRecibo = clienteSel.consecutivo;
            documentoFacturas.add(nroRecibo);
        }

        if (anticipo != null) {

            nroRecibo = clienteSel.consecutivo;
            documentoFacturas.add(nroRecibo);
        }


        if (imagen.getDrawable() != null ) {

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Toasty.warning(MultiplesFotosActivity.this, "He already took a picture", Toasty.LENGTH_LONG).show();
                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Toasty.warning(MultiplesFotosActivity.this, "Ya se cargo una imagen", Toasty.LENGTH_LONG).show();

                }
            }





        }else if (imagen.getDrawable() == null ) {

            if (estadoFto==true) {
                final String nombreFoto = ".jpg";

        /*Recuperamos la imagen, mandano la ruta (recordar el
        NULL es porque no es un directorio
        en especifico, solo la raiz del proyecto
        en la carpeta android ---- y el nombre del archivo)*/
                Bitmap bitmap1 =
                        BitmapFactory.
                                decodeFile(getExternalFilesDir(null) +
                                        "/" + nombreFoto);
                /*Se añade la foto al imageView*/
                String posicion = String.valueOf(position);
                posicion = String.valueOf(0);
                imagen.setImageBitmap(bitmap1);
                imagen.setDrawingCacheEnabled(true);
                imagen.buildDrawingCache();
//                Bitmap bitmap = Bitmap.createBitmap(imagen.getDrawingCache());
                idEliminar.add(idUnicaElmininar);
                /***
                 * VALIDAR SI EL RECIBO TIENE EL METODO DE PAGO ENVIADO
                 */
                if(metodo_pago != null)
                {
                    if(!metodo_pago.isEmpty())
                    {
                        DataBaseBO.guardarImagenMutilples(idPago, bitmap1, documentoFacturas, empresa, fotos.idenFoto,metodo_pago);
                    }
                    else
                    {
                        DataBaseBO.guardarImagen(idPago, bitmap1, documentoFacturas, empresa, fotos.idenFoto);
                    }
                }
                else
                {
                    DataBaseBO.guardarImagen(idPago, bitmap1, documentoFacturas, empresa, fotos.idenFoto);
                }
            }else if (estadoFto == false) {
                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        Toasty.warning(MultiplesFotosActivity.this, "Before uploading a photo you should take the photo first", Toasty.LENGTH_LONG).show();
                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        Toasty.warning(MultiplesFotosActivity.this, "Antes de cargar una foto se debe tomar la foto primero", Toasty.LENGTH_LONG).show();

                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final List<String> documentoFacturas = new ArrayList<>();
        final List<String> idsFotos = new ArrayList<>();

        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(this);
        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(this);
        formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(this);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        Gson gsonFotos = new Gson();
        String stringJsonObjectFotos = PreferencesFotos.obteneFotoSeleccionada(this);
        Fotos fotos = gsonFotos.fromJson(stringJsonObjectFotos, Fotos.class);

        Gson gsonPen = new Gson();
        Type collectionTypePend = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPend = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MultiplesFotosActivity.this);
        final Collection<Pendientes> facCollectionPend = gsonPen.fromJson(stringJsonObjectPend, collectionTypePend);

        Gson gsonMultiplePendientes = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(MultiplesFotosActivity.this);
        final Collection<Pendientes> facCollectionPendientes = gsonMultiplePendientes.fromJson(stringJsonObjectPendientes, collectionTypePendientes);


        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();

        if (facCollection != null) {
            nroRecibo = clienteSel.consecutivo;
            documentoFacturas.add(nroRecibo);
        }

        if (anticipo != null) {

            nroRecibo = clienteSel.consecutivo;
            documentoFacturas.add(nroRecibo);
        }

        if (facCollectionPend != null) {
            for (Pendientes pendientes : facCollectionPend) {
                nroRecibo = pendientes.numeroRecibo;
            }
            documentoFacturas.add(nroRecibo);
        }

        if (facCollectionPendientes != null) {
            for (Pendientes pendientes : facCollectionPendientes) {
                nroRecibo = pendientes.numeroRecibo;
                documentoFacturas.add(nroRecibo);
            }

        }


        mArrayUri.clear();
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                int contador = 0;
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    mArrayUri.add(imageurl);
                    idPagoVarias = idPago + contador++;
                    idsFotos.add(idPagoVarias);
                    idUnicaElmininarVarias = idPagoVarias;
                    idEliminarVarias.add(idUnicaElmininarVarias);



                }
                // setting 1st selected image into image switcher
                imageView.setImageURI(mArrayUri.get(0));
                position = 0;
                String posicion = String.valueOf(position);
                //pasar a mapa de bits
                for (int i = 0; i < mArrayUri.size(); i++) {
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();

                    try {
                        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                        Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageurl);
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap1, 1024 /*Ancho*/, 1050 /*Alto*/, false /* filter*/);
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*Calidad*/, bytearrayoutputstream);
                        imageView.setImageURI(mArrayUri.get(i));
                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();

                        Bitmap bitmap = Bitmap.createBitmap(resizedBitmap);
                        DataBaseBO.guardarImagenes(idsFotos.get(i), bitmap, documentoFacturas, empresa, fotos.idenFoto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {

                Uri imageurl = data.getData();
                mArrayUri.add(imageurl);
                position = 0;
                for (int i = 0; i < mArrayUri.size(); i++) {
                    Uri imageUri = data.getData();

                    try {
                        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                        Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

//                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap1, 1024 /*Ancho*/, 1050 /*Alto*/, false /* filter*/);
//                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*Calidad*/, bytearrayoutputstream);
                        imageView.setImageURI(mArrayUri.get(i));
                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();

                        Bitmap bitmap = Bitmap.createBitmap(bitmap1);
                        idUnicaElmininarVariasUnica = idPago;
                        idEliminarVariasUnica.add(idUnicaElmininarVariasUnica);


                        DataBaseBO.guardarImagen(idPago, bitmap, documentoFacturas, empresa, fotos.idenFoto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
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


            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {
                    Alert.alertGeneral(this, null, "You want to go?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (imagen.getDrawable() != null || imageView.getDrawingCache() != null) {
                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = settings.edit();
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");

                                editor1.commit();

                                finish();
                            } else if (imagen.getDrawable() == null || imageView.getDrawingCache() == null) {

                                Toasty.warning(MultiplesFotosActivity.this, "No photos taken or images selected in order to continue you have to take a photo or select an image", Toasty.LENGTH_LONG).show();

                            }



                            Alert.dialogo.cancel();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Alert.dialogo.cancel();
                        }
                    });

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                    Alert.alertGeneral(this, null, "Desea salir", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (imagen.getDrawable() != null || imageView.getDrawingCache() != null) {
                                SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = settings.edit();
                                editor1.putBoolean("estado_MultiplesFotos", true);
                                editor1.remove("estado_MultiplesFotos");

                                editor1.commit();

                                finish();
                            } else if (imagen.getDrawable() == null || imageView.getDrawingCache() == null) {
                                Toasty.warning(MultiplesFotosActivity.this, "No se ha tomado fotos ni seleccionado imagenes para poder continuar se tiene que tomar una foto o seleccionar una imagen", Toasty.LENGTH_LONG).show();
                            }

                            Alert.dialogo.cancel();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Alert.dialogo.cancel();
                        }
                    });

                }
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void Eliminar(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);



        final List<String> documentoFacturas = new ArrayList<>();

        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(this);
        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(this);
        formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(this);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        Gson gsonPen = new Gson();
        Type collectionTypePend = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPend = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MultiplesFotosActivity.this);
        final Collection<Pendientes> facCollectionPend = gsonPen.fromJson(stringJsonObjectPend, collectionTypePend);

        int numero = (int) (Math.random() * 1000) + 1;
        String codigoVendedor = "";
        codigoVendedor = DataBaseBO.cargarCodigo();
        final String idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + numero;
        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();

        if (facCollection != null) {

            nroRecibo = clienteSel.consecutivo;
            documentoFacturas.add(nroRecibo);
        }

        if (facCollectionPend != null) {
            for (Pendientes pendientes : facCollectionPend) {
                nroRecibo = pendientes.numeroRecibo;
                documentoFacturas.add(nroRecibo);
            }
        }

        //   listaFotos = DataBaseBO.cargarIdPagoFoto();

        // for (Fotos fot:listaFotos) {
        // fot.idFoto}


        DataBaseBO.eliminarFoto(idEliminar);
        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Toasty.warning(getApplicationContext(), "Photo deleted with success", Toast.LENGTH_SHORT).show();

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Toasty.warning(getApplicationContext(), "Foto eliminada con exito", Toast.LENGTH_SHORT).show();

            }
        }

        imagen.destroyDrawingCache();
        imagen.setImageDrawable(null);
        imagen.refreshDrawableState();
        imagen.setImageBitmap(null);

        finish();
        startActivity(getIntent());

    }

    public void EliminarTodos(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);



        final List<String> documentoFacturas = new ArrayList<>();
        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(this);
        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(this);
        formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(this);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gsonPen = new Gson();
        Type collectionTypePend = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPend = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(MultiplesFotosActivity.this);
        final Collection<Pendientes> facCollectionPend = gsonPen.fromJson(stringJsonObjectPend, collectionTypePend);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(this);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);
        int numero = (int) (Math.random() * 1000) + 1;
        String codigoVendedor = "";
        codigoVendedor = DataBaseBO.cargarCodigo();
        final String idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + numero;
        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();

        if (facCollection != null) {

            nroRecibo = clienteSel.consecutivo;
            documentoFacturas.add(nroRecibo);
        }

        if (facCollectionPend != null) {
            for (Pendientes pendientes : facCollectionPend) {
                nroRecibo = pendientes.numeroRecibo;
                documentoFacturas.add(nroRecibo);

            }

        }

        if (facCollection == null) {
            if(clienteSel != null)
            {
                nroRecibo = clienteSel.consecutivo;
                documentoFacturas.add(nroRecibo);
            }
        }

        if (idEliminarVarias.size()>0) {

            DataBaseBO.eliminarFoto(idEliminarVarias);
            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Toasty.warning(getApplicationContext(), "Photo deleted with success", Toast.LENGTH_SHORT).show();

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Toasty.warning(getApplicationContext(), "Foto eliminada con exito", Toast.LENGTH_SHORT).show();

                }
            }

            imageView.destroyDrawingCache();
            imageView.setImageURI(null);
            finish();
            startActivity(getIntent());
        }

        if (idEliminarVariasUnica.size()>0) {
            DataBaseBO.eliminarFoto(idEliminarVariasUnica);
            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Toasty.warning(getApplicationContext(), "Photo deleted with success", Toast.LENGTH_SHORT).show();

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Toasty.warning(getApplicationContext(), "Foto eliminada con exito", Toast.LENGTH_SHORT).show();

                }
            }

            imageView.destroyDrawingCache();
            imageView.setImageURI(null);
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onBackPressed() {

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                Alert.alertGeneral(this, null, "You want to go?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (imagen.getDrawable() != null || imageView.getDrawingCache() != null) {
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_MultiplesFotos", true);
                            editor1.remove("estado_MultiplesFotos");

                            editor1.commit();

                            finish();

                        } else if (imagen.getDrawable() == null || imageView.getDrawingCache() == null) {

                            Toasty.warning(MultiplesFotosActivity.this, "No photos taken or images selected in order to continue you have to take a photo or select an image", Toasty.LENGTH_LONG).show();

                        }



                        Alert.dialogo.cancel();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alert.dialogo.cancel();
                    }
                });

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                Alert.alertGeneral(this, null, "Desea salir", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (imagen.getDrawable() != null || imageView.getDrawingCache() != null) {
                            SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = settings.edit();
                            editor1.putBoolean("estado_MultiplesFotos", true);
                            editor1.remove("estado_MultiplesFotos");

                            editor1.commit();

                            finish();

                        } else if (imagen.getDrawable() == null || imageView.getDrawingCache() == null) {
                            Toasty.warning(MultiplesFotosActivity.this, "No se ha tomado fotos ni seleccionado imagenes para poder continuar se tiene que tomar una foto o seleccionar una imagen", Toasty.LENGTH_LONG).show();
                        }

                        Alert.dialogo.cancel();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alert.dialogo.cancel();
                    }
                });

            }
        }

    }

}