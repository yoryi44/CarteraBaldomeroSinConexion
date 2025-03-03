package co.com.celuweb.carterabaldomero;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

public class Camera extends Activity {
    /**
     * Constantes para identificar la acci—n realizada (tomar una fotograf’a
     * o bien seleccionarla de la galer’a)
     */
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;

    /**
     * Variable que define el nombre para el archivo donde escribiremos
     * la fotograf’a de tama–o completo al tomarla.
     */
    private String name = "";

    /** Este mŽtodo es llamado cuando la actividad es creada */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        name = Environment.getExternalStorageDirectory() + "/test.jpg";

        Button btnAction = (Button)findViewById(R.id.btnPic);
        btnAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Obtenemos los botones de imagen completa y de galer’a para revisar su estatus
                 * m‡s adelante
                 */
                RadioButton rbtnFull = (RadioButton)findViewById(R.id.radbtnFull);
                RadioButton rbtnGallery = (RadioButton)findViewById(R.id.radbtnGall);

                /**
                 * Para todos los casos es necesario un intent, si accesamos la c‡mara con la acci—n
                 * ACTION_IMAGE_CAPTURE, si accesamos la galer’a con la acci—n ACTION_PICK.
                 * En el caso de la vista previa (thumbnail) no se necesita m‡s que el intent,
                 * el c—digo e iniciar la actividad. Por eso inicializamos las variables intent y
                 * code con los valores necesarios para el caso del thumbnail, as’ si ese es el
                 * bot—n seleccionado no validamos nada en un if.
                 */
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                int code = TAKE_PICTURE;

                /**
                 * Si la opci—n seleccionada es fotograf’a completa, necesitamos un archivo donde
                 * guardarla
                 */
                if (rbtnFull.isChecked()) {
                    Uri output = Uri.fromFile(new File(name));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                    /**
                     * Si la opci—n seleccionada es ir a la galer’a, el intent es diferente y el c—digo
                     * tambiŽn, en la consecuencia de que estŽ chequeado el bot—n de la galer’a se hacen
                     * esas asignaciones
                     */
                } else if (rbtnGallery.isChecked()){
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    code = SELECT_PICTURE;
                }


                startActivityForResult(intent, code);
            }
        });
    }

    /**
     * Funci—n que se ejecuta cuando concluye el intent en el que se solicita una imagen
     * ya sea de la c‡mara o de la galer’a
     */
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * Se revisa si la imagen viene de la c‡mara (TAKE_PICTURE) o de la galer’a (SELECT_PICTURE)
         */
        if (requestCode == TAKE_PICTURE) {
            /**
             * Si se reciben datos en el intent tenemos una vista previa (thumbnail)
             */
            if (data != null) {
                /**
                 * En el caso de una vista previa, obtenemos el extra ÒdataÓ del intent y
                 * lo mostramos en el ImageView
                 */
                if (data.hasExtra("data")) {
                    ImageView iv = (ImageView)findViewById(R.id.imgView);
                    iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
                }
                /**
                 * De lo contrario es una imagen completa
                 */
            } else {
                /**
                 * A partir del nombre del archivo ya definido lo buscamos y creamos el bitmap
                 * para el ImageView
                 */
                ImageView iv = (ImageView)findViewById(R.id.imgView);
                iv.setImageBitmap(BitmapFactory.decodeFile(name));
                /**
                 * Para guardar la imagen en la galer’a, utilizamos una conexi—n a un MediaScanner
                 */
                new MediaScannerConnectionClient() {
                    private MediaScannerConnection msc = null; {
                        msc = new MediaScannerConnection(getApplicationContext(), this); msc.connect();
                    }
                    public void onMediaScannerConnected() {
                        msc.scanFile(name, null);
                    }
                    public void onScanCompleted(String path, Uri uri) {
                        msc.disconnect();
                    }
                };
            }
            /**
             * Recibimos el URI de la imagen y construimos un Bitmap a partir de un stream de Bytes
             */
        } else if (requestCode == SELECT_PICTURE){
            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                ImageView iv = (ImageView)findViewById(R.id.imgView);
                iv.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {}
        }
    }



}