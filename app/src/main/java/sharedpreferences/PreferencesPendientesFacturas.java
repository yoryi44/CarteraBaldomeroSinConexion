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

public class PreferencesPendientesFacturas {

    public static final String PENDIENTESFACTURASELECCIONADO = "PENDIENTESFACTURASELECCIONADO";
    private static final String NOMBRE = "pendientesfacturaseleccionado";
    private static final String ORDENFACTURASPENDIENTES = "ORDEN";
    private static final String ORDEN = "orden";
    private static final String LISTAORDENADAFACTURASPENDIENTES = "listaordenadaPendientesfacturas";


    public static void guardarPendientesFacturaSeleccionada(Context context, String usuario) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PENDIENTESFACTURASELECCIONADO, usuario);
        editor.commit();
    }

    public static String obtenerPendientesFacturaSeleccionada(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return settings.getString(PENDIENTESFACTURASELECCIONADO, "");
    }

    /**
     * vaciar el preference. Remover todos los datos guardados.
     *
     * @param context
     */
    public static void vaciarPreferencesPendientesFacturaSeleccionada(Context context) {

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
    public static void guardarOrdenPendientesFacturaSeleccionada(Context context, int orden) {

        SharedPreferences settings = context.getSharedPreferences(ORDEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ORDENFACTURASPENDIENTES, orden);
        editor.apply();
    }

    /**
     * Metodo para obtener el codigo del orden en que fueron organizados los clientes
     *
     * @param context contexto de la aplicacion
     * @return retorno del codigo de orden
     */
    public static int obtenerOrdenPendientesFacturaSeleccionada(Context context) {
        SharedPreferences settings = context.getSharedPreferences(ORDEN, Context.MODE_PRIVATE);
        return settings.getInt(ORDENFACTURASPENDIENTES, 0);
    }

    public static void guardarListaOrdenPendientesFacturaSeleccionada(Context context, ArrayList<String> listaOrden) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String j = new Gson().toJson(listaOrden);

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

        prefsEditor.putString(LISTAORDENADAFACTURASPENDIENTES, j);
        prefsEditor.apply();
    }

    public static ArrayList<String> loadListaFacturaPendientesSeleccionadaOrdenados(Context context) throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson(); //Instancia Gson.
        //Obtiene datos (json)
        String objetos = prefs.getString(LISTAORDENADAFACTURASPENDIENTES, "");
        //Convierte json  a JsonArray.
        JSONArray jsonArray = new JSONArray(objetos);

        //Convierte JSONArray a Lista de Objetos!
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(String.valueOf(jsonArray), listType);
    }
}
