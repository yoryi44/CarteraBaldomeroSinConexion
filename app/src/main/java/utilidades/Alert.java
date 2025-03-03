package utilidades;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.google.gson.Gson;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.PrincipalActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Lenguaje;
import dataobject.Usuario;
import sharedpreferences.PreferencesLenguaje;

public class Alert {

    public static Dialog dialogo;


    /**
     * @param activity           contexto de la aplicacion
     * @param title              titulo del dialogo
     * @param msg                mensaje que se muestra en el dialogo
     * @param positive           texto en el boton de aceptar
     * @param negative           texto en el boton de cancelar
     * @param onClickListener    evento del boton aceptar
     * @param onClickListenerNeg evento del boton cancelar
     */
    public static void nutresaShow(Context activity,
                                   String title,
                                   String msg,
                                   String positive,
                                   String negative,
                                   View.OnClickListener onClickListener,
                                   View.OnClickListener onClickListenerNeg) {

        dialogo = new Dialog(activity);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.mensajeapp);

        TextView tvTitle = dialogo.findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        TextView tvMsg = dialogo.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);

        Button btnSi = dialogo.findViewById(R.id.btnSi);
        btnSi.setText(positive);

        Button btnNo = dialogo.findViewById(R.id.btnNo);


        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alert.dialogo.cancel();
                Alert.dialogo.dismiss();
            }
        });

        if (negative == null) {

            btnNo.setVisibility(View.GONE);

        } else {

            btnNo.setText(negative);
        }

        (dialogo.findViewById(R.id.btnSi)).setOnClickListener(onClickListener);
        (dialogo.findViewById(R.id.btnNo)).setOnClickListener(onClickListenerNeg);

        dialogo.setCancelable(true);
        dialogo.show();
    }


    /**
     * Metodo para mostrar una alerta con titulo y mensaje predeterminado
     *
     * @param contexto                contexto de la aplicacion
     * @param titulo                  titulo del dialogo personalizado
     * @param texto                   texto del cuerpo del dialogo
     * @param onClickListenerAceptar  evento del boton aceptar
     * @param onClickListenerCancelar evento del boton cancelar
     */
    public static void alertGeneral(@NonNull final Context contexto,
                                    @NonNull String titulo,
                                    @NonNull String texto,
                                    View.OnClickListener onClickListenerAceptar,
                                    View.OnClickListener onClickListenerCancelar) {
        Activity activity = (Activity) contexto;
        dialogo = new Dialog(activity);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.vista_dialogo_alert);


        TextView dialogTitulo = dialogo.findViewById(R.id.tvTitleAlert);
        TextView dialogText = dialogo.findViewById(R.id.dialogText);

        dialogTitulo.setText(titulo);
        dialogText.setText(texto);

        Button btnAceptarDialogoCerrarSesion = dialogo.findViewById(R.id.btnAceptarDialogoCerrarSesion);
        Button btnCancelarDialogoCerrarSesion = dialogo.findViewById(R.id.btnCancelarDialogoCerrarSesion);

        btnAceptarDialogoCerrarSesion.setOnClickListener(onClickListenerAceptar);

        if (onClickListenerCancelar != null) {
            btnCancelarDialogoCerrarSesion.setOnClickListener(onClickListenerCancelar);
        } else {
            btnCancelarDialogoCerrarSesion.setVisibility(View.GONE);
        }

        dialogo.setCancelable(false);
        dialogo.show();
    }



    public static void vistaDialogoCerrarSesion(final Context contexto, String
            titulo, String
                                                        texto, View.OnClickListener onClickListenerAceptar, View.OnClickListener
                                                        onClickListenerCancelar) {

        Activity activity = (Activity) contexto;
        dialogo = new Dialog(activity);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.vista_dialogo_cerrar_sesion);
        Lenguaje lenguajeElegido;

        if (texto != null && !texto.isEmpty()) {

            TextView dialogText = dialogo.findViewById(R.id.idTitle2);
            TextView dialogTitulo = dialogo.findViewById(R.id.dialogText2);
            dialogTitulo.setText(titulo);
            dialogText.setText(texto);
        }

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Button btnAceptarDialogoCerrarSesion = dialogo.findViewById(R.id.btnAceptarDialogoCerrarSesion);
               btnAceptarDialogoCerrarSesion.setText("OK");
                btnAceptarDialogoCerrarSesion.setOnClickListener(onClickListenerAceptar);

                Button btnCancelarDialogoCerrarSesion = dialogo.findViewById(R.id.btnCancelarDialogoCerrarSesion);
                btnCancelarDialogoCerrarSesion.setText("Cancel");
                btnCancelarDialogoCerrarSesion.setOnClickListener(onClickListenerCancelar);

            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Button btnAceptarDialogoCerrarSesion = dialogo.findViewById(R.id.btnAceptarDialogoCerrarSesion);
                btnAceptarDialogoCerrarSesion.setOnClickListener(onClickListenerAceptar);

                Button btnCancelarDialogoCerrarSesion = dialogo.findViewById(R.id.btnCancelarDialogoCerrarSesion);
                btnCancelarDialogoCerrarSesion.setOnClickListener(onClickListenerCancelar);

            }
        }



        dialogo.setCancelable(false);
        dialogo.show();
    }

    /**
     * Metodo para mostrar un dialogo de advertencia indicando al usuario que esta a punto de
     * finalizar dia
     *
     * @param contexto                contexto de la aplicación
     * @param texto                   texto del cuerpo del pop up
     * @param onClickListenerAceptar  evento para el boton aceptar
     * @param onClickListenerCancelar evento para el boton cancelar
     */
    public static void vistaDialogoTerminarDia(final Context contexto, String
            title, String
                                                       texto,
                                               View.OnClickListener onClickListenerAceptar,
                                               View.OnClickListener onClickListenerCancelar) {
        final Usuario usuarioApp;
        dialogo = new Dialog(contexto);
        Lenguaje lenguajeElegido;


        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.vista_dialogo_cerrar_sesion);
        TextView dialogTitulo = dialogo.findViewById(R.id.idTitle2);
        TextView dialogText = dialogo.findViewById(R.id.dialogText2);

        dialogTitulo.setText(title);
        dialogText.setText(texto);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        }else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                Button btnAceptarDialogoCerrarSesion = dialogo.findViewById(R.id.btnAceptarDialogoCerrarSesion);
                btnAceptarDialogoCerrarSesion.setText("OK");
                btnAceptarDialogoCerrarSesion.setOnClickListener(onClickListenerAceptar);

                Button btnCancelarDialogoCerrarSesion = dialogo.findViewById(R.id.btnCancelarDialogoCerrarSesion);
                btnCancelarDialogoCerrarSesion.setText("Cancel");
                btnCancelarDialogoCerrarSesion.setOnClickListener(onClickListenerCancelar);

            }else if (lenguajeElegido.lenguaje.equals("ESP")) {

                Button btnAceptarDialogoCerrarSesion = dialogo.findViewById(R.id.btnAceptarDialogoCerrarSesion);
                btnAceptarDialogoCerrarSesion.setOnClickListener(onClickListenerAceptar);

                Button btnCancelarDialogoCerrarSesion = dialogo.findViewById(R.id.btnCancelarDialogoCerrarSesion);
                btnCancelarDialogoCerrarSesion.setOnClickListener(onClickListenerCancelar);

            }
        }

        dialogo.setCancelable(false);
        dialogo.show();
    }

    /**
     * Metodo para mostrar una alerta con titulo y mensaje predeterminado
     *
     * @param contexto                contexto de la aplicacion
     * @param titulo                  titulo del dialogo personalizado
     * @param texto                   texto del cuerpo del dialogo
     * @param onClickListenerAceptar  evento del boton aceptar
     * @param onClickListenerCancelar evento del boton cancelar
     */
    public static void alertAnular(@NonNull final Context contexto,
                                    @NonNull String titulo,
                                    @NonNull String texto,
                                    View.OnClickListener onClickListenerAceptar,
                                    View.OnClickListener onClickListenerCancelar,
                                   Consumer<EditText> obtenerValorEditText) {
        Activity activity = (Activity) contexto;
        dialogo = new Dialog(activity);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.vista_dialogo_anular_alert);


        TextView dialogTitulo = dialogo.findViewById(R.id.tvTitleAlert);
        TextView dialogText = dialogo.findViewById(R.id.dialogText);
        EditText dialogObservacion = dialogo.findViewById(R.id.etObservacionAnulacion);

        dialogTitulo.setText(titulo);
        dialogText.setText(texto);

        Button btnAceptarDialogoCerrarSesion = dialogo.findViewById(R.id.btnAceptarDialogoCerrarSesion);
        Button btnCancelarDialogoCerrarSesion = dialogo.findViewById(R.id.btnCancelarDialogoCerrarSesion);

        btnAceptarDialogoCerrarSesion.setOnClickListener(view -> {
            if (obtenerValorEditText != null) {
                obtenerValorEditText.accept(dialogObservacion);
            }
            if (onClickListenerAceptar != null) {
                onClickListenerAceptar.onClick(view);
            }
        });

        if (onClickListenerCancelar != null) {
            btnCancelarDialogoCerrarSesion.setOnClickListener(onClickListenerCancelar);
        } else {
            btnCancelarDialogoCerrarSesion.setVisibility(View.GONE);
        }

        dialogo.setCancelable(false);
        dialogo.show();
    }

}
