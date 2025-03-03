package sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

import co.com.celuweb.carterabaldomero.LoginActivity;

/**
 * Created by Arfrak on 01/10/2019.
 */

public class PreferencesUsuario {

    public static final String USUARIO = "USUARIO";
    private static final String NOMBRE = "usuario";
    private static final String CONTRA = "contrasena";


    public static void guardarUsuario(Context context, String usuario) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USUARIO, usuario);

        editor.commit();
    }

    public static String obtenerUsuario(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return settings.getString(USUARIO, "");
    }

    /**
     * vaciar el preference. Remover todos los datos guardados.
     *
     * @param context
     */
    public static void vaciarPreferencesUsuario(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.commit();
    }

}

