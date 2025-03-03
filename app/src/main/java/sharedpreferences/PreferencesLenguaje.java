package sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PreferencesLenguaje {
    public static final String LENGUAJE = "LENGUAJE";
    private static final String NOMBRE = "lenguaje";
    private static final String ORDENLENGUAJE = "ORDEN";
    private static final String ORDEN = "orden";
    private static final String LISTAORDENADALENGUAJE = "listaordenadalenguaje";


    public static void guardarLenguajeSeleccionada(Context context, String usuario) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LENGUAJE, usuario);
        editor.commit();
    }

    public static String obtenerLenguajeSeleccionada(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return settings.getString(LENGUAJE, "");
    }

    /**
     * vaciar el preference. Remover todos los datos guardados.
     *
     * @param context
     */
    public static void vaciarPreferencesLenguajeSeleccionada(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Metodo para guardar el metodo de ordenamiento que se utilice en los clientes sincronizados
     *
     * @param context contexto de la aplicacion
     * @param orden   orden en el cual se organizaron los cliente
     */
    public static void guardarOrdenLenguajeSeleccionada(Context context, int orden) {

        SharedPreferences settings = context.getSharedPreferences(ORDEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ORDENLENGUAJE, orden);
        editor.apply();
    }

    /**
     * Metodo para obtener el codigo del orden en que fueron organizados los clientes
     *
     * @param context contexto de la aplicacion
     * @return retorno del codigo de orden
     */
    public static int obtenerOrdenLenguajeSeleccionada(Context context) {
        SharedPreferences settings = context.getSharedPreferences(ORDEN, Context.MODE_PRIVATE);
        return settings.getInt(ORDENLENGUAJE, 0);
    }

    public static void guardarListaOrdenLenguajeSeleccionada(Context context, ArrayList<String> listaOrden) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String j = new Gson().toJson(listaOrden);

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

        prefsEditor.putString(LISTAORDENADALENGUAJE, j);
        prefsEditor.apply();
    }

    public static ArrayList<String> loadListaLenguajeSeleccionadaOrdenados(Context context) throws
            JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson(); //Instancia Gson.
        //Obtiene datos (json)
        String objetos = prefs.getString(LISTAORDENADALENGUAJE, "");
        //Convierte json  a JsonArray.
        JSONArray jsonArray = new JSONArray(objetos);

        //Convierte JSONArray a Lista de Objetos!
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(String.valueOf(jsonArray), listType);
    }



}
