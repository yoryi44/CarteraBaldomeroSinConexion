package utilidades;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.core.content.res.ResourcesCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Anticipo;
import dataobject.Cartera;
import dataobject.Cliente;
import dataobject.ClienteSincronizado;
import dataobject.Facturas;
import dataobject.ParseBitmap;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesClienteSeleccionado;

public class Utilidades {

    public static String getVersion(Context context) {

        String version = "";
        try {
            version = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static boolean tieneConexionInternet(Context contexto) {

        ConnectivityManager cm =
                (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    /**
     * Metodo para verificar que el dispositivo este conectado a internet
     *
     * @param applicationContext contexto de la aplicación
     */
    public static boolean verificarNetwork(Context applicationContext) {
        ConnectivityManager cm =
                (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    /**
     * Permite convertir un String en fecha (Date).
     *
     * @param fecha Cadena de fecha dd/MM/yyyy
     * @return Objeto Date
     */
    public static Date ParseFecha(String fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
        } catch (ParseException ex) {
            Log.e("UTILIDADES", ex.getMessage());
        }
        return fechaDate;
    }

    public static String formatFecha(String fecha, String formato) {
        // el que parsea
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        // el que formatea
        SimpleDateFormat formateador = new SimpleDateFormat(formato);


        Date date = null;
        try {
            date = parseador.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String s = formateador.format(date);

        return "" + formateador.format(date);
    }

    /**
     * Permite convertir un String en fecha (Date).
     *
     * @param fecha Cadena de fecha dd/MM/yyyy
     * @return Objeto Date
     */
    public static Date ParseFechaDate(String fecha, String formatoFecha) {
        Locale locale = Locale.getDefault();
        SimpleDateFormat formato = new SimpleDateFormat(formatoFecha, locale);
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
        } catch (ParseException ex) {
            Log.e("ERRORFECHA", ex.getMessage());
        }
        return fechaDate;
    }

    public static SpannableStringBuilder tituloFormato(Context contexto, String titulo) {

        Typeface font = ResourcesCompat.getFont(contexto, R.font.foco_std);
        SpannableStringBuilder s = new SpannableStringBuilder(titulo);
        s.setSpan(new CustomTypefaceSpan("", font), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        return s;
    }

    public static String obtenerListaClientesSeleccionadosFormato(HashMap<String, Cliente> listaClientesSeleccionados) {

        String lista = "";

        for (Map.Entry entry : listaClientesSeleccionados.entrySet()) {

            if (lista.isEmpty()) {

                lista = entry.getKey().toString().trim();

            } else {

                lista += "_" + entry.getKey().toString().trim();

            }
        }

        return lista;
    }

    public static List<Facturas> listaFacturasParcialTotal(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotal(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasParcialTotalHechas(Context contexto, List<String> idesPago) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalHechas(nroReciboFacTotalPar, idesPago, contexto);


        return listafacturasParcialTotal;
    }

    //Efectivo

    public static List<Facturas> listaFacturasParcialTotalEfectivo(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalEfec(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasParcialTotalEfectivoCantidadFac(Context contexto, List<String> idesPago) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalEfecCantidadFacturas(nroReciboFacTotalPar, documentosFinanciero, idesPago, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasParcialTotalEfectivoTarjeta(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalEfecTarjeta(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasParcialTotalEfectivoTransferencia(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalEfecTransferencia(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasIDEfectivo(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarIDEfectivo(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasMetodofectivo(Context contexto, List<String> idPago) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarListaEfectivo(nroReciboFacTotalPar, documentosFinanciero, idPago, contexto);


        return listafacturasParcialTotal;
    }

    //Cheque

    public static List<Facturas> listaFacturasParcialTotalCheq(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalCheq(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasIDCheq(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarIDCheq(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasMetodoCheque(Context contexto, List<String> idPago) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarListaCheque(nroReciboFacTotalPar, documentosFinanciero, idPago, contexto);


        return listafacturasParcialTotal;
    }


    //Transferencia

    public static List<Facturas> listaFacturasParcialTotalTransfe(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalTransf(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasParcialTotalTransfeCheque(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalTransfCheque(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasIDTranfe(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarIDTransfe(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasMetodoTransferencia(Context contexto, List<String> idPago) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarListaTransferencia(nroReciboFacTotalPar, documentosFinanciero, idPago, contexto);


        return listafacturasParcialTotal;
    }


    //Tarjeta

    public static List<Facturas> listaFacturasParcialTotalTarjeta(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalTarjeta(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasIDTTarjeta(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarIDTarjeta(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasMetodoTarjeta(Context contexto, List<String> idPago) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarListaTarjeta(nroReciboFacTotalPar, documentosFinanciero, idPago, contexto);


        return listafacturasParcialTotal;
    }


    //Bitcoin

    public static List<Facturas> listaFacturasParcialTotalBitcoin(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarFacParTotalBitcoin(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }

    public static List<Facturas> listaFacturasIDBitcoin(Context contexto) {

        final List<String> documentosFinanciero = new ArrayList<>();
        final List<Facturas> listafacturasParcialTotal;

        final ClienteSincronizado clienteSel;
        String documentoFinanciero = "";

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);
        String nroReciboFacTotalPar = clienteSel.consecutivo;

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject, collectionType);


        if (facCollection != null) {
            for (Cartera cartera1 : facCollection) {
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                documentosFinanciero.add(documentoFinanciero);
            }

        }

        listafacturasParcialTotal = DataBaseBO.cargarIDBitcoin(nroReciboFacTotalPar, documentosFinanciero, contexto);


        return listafacturasParcialTotal;
    }


    public static double totalFormasPago(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROG(str, clienteSel.consecutivo, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double totalFormasPagoFacRealizadas(Context contexto, String nroRecibo) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;


        listaFacturas2 = DataBaseBO.cargarIdPagoOGRealizados(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGRealizados(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }


    public static double SUMAVALORDOC(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TDiferenciaSumadelTotalDelDOcumento(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double sumaValorConsig(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";


        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalValorConsignado(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double sumaValorConsigRealizados(Context contexto,String consecutivo) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";



        listaFacturas2 = DataBaseBO.cargarIdPagoOGRealizados(consecutivo, contexto);
        String str = "";


//        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalValorConsignadoRealizados(str, contexto);

            }
//        }
        return DiferenciaFormasPagoE;
    }

    public static double totalDifereFAv(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotaDifeAfav(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double totalFormasPagoEfec(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGEFec(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double totalFormasPagoEfecCantidadFacturas(Context contexto, List<String> idPago) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGEFecCantidadFac(str, idPago, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }


    public static double totalFormasPagoEfecCantidadFacturasHechas(Context contexto, String idPago, List<String> idPagos) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGEFecCantidadFac(idPago, idPagos, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    ///COUNTS

    public static double CountMetodoPagoEfec(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.countMetodEfec(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double CountMetodoPagoCheq(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.countMetodCheq(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double CountMetodoPagoTarjeta(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.countMetodTarjeta(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double CountMetodoPagoTransF(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.countMetodTransferencia(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    ////

    public static double totalFormasPagoCheq(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGCheq(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double totalFormasPagoTarje(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGTarje(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double totalFormasPagoTranF(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGTrasnf(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static double totalFormasPagoBit(Context contexto) {
        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        List<Facturas> listaFacturas2;
        final ClienteSincronizado clienteSel;
        final Anticipo anticipo;
        double DiferenciaFormasPagoE = 0;
        String nroRecibo = "";

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);


        if (anticipo != null) {
            claseDocumento.add("DZ");
            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);
        }

        if (anticipo == null) {

            nroRecibo = clienteSel.consecutivo;
        }

        listaFacturas2 = DataBaseBO.cargarIdPagoOG(nroRecibo, contexto);
        String str = "";

        for (int i = 0; i < listaFacturas2.size(); i++) {
            for (Facturas fruit : listaFacturas2) {
                str += "\'" + fruit.idPago + "\',";

                DiferenciaFormasPagoE = DataBaseBO.TotalFormasPagoAnticipoRROGBit(str, contexto);

            }
        }
        return DiferenciaFormasPagoE;
    }

    public static File dirApp(Context context) {

        File SDCardRoot;

        SDCardRoot = Environment.getExternalStorageDirectory();

        assert SDCardRoot != null;
        File dirApp = new File(SDCardRoot.getPath() + "/" + Constantes.nameDirApp);

        if (!dirApp.isDirectory())
            dirApp.mkdirs();

        return dirApp;
    }

    public static File dirAppPDF() {

        File SDCardRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dirApp = new File(SDCardRoot.getPath() + "/" + Constantes.nameDirApp);

        if (!dirApp.isDirectory())
            dirApp.mkdirs();

        return dirApp;
    }

    public static File dirAppTemp() {

        File SDCardRoot = Environment.getExternalStorageDirectory();
        File dirApp = new File(SDCardRoot.getPath() + "/" + Constantes.nameDirAppTemp);

        if (!dirApp.isDirectory())
            dirApp.mkdirs();

        return dirApp;
    }

    /**
     * @param value valor a convertir
     * @return el nuevo dato en tipo long
     */
    public static long ToLong(String value) {

        try {
            return Long.parseLong(value);

        } catch (NumberFormatException e) {

            return 0L;
        }
    }

    public static boolean existeArchivoDataBase() {

        File SDCardRoot = Environment.getExternalStorageDirectory();
        File database = new File(SDCardRoot.getPath() + "/" + Constantes.nameDirApp, "DataBase.db");
        return database.exists();
    }

    public static boolean existeArchivoDataBaseTemp(Context context) {

        File SDCardRoot;

        SDCardRoot = Environment.getExternalStorageDirectory();

        assert SDCardRoot != null;

        File database = new File(SDCardRoot.getPath() + "/" + Constantes.nameDirApp, "Temp.db");
        return database.exists();
    }

    public static String separarMiles(String numero) {

        String cantidad;
        String cantidadAux1;
        String cantidadAux2;
        boolean tieneMenos;

        int posPunto;
        int i;

        cantidadAux1 = "";
        cantidadAux2 = "";

        numero = QuitarE(numero);

        tieneMenos = false;
        if (numero.indexOf("-") != -1) {

            String aux;
            tieneMenos = true;
            aux = numero.substring(0, numero.indexOf("-"));
            aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
            numero = aux;
        }

        if (numero.indexOf(".") == -1) {

            if (numero.length() > 3) {

                cantidad = ColocarComas(numero, numero.length());

            } else {

                if (tieneMenos)
                    numero = "-" + numero;
                else
                    numero = numero;

                return numero;
            }

        } else {

            posPunto = numero.indexOf(".");

            for (i = 0; i < posPunto; i++) {

                cantidadAux1 = cantidadAux1 + numero.charAt(i);
            }

            for (i = posPunto; i < numero.length(); i++) {

                cantidadAux2 = cantidadAux2 + numero.charAt(i);
            }

            if (cantidadAux1.length() > 3) {

                cantidad = ColocarComas(cantidadAux1, posPunto);
                cantidad = cantidad + cantidadAux2;

            } else {

                if (tieneMenos)
                    numero = "-" + numero;
                else
                    numero = numero;

                return numero;
            }
        }

        if (tieneMenos)
            cantidad = "-" + cantidad;
        else
            cantidad = cantidad;

        return cantidad;
    }

    public static String ColocarComas(String numero, int pos) {

        String cantidad;
        Vector<String> cantidadAux;
        String cantidadAux1;
        int i;
        int cont;

        cantidadAux = new Vector<>();
        cantidadAux1 = "";
        cont = 0;

        for (i = (pos - 1); i >= 0; i--) {

            if (cont == 3) {

                cantidadAux1 = "," + cantidadAux1;
                cantidadAux.addElement(cantidadAux1);
                cantidadAux1 = "";
                cont = 0;
            }

            cantidadAux1 = numero.charAt(i) + cantidadAux1;
            cont++;
        }

        cantidad = cantidadAux1;

        for (i = cantidadAux.size() - 1; i >= 0; i--) {

            cantidad = cantidad + cantidadAux.elementAt(i);
        }

        return cantidad;
    }

    public synchronized static String QuitarE(String numero) {

        int posE;
        int cantMover;
        int posAux;
        int cantCeros;
        int posPunto;
        String cantMoverString;
        String cantidad;
        String cantidadAux1, cantidadAux2;


        cantMoverString = "";
        cantidad = "";

        if (!(numero.indexOf("E") != -1)) {

            return numero;
        } else {

            posE = numero.indexOf("E");
            posE++;

            while (posE < numero.length()) {

                cantMoverString = cantMoverString + numero.charAt(posE);
                posE++;
            }

            cantMover = Integer.parseInt(cantMoverString);

            posE = numero.indexOf("E");
            posAux = 0;
            posPunto = 0;

            while (posAux < posE) {

                if (numero.charAt(posAux) != '.') {

                    cantidad = cantidad + numero.charAt(posAux);
                } else {

                    posPunto = posAux;
                }
                posAux++;
            }

            if (cantidad.length() < (cantMover + posPunto)) {

                cantCeros = cantMover - cantidad.length() + posPunto;

                for (int i = 0; i < cantCeros; i++) {

                    cantidad = cantidad + "0";
                }
            } else {

                cantidadAux1 = cantidad.substring(0, (cantMover + posPunto));
                cantidadAux2 = cantidad.substring((cantMover + posPunto));

                if (!cantidadAux2.equals("")) {

                    cantidad = cantidadAux1 + "." + cantidadAux2;
                } else {

                    cantidad = cantidadAux1;
                }
            }
        }

        return cantidad;
    }

    public static String separarMilesSinDecimal(String numero, Context context) {
        String empresa = "", simbolo = "";
        String cantidad;
        String cantidadAux1;
        String cantidadAux2;
        boolean tieneMenos;
        empresa = DataBaseBO.cargarEmpresa(context);
        final String finalEmpresa = empresa;
        String simboloefec = simbolo;
        int posPunto;
        int i;


        if (finalEmpresa.equals("AABR")) {
            simbolo = "$";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("ADHB")) {
            simbolo = "$";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AGSC")) {
            simbolo = "$";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AGGC")) {
            simbolo = "Q";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AFPN")) {
            simbolo = "C$";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AFPZ")) {
            simbolo = "₡";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AGCO")) {
            simbolo = "$";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AGAH")) {
            simbolo = "₡";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AGDP")) {
            simbolo = "Q";
            simboloefec = simbolo;
        }
        if (finalEmpresa.equals("AGUC")) {
            simbolo = "$";
            simboloefec = simbolo;
        }

        cantidadAux1 = "";
        cantidadAux2 = "";

        numero = QuitarE(numero);

        tieneMenos = false;
        if (numero.indexOf("-") != -1) {

            String aux;
            tieneMenos = true;
            aux = numero.substring(0, numero.indexOf("-"));
            aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
            numero = aux;
        }

        if (numero.indexOf(".") == -1) {

            if (numero.length() > 3) {

                cantidad = ColocarComas(numero, numero.length());

            } else {

                if (tieneMenos)
                    numero = simboloefec + "-" + numero;
                else
                    numero = simboloefec + numero;

                return numero;
            }

        } else {

            posPunto = numero.indexOf(".");

            for (i = 0; i < posPunto; i++) {

                cantidadAux1 = cantidadAux1 + numero.charAt(i);
            }

            for (i = posPunto; i < numero.length(); i++) {

                cantidadAux2 = cantidadAux2 + numero.charAt(i);
            }

            if (cantidadAux1.length() > 3) {

                cantidad = ColocarComas(cantidadAux1, posPunto);
                //cantidad = cantidad + cantidadAux2;

            } else {

                if (tieneMenos)
                    numero = simboloefec + "-" + numero;
                else
                    numero = simboloefec + numero;

                return numero;
            }
        }

        if (tieneMenos)
            cantidad = simboloefec + "-" + cantidad;
        else
            cantidad = simboloefec + cantidad;

        return cantidad;
    }

    public static String separarMilesSinDecimalPendientes(String numero) {
        String empresa = "", simbolo = "";
        String cantidad;
        String cantidadAux1;
        String cantidadAux2;
        boolean tieneMenos;
        int posPunto;
        int i;


        cantidadAux1 = "";
        cantidadAux2 = "";

        numero = QuitarE(numero);

        tieneMenos = false;
        if (numero.indexOf("-") != -1) {

            String aux;
            tieneMenos = true;
            aux = numero.substring(0, numero.indexOf("-"));
            aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
            numero = aux;
        }

        if (numero.indexOf(".") == -1) {

            if (numero.length() > 3) {

                cantidad = ColocarComas(numero, numero.length());

            } else {

                if (tieneMenos)
                    numero = "-" + numero;
                else
                    numero = numero;

                return numero;
            }

        } else {

            posPunto = numero.indexOf(".");

            for (i = 0; i < posPunto; i++) {

                cantidadAux1 = cantidadAux1 + numero.charAt(i);
            }

            for (i = posPunto; i < numero.length(); i++) {

                cantidadAux2 = cantidadAux2 + numero.charAt(i);
            }

            if (cantidadAux1.length() > 3) {

                cantidad = ColocarComas(cantidadAux1, posPunto);
                //cantidad = cantidad + cantidadAux2;

            } else {

                if (tieneMenos)
                    numero = "-" + numero;
                else
                    numero = numero;

                return numero;
            }
        }

        if (tieneMenos)
            cantidad = "-" + cantidad;
        else
            cantidad = cantidad;

        return cantidad;
    }

    public static int toInt(String value) {

        try {


            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    public static Float toFloat(String value) {

        try {
            value = value.replaceAll(",", "");
            value = value.replaceAll("\\$", "");
            return Float.parseFloat(value);

        } catch (NumberFormatException e) {

            return 0F;
        }
    }

    public static String calcularFechaDiferenciaDias(String fechaVencto) {

        int anio = toInt(fechaVencto.substring(0, 4));
        int mes = toInt(fechaVencto.substring(4, 6));
        int dia = toInt(fechaVencto.substring(6, 8));

        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al dï¿½a
        java.util.Date hoy = new Date(); //Fecha de hoy

        Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(hoy);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Calendar calendar = new GregorianCalendar(anio, mes - 1, dia);
        java.sql.Date fecha = new java.sql.Date(calendar.getTimeInMillis());

        long diferencia = (hoy.getTime() - fecha.getTime()) / MILLSECS_PER_DAY;

        // SE CALCULA DIFERENCIA
        if (diferencia < 0) {

            diferencia += -1;

        } else {

            if (diferencia == 0 && day != dia) {

                diferencia += -1;
            }
        }

        return String.valueOf(diferencia);
    }

    public static String fechaActual(String format) {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String formatearDireccionWaze(String direccion) {

        return direccion.replaceAll(" ", "%20");
    }

    /**
     * Metodo para eliminar el archivo database.bd al momento
     * en que se genera una nueva ruta
     *
     * @return retorna true si se logra eliminar el archivo o false de lo contrario
     */
    public static boolean eliminarDataBase() {

        boolean exito = false;
        File SDCardRoot = Environment.getExternalStorageDirectory();
        File database = new File(SDCardRoot.getPath() + "/" + Constantes.nameDirApp, "DataBase.db");


        if (database.exists()) {

            exito = database.delete();
        }
        return exito;
    }

    /**
     * Metodo para poner el simbolo $ al principio del valor
     *
     * @param edit      editText donde se de sea colocar
     * @param s         texto al cual se le desea poner el simbolo
     * @param addSimbol variable para verificar si se desea agregar el simbolo a la cadena
     */
    public static void formatNumber(EditText edit, Editable s, boolean addSimbol) {
        String numero = s.toString().trim();
        numero = numero.replaceAll("\\,|\\$", "");
        double temp = 0;
        if (!numero.equals("")) temp = Double.parseDouble(numero);
        String valor = formatMilesDouble(temp, "#.##", addSimbol);
        String cadena = addSimbol ? "$0" : "0";
        valor = valor.equals(cadena) ? "" : valor;
        edit.setText(valor);
        edit.setSelection(edit.getText().length());
    }

    /**
     * Método para poner al principio de la cadena de números el simbolo $
     *
     * @param text      texto al cual fomatear
     * @param addSimbol variable de estado para poner el simbolo
     * @return retorna el texto con el simbolo al principio, o una cadena vacia
     */
    public static String formatNumberText(String text, boolean addSimbol) {
        String numero = text.trim();
        numero = numero.replaceAll("\\,|\\$", "");
        double temp = 0;
        if (!numero.equals("")) temp = Double.parseDouble(numero);
        String valor = formatMilesDouble(temp, "#.##", addSimbol);
        String cadena = addSimbol ? "$0" : "0";
        valor = valor.equals(cadena) ? "" : valor;
        return valor;
    }

    public static Double formatearDecimales(Double numero, Integer numeroDecimales) {
        return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
    }

    public static String formatMilesDouble(double num, String format, boolean addSimbol) {
        DecimalFormat df = new DecimalFormat("#.##");
        String numero = df.format(num);
        numero = numero.replace(',', '.');
        int indexPunto = numero.indexOf(".");
        String entero = "";
        String decimal = "";
        if (indexPunto != -1) {
            String[] numDecimal = numero.split("\\.");
            entero = numDecimal[0];
            decimal = numDecimal[1];
            entero = Utilidades.separarMiles(entero) + "." + decimal;
        } else {
            entero = Utilidades.separarMiles(numero);
        }
        if (addSimbol) return entero;
        return entero;
    }

    public static String Redondear(String numero, int cantDec) {

        int tamNumero = 0;
        double numRedondear;
        int cantAfterPunto;

        if (numero.indexOf(".") == -1) {

            return numero;
        }

        tamNumero = numero.length();
        cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);

        if (cantAfterPunto <= cantDec)
            return numero;

        String numeroSumar = "0.";

        for (int i = 0; i < cantDec; i++) {

            numeroSumar = numeroSumar.concat("0");
        }

        numeroSumar = numeroSumar.concat("5");

        numRedondear = Double.parseDouble(numero);

        numRedondear = numRedondear + Double.parseDouble(numeroSumar);

        numero = String.valueOf(numRedondear);

        tamNumero = numero.length();
        cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);

        if (cantAfterPunto <= cantDec)
            return numero;
        else {

            if (cantDec == 0)
                numero = numero.substring(0, numero.indexOf("."));
            else
                numero = numero.substring(0,
                        (numero.indexOf(".") + 1 + cantDec));

            return numero;
        }
    }

    public static Bitmap rezizedImageFinal(int newHeight, Bitmap bitmap) {
        Matrix matrix;
        Bitmap resizedBitmap;

        try {

            //TAMAÑO DE LA IMAGEN ACTUAL
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            //CALULAR NUEVA ANCHURA
            int newWidth = (newHeight * width) / height;


            // Reescala el Ancho y el Alto de la Imagen
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            if (width > height) {
                matrix.postRotate(90);
            }

            // Crea la Imagen con el nuevo Tamano
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);


            bitmap.recycle();
//	            bitmapOriginal = null;
            return resizedBitmap;

        } catch (Exception e) {

            Log.e("TAG", "resizedImage -> " + e.getMessage(), e);
            return null;

        } finally {

            System.gc();
        }
    }


    /**
     * Metodo para cambiar una fecha a un nuevo formato
     *
     * @param context      contexto de la aplicación
     * @param fecha        fecha a la cual se le desea cambiar el formato
     * @param formatoFecha el formato actual de la fecha
     * @param nuevoFormato el nuevo formato que tendra la fecha
     * @return retorna una cadena con la fecha en el nuevo formato
     */
    public static String cambiarFormatoFecha(Context context, String fecha, String formatoFecha, String nuevoFormato) {
        try {
            Locale location = context.getResources().getConfiguration().locale;//Locacion o region
            Date dateCuota = new SimpleDateFormat(formatoFecha, location).parse(fecha);
            return new SimpleDateFormat(nuevoFormato, location).format(dateCuota);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Metodo para impedir que se repitan clientes en la lista
     *
     * @param listaClientesOrdenados
     * @param codigo
     * @return
     */
    public static boolean existeCliente(ArrayList<String> listaClientesOrdenados, String codigo) {

        for (int i = 0; i < listaClientesOrdenados.size(); i++) {
            if (listaClientesOrdenados.get(i).equals(codigo)) {
                return true;
            }
        }
        return false;
    }


    /**
     * VErificar si el gps esta habilitado
     *
     * @return retorna true si el gps esta habilitado o falso de lo contrario
     */
    public static boolean isLocationServiceEnabled(Context contexto) {
        LocationManager locationManager;
        boolean gps_enabled = false, network_enabled = false;

        locationManager = (LocationManager) contexto.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager != null) {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ex) {
            //do nothing... } try{ network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); }catch(Exception ex){ //do nothing... } return gps_enabled || network_enabled;
        }
        return gps_enabled;
    }

    /////IMPRESORA
    public static String CentrarLinea(String linea, int numSpace) {

        int space, longitud;
        String centrado;
        centrado = "";

        if (linea.length() > numSpace) {

            linea = linea.substring(0, numSpace);
        }

        longitud = linea.length() / 2;

        if (longitud % 2 != 0) {

            longitud++;
        }

        space = numSpace / 2;
        space = space - longitud;

        for (int i = 0; i < space; i++) {

            centrado = centrado + " ";
        }

        centrado = centrado + linea;

        for (int i = centrado.length(); i < numSpace; i++) {

            centrado = centrado + " ";
        }

        return centrado;
    }

    public static String rpad(String cadena, int tamano, String caracter) {
        int i;
        int tamano1;
        tamano1 = cadena.length();
        if (tamano1 > tamano) cadena = cadena.substring(0, tamano);
        tamano1 = cadena.length();
        for (i = tamano1; i < tamano; i++)
            cadena = cadena + caracter;
        return cadena;
    }

    public static String line(String cadena, int tamano) {
        String line = "";
        while (line.length() < tamano) {
            line += cadena;
        }
        return line;
    }

    public static String lpad(String cadena, int tamano, String caracter) {

        int i;
        int tamano1;

        tamano1 = cadena.length();

        if (tamano1 > tamano)
            cadena = cadena.substring(0, tamano);

        tamano1 = cadena.length();

        for (i = tamano1; i < tamano; i++)
            cadena = caracter + cadena;

        return cadena;
    }

    public static String[] split(String original, String separator) {
        Vector nodes = new Vector();
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);
        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++)
                result[loop] = (String) nodes.elementAt(loop);
        }
        return result;
    }


    public static String readLogo(Context contexto) {

        byte[] data = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            InputStream is = contexto.getResources().openRawResource(R.drawable.backshadow);
            Bitmap bi = BitmapFactory.decodeStream(is, new Rect(), options);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 100, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data == null) {

            return "";

        } else {

            Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

            ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
            ////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////

            // 1.0 LOGO SUPERIOR
            String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(0, 0);
            String zplDataLogo = strLogo;

            // return zplDataLogo.getBytes();
            return zplDataLogo;

        }
    }

    public static String ordenarFecha(String fecha) {

        String[] arrOfStr = new String[20];
        arrOfStr[0] = fecha.substring(0,4);
        arrOfStr[1] = fecha.substring(4,6);
        arrOfStr[2] = fecha.substring(6);

        String fechaFormato = arrOfStr[0] + "-" + arrOfStr[1] + "-" + arrOfStr[2];

        return fechaFormato;
    }

    public static String voltearFecha(String fecha) {

        String[] arrOfStr = fecha.split("-");

        if (Integer.parseInt(arrOfStr[0]) > 2000) {
            String fechaFormato = arrOfStr[1] + "-" + arrOfStr[2] + "-" + arrOfStr[0];
            return fechaFormato;
        } else {
            return fecha;
        }

    }

    public static String ordenarFecha2(String fecha) {

        String[] arrOfStr = fecha.split("-");

        if (Integer.parseInt(arrOfStr[0]) > 2000) {
            return fecha;
        } else {
            String fechaFormato = arrOfStr[2] + "-" + arrOfStr[0] + "-" + arrOfStr[1];
            return fechaFormato;
        }

    }

//    public static String ordenarFechaInversa(String fecha) {
//
//        String[] arrOfStr = fecha.split("-");
//
//        String fechaFormato = arrOfStr[0] + "-" + arrOfStr[1] + "-" + arrOfStr[2];
//
//        return fechaFormato;
//    }

    public static String ordenarFechaHora(String fecha) {

        String[] arrOfStr = fecha.split("-");
        String[] arrOfStrHora = arrOfStr[2].split(" ");
        String fechaFormato;

        if(arrOfStr[0].length() > 2)
        {
            fechaFormato = fecha;
        }
        else
        {
            fechaFormato = arrOfStrHora[0] + "-" + arrOfStr[0] + "-" + arrOfStr[1] + " " + arrOfStrHora[1];
        }

        return fechaFormato;
    }

    public static String obtenerImei(Context context) {

        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static float ToFloat(String value) {

        try {

            return Float.parseFloat(value);

        } catch (NumberFormatException e) {

            return 0F;
        }
    }

    public static void MostrarAlertDialog(final Context context, String mensaje) {

        AlertDialog alertDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertDialog = builder.create();
        alertDialog.setMessage(mensaje);
        alertDialog.show();
    }

}