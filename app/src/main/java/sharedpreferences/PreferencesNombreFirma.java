package sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Arfrak on 01/10/2019.
 */

public class PreferencesNombreFirma {

    public static final String NOMBRE = "nombre";
    private static final String NOMBRE_FIRMA = "nombre_firma";


    public static void guardarNombreFirma(Context context, String nombreFirma) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE_FIRMA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(NOMBRE, nombreFirma);

        editor.commit();
    }

    public static String obtenerNombreFirma(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE_FIRMA, Context.MODE_PRIVATE);
        return settings.getString(NOMBRE, "");
    }

    /**
     * vaciar el preference. Remover todos los datos guardados.
     *
     * @param context
     */
    public static void vaciarPreferencesNombreFirma(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE_FIRMA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.commit();
    }

}

