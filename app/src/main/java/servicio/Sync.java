package servicio;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.Main;
import dataobject.Usuario;
import utilidades.Constantes;
import utilidades.Utilidades;

public class Sync extends Thread {

    private final static String TAG = Sync.class.getName();

    Context context;

    private Synchronizer sincronizador;
    private int codeRequest;

    //Guarda la respuesta del servidor
    private String respuestaServer = "";

    private String mensaje = "";
    private boolean ok;

    // PARAMETROS DE PETICION
    public String user;
    public String password;
    public String version;
    public String token;
    public String androidId;
    public String regionalUser;

    // PARAMETROS PETICION BUSQUEDA
    public int opcionNitSap;
    public int opcionNombreRazonSocial;
    public int opcionPoblacion;
    public String opcionSegmento;
    public String opcionCondicionPago;
    public String parametroBusqueda;
    public String vendedorSeleccionado;
    public String poblacionSeleccionada;
    public String segmentoSeleccionado;

    //PARAMETROS FILTRO DINAMICO
    public String parametro;
    public int hayParametroBusqueda;

    // PARAMETROS PETICION BUSQUEDA X CRITERIOS
    public String codigoVendedor;
    public String codigoPoblacion;
    public String codigoSegmento;
    public String codigoDia;
    public String codRegional;

    // PARAMETROS PETICION BUSQUEDA RUTA ANTERIOR
    public String ruta;

    // PARAMETROS SINCRONIZACION DATABASE CLIENTES
    public String imei;
    public String clientes;

    public String codigoCliente;

    /**
     * Constructor de la clase
     *
     * @param sincronizador clase que implementa la interfaz
     * @param codeRequest   constante para identificar que tipo de operacion realizar
     */
    public Sync(Synchronizer sincronizador, int codeRequest, Context context) {

        this.sincronizador = sincronizador;
        this.codeRequest = codeRequest;
        this.context = context;
    }



    public void run() {

        switch (codeRequest) {

            case Constantes.LOGIN:
                login();

                break;

            case Constantes.DESCARGARINFO:
                descargarDataBase();

                break;

            case Constantes.BUSQUEDACLIENTESPARAMETRO:
                buscarClientesParametro();
                break;

            case Constantes.BUSQUEDACRITERIOSSPINNER:
                buscarCriteriosBusquedaClientes();
                break;

            case Constantes.BUSQUEDACLIENTESCRITERIOS:
                buscarClientesCriterios();
                break;

            case Constantes.BUSQUEDACRITERIOSCONDICIONSPINNER:
                buscarCriteriosCondicionBusquedaClientes();
                break;

            case Constantes.BUSQUEDARUTEROANTERIOR:
                buscarRuteroAnterior();
                break;

            case Constantes.BUSQUEDACLIENTESRUTEROANTERIOR:
                buscarClientesRuteroAnterior();
                break;

            case Constantes.SINCRONIZARCLIENTESSELECCIONADOS:

                break;

            case Constantes.TERMINARDIA:
                terminarDia();
                break;

            case Constantes.BLOQUEARCLIENTE:
                bloquearCliente();
                break;

            case Constantes.ENVIARINFORMACION:
                enviarInformacion();
                break;

            case Constantes.ENVIAR_TOKEN_NOTIFICATION:
                enviarTokenPost();
                break;

            case Constantes.FILTRODINAMICO:
                actualizarFiltrosDinamicos();
                break;
            case Constantes.LISTAREGIONALES:
                listarRegionales();
                break;
            case Constantes.LISTARVENDEDORES:
                listarVendedorRegional();
                break;
            case Constantes.LISTARPOBLACIONVENDEDORES:
                listarPoblacionesVendedor();
                break;
            case Constantes.LISTARSEGMENTOVENDEDORES:
                listarSegmentoVendedores();
                break;
            case Constantes.LISTARDIAS:
                listarDias();
                break;
            case Constantes.DOWNLOAD_VERSION_APP:
                DownloadVersionApp();
                break;
        }
    }

    /**
     * Metodopara realizar la consulta para que los filtros de busqueda de clientes por poblacion sean dinamicos
     * Actualizar los listados de opciones de segmento y condicion cada vez que se seleccione una opcion de las dos
     */
    private void actualizarFiltrosDinamicos() {

        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_FILTRO_DINAMICO;

        try {

            String urlParameters = "usr=" + user + "&param=" + parametro.trim() + "&poblacion=" + hayParametroBusqueda + "&segmento=" + opcionSegmento + "&condicion=" + opcionCondicionPago;

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {

                    sb.append(line).append("\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;
            } else {
                respuestaServer = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "Criterios");
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void descargarDataBase() {

        boolean ok = false;
        InputStream inputStream = null;
        FileOutputStream fileOutput = null;
        HttpURLConnection urlConnection = null;

        try {

            /*
             * Carga la Configuracion del Usuario.
             */
            Locale locale = Locale.getDefault();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss", locale);

            String urlDataBase;
            if (!user.isEmpty()) {
                urlDataBase = Constantes.URL_SYNC_DB + Constantes.URL_CREARDB + "?un=" + user + "&pw=" + password + "&vr=" + Main.version + "&imei=" + imei;
            } else {
                urlDataBase = Constantes.URL_SYNC_DB + Constantes.URL_CREARDB + "?un=" + user + "&pw=" + password + "&vr=" + Main.version + "&imei=" + imei;
            }

            Log.i(TAG, "URL DB = " + urlDataBase);

            URL url = new URL(urlDataBase);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
//            urlConnection.setConnectTimeout(30 * 1000);

            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            String contentDisposition = urlConnection.getHeaderField("Content-Disposition");

            if (contentDisposition != null) { //Viene Archivo Adjunto

                /*
                 * Se obtiene la ruta del SD Card, para guardar la Base de Datos.
                 * Y se crea el Archivo de la BD
                 **/
                String fileName = "Temporal.zip";
                File file = new File(Utilidades.dirApp(context), fileName);

                if (file.exists())
                    file.delete();

                if (file.createNewFile()) {

                    fileOutput = new FileOutputStream(file);

                    long downloadedSize = 0;
                    int bufferLength = 0;
                    byte[] buffer = new byte[1024];

                    /*
                     * SE LEE LA INFORMACION DEL BUFFER Y SE ESCRIBE EL CONTENIDO EN EL ARCHIVO DE SALIDA
                     **/
                    while ((bufferLength = inputStream.read(buffer)) > 0) {

                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                    }

                    fileOutput.flush();
                    fileOutput.close();
                    inputStream.close();

                    long content_length = Utilidades.ToLong(urlConnection.getHeaderField("content-length"));

                    if (content_length == 0) {

                        mensaje = "No hay conexion, por favor intente de nuevo";

                    } else if (content_length != downloadedSize) { // La longitud de descarga no es igual al Content Length del Archivo

                        mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo";

                    } else {

                        Descomprimir(file);

                        ok = true;
                        mensaje = "Se ha cargado al cliente al rutero correctamente";
                    }

                } else {

                    mensaje = "No se pudo crear el archivo de la Base de Datos";
                }

            } else { //No hay archivo adjunto, se procesa el Mensaje de respuesta del Servidor

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    respuestaServer = respuestaServer.concat(line);
                }

                if(respuestaServer.equals("99")) {
                    mensaje = "imeiError";
                }
                else if (respuestaServer.equals(""))
                    mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo.";
                else
                    mensaje = respuestaServer;
            }

        } catch (Exception e) {

            String motivo = e.getMessage();

            if (motivo.startsWith("http://"))
                motivo = "Pagina no Encontrada: CrearDB.aspx";

            mensaje = "No se pudo descargar la Base de Datos\n\n";
            mensaje += "Motivo: " + motivo;

            Log.e(TAG, "DownloadDataBase: " + e.getMessage(), e);

        } finally {

            try {

                if (fileOutput != null)
                    fileOutput.close();

                if (inputStream != null)
                    inputStream.close();

            } catch (IOException e) {

                Log.e(TAG, "" + e.getMessage());
            }

            if (urlConnection != null)
                urlConnection.disconnect();
        }


        sincronizador.respSync(ok, mensaje, mensaje, codeRequest);
    }


    private void descargarDataBase2() {

        boolean ok = false;
        InputStream inputStream = null;
        FileOutputStream fileOutput = null;
        HttpURLConnection urlConnection = null;

        try {

            /*
             * Carga la Configuracion del Usuario.
             */
            Locale locale = Locale.getDefault();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss", locale);

            String urlDataBase;
            if (!user.isEmpty()) {
                urlDataBase = Constantes.URL_SYNC_DB + Constantes.URL_CREARDB;
            } else {
                urlDataBase = Constantes.URL_SYNC_DB + Constantes.URL_CREARDB;
            }

            Log.i(TAG, "URL DB = " + urlDataBase);

            URL url = new URL(urlDataBase);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(30 * 1000);

            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            String contentDisposition = urlConnection.getHeaderField("Content-Disposition");

            if (contentDisposition != null) { //Viene Archivo Adjunto

                /*
                 * Se obtiene la ruta del SD Card, para guardar la Base de Datos.
                 * Y se crea el Archivo de la BD
                 **/
                String fileName = "Temporal.zip";
                File file = new File(Utilidades.dirApp(context), fileName);

                if (file.exists())
                    file.delete();

                if (file.createNewFile()) {

                    fileOutput = new FileOutputStream(file);

                    long downloadedSize = 0;
                    int bufferLength = 0;
                    byte[] buffer = new byte[1024];

                    /*
                     * SE LEE LA INFORMACION DEL BUFFER Y SE ESCRIBE EL CONTENIDO EN EL ARCHIVO DE SALIDA
                     **/
                    while ((bufferLength = inputStream.read(buffer)) > 0) {

                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                    }

                    fileOutput.flush();
                    fileOutput.close();
                    inputStream.close();

                    long content_length = Utilidades.ToLong(urlConnection.getHeaderField("content-length"));

                    if (content_length == 0) {

                        mensaje = "No hay conexion, por favor intente de nuevo";

                    } else if (content_length != downloadedSize) { // La longitud de descarga no es igual al Content Length del Archivo

                        mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo";

                    } else {

                        Descomprimir(file);

                        ok = true;
                        mensaje = "Se ha cargado al cliente al rutero correctamente";
                    }

                } else {

                    mensaje = "No se pudo crear el archivo de la Base de Datos";
                }

            } else { //No hay archivo adjunto, se procesa el Mensaje de respuesta del Servidor

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    respuestaServer = respuestaServer.concat(line);
                }

                if (respuestaServer.equals(""))
                    mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo.";
                else
                    mensaje = respuestaServer;
            }

        } catch (Exception e) {

            String motivo = e.getMessage();

            if (motivo.startsWith("http://"))
                motivo = "Pagina no Encontrada: CrearDB.aspx";

            mensaje = "No se pudo descargar la Base de Datos\n\n";
            mensaje += "Motivo: " + motivo;

            Log.e(TAG, "DownloadDataBase: " + e.getMessage(), e);

        } finally {

            try {

                if (fileOutput != null)
                    fileOutput.close();

                if (inputStream != null)
                    inputStream.close();

            } catch (IOException e) {

                Log.e(TAG, "" + e.getMessage());
            }

            if (urlConnection != null)
                urlConnection.disconnect();
        }


        sincronizador.respSync(ok, mensaje, mensaje, codeRequest);
    }


    private void login() {

        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + "?un=" + user + "&pw=" + password;
        try {

            String urlParameters = Constantes.URL_SYNC + "?un=" + user + "&pw=" + password;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            Log.i(TAG, "URL LOGIN = " + urlParameters);

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {

                    sb.append(line + "\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;

            } else {
                String error = conexion.getResponseMessage();

                if (error.contains("Usuario Incorrecto\n")) {

                    mensaje = "Usuario y/o clave incorrecto";
                    respuestaServer = "error";

                } else {

                    mensaje = mensajeHttpError(statusCode, "Login");
                    respuestaServer = "error";
                }
            }
        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void buscarClientesParametro() {

        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_BUSCAR_CLIENTES_PARAMETRO;

        try {

            String urlParameters =
                    "&usr=" + user +
                            "&param=" + parametroBusqueda;


            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    br.close();

                    mensaje = "ok";
                    respuestaServer = sb.toString();
                    ok = true;

                    break;

                case 400:

                    String errorNoResult = conexion.getResponseMessage();
                    mensaje = "Búsqueda sin resultados.";
                    respuestaServer = "Alerta";
                    break;

                default:

                    String error = conexion.getResponseMessage();
                    mensaje = mensajeHttpError(statusCode, "Clientes Parametro");
                    respuestaServer = "error";
                    break;
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void buscarClientesRuteroAnterior() {

        ok = false;

        int longTimeout = 1 * 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_BUSCAR_CLIENTES_RUTERO_ANTERIOR;

        try {

            String urlParameters = "usr=" + user +
                    "&ruta=" + ruta;

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    br.close();

                    mensaje = "ok";
                    respuestaServer = sb.toString();
                    ok = true;

                    break;

                default:

                    String error = conexion.getResponseMessage();
                    mensaje = mensajeHttpError(statusCode, "Clientes Parametro");
                    respuestaServer = "error";
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void buscarClientesCriterios() {

        ok = false;

        int longTimeout = 1 * 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_BUSCAR_CLIENTES_CRITERIO;

        try {

            String urlParameters = "usr=" + user +
                    "&poblacion=" + codigoPoblacion +
                    "&segmento=" + codigoSegmento +
                    "&vendedor=" + codigoVendedor +
                    "&dia=" + codigoDia;

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /**
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    br.close();

                    mensaje = "ok";
                    respuestaServer = sb.toString();
                    ok = true;
                    break;

                case 400:

                    String errorNoResult = conexion.getResponseMessage();
                    String motivo = mensajeHttpError(statusCode, "Criterios");
                    Log.e(TAG, motivo);
                    mensaje = "Búsqueda sin resultados.";
                    respuestaServer = "Alerta";
                    break;

                default:

                    String error = conexion.getResponseMessage();
                    mensaje = mensajeHttpError(statusCode, "Clientes Criterio");
                    respuestaServer = "error";
                    break;
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void buscarCriteriosBusquedaClientes() {

        ok = false;

        int longTimeout = 1 * 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_BUSCAR_CRITERIOS;

        try {

            String urlParameters = "usr=" + user;

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /**
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    br.close();

                    mensaje = "ok";
                    respuestaServer = sb.toString();
                    ok = true;

                    break;

                default:

                    String error = conexion.getResponseMessage();
                    mensaje = mensajeHttpError(statusCode, "Criterios");
                    respuestaServer = "error";
                    break;
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void buscarCriteriosCondicionBusquedaClientes() {

        ok = false;

        int longTimeout = 1 * 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_BUSCAR_CRITERIOS_CONDICION;

        try {

            String urlParameters = "usr=" + user;

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /**
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    br.close();

                    mensaje = "ok";
                    respuestaServer = sb.toString();
                    ok = true;

                    break;

                default:

                    String error = conexion.getResponseMessage();
                    mensaje = mensajeHttpError(statusCode, "Criterios");
                    respuestaServer = "error";
                    break;
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void buscarRuteroAnterior() {

        ok = false;

        int longTimeout = 1 * 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_BUSCAR_RUTERO_ANTERIOR;

        try {

            String urlParameters = "usr=" + user;

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line).append("\n");
                    }

                    br.close();

                    mensaje = "ok";
                    respuestaServer = sb.toString();
                    ok = true;

                    break;

                default:

                    String error = conexion.getResponseMessage();
                    mensaje = mensajeHttpError(statusCode, "Criterios");
                    respuestaServer = "error";
                    break;
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    private void terminarDia() {
        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_TERMINAR_DIA + "?un=" + user;

        try {
            BufferedReader bufferedReader;


            String urlParameters = "un=" + user;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             */
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                /*
                 * LEE LA RESPUESTA DEL SERVIDOR
                 **/
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String line;

                while ((line = bufferedReader.readLine()) != null) {

                    sb.append(line).append("\n");
                }

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;

            } else {
                String error = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "Criterios");
                respuestaServer = "error: " + error;
            }


        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();
        } finally {
            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }

    }

    /**
     * Metodo para comprimir la base de datos temporal
     * y poder enviar la información
     *
     * @return true si la base de datos se pudo comprimir, o false si sucedio algún error
     */
    public boolean comprimirArchivo() {

        File zipPedido = new File(Utilidades.dirApp(context), "Temp.zip");

        if (zipPedido.exists())
            zipPedido.delete();

        FileOutputStream out = null;
        GZIPOutputStream gZipOut = null;
        FileInputStream fileInputStream = null;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "Temp.db");

            if (dbFile.exists()) {

                fileInputStream = new FileInputStream(dbFile);

                int lenFile = fileInputStream.available();
                byte[] buffer = new byte[fileInputStream.available()];

                int byteRead = fileInputStream.read(buffer);

                if (byteRead == lenFile) {

                    out = new FileOutputStream(zipPedido);
                    gZipOut = new GZIPOutputStream(out);
                    gZipOut.write(buffer);
                    return true;
                }

                if (zipPedido.exists())
                    zipPedido.delete();
            }

            return false;

        } catch (Exception e) {

            if (zipPedido.exists())
                zipPedido.delete();

            Log.e("ComprimirArchivo", e.getMessage(), e);
            return false;

        } finally {

            try {

                if (gZipOut != null)
                    gZipOut.close();

                if (out != null)
                    out.close();

                if (fileInputStream != null)
                    fileInputStream.close();

            } catch (Exception e) {

                Log.e("ComprimirArchivo", e.getMessage(), e);
            }
        }
    }

    /**
     * DESCOMPRIME LA BASE DE DATOS
     *
     * @param fileZip
     */


    private void Descomprimir(File fileZip) {

        try {

            if (fileZip.exists()) {

                String nameFile = fileZip.getName().replace(".zip", "");
                File fileZipAux = new File(Utilidades.dirApp(context), nameFile);

                if (!fileZipAux.exists())
                    fileZipAux.delete();

                FileInputStream fin = new FileInputStream(fileZip);
                ZipInputStream zin = new ZipInputStream(fin);

                ZipEntry ze = null;

                while ((ze = zin.getNextEntry()) != null) {

                    Log.v("Descomprimir", "Unzipping " + ze.getName());

                    if (ze.isDirectory()) {

                        dirChecker(ze.getName());

                    } else {

                        String pathFile = Utilidades.dirApp(context) + "/" + ze.getName();
                        File file = new File(pathFile);
                        FileOutputStream fout = new FileOutputStream(file);

                        int bufferLength = 0;
                        byte[] buffer = new byte[1024];

                        while ((bufferLength = zin.read(buffer)) > 0) {

                            fout.write(buffer, 0, bufferLength);
                        }

                        zin.closeEntry();
                        fout.flush();
                        fout.close();
                    }
                }
                zin.close();
            }

        } catch (Exception e) {

            Log.e("Descomprimir", e.getMessage(), e);
        }
    }

    /**
     * VALIDA EL DIRECTORIO
     *
     * @param dir
     */
    private void dirChecker(String dir) {

        File f = new File(Utilidades.dirApp(context).getPath() + "/" + dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public String mensajeHttpError(int statusCode, String pagina) {

        switch (statusCode) {

            case HttpStatus.SC_NOT_IMPLEMENTED:
                return "Conexion no Disponible: No Implementado.";

            case HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED:
                return "Conexion no Disponible: Version No Soportada.";

            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                return "Conexion no Disponible: Error Interno.";

            case HttpStatus.SC_GATEWAY_TIMEOUT:
                return "Conexion no Disponible: Tiempo de Conexion Excedido.";

            case HttpStatus.SC_BAD_GATEWAY:
                return "Conexion no Disponible: Mala Conexion.";

            case HttpStatus.SC_NOT_FOUND:
                return "Pagina No Encontrada: " + pagina + ".";

            default:
                return "Conexion no Disponible. Http Error Code: " + statusCode + ".";
        }
    }

    private void bloquearCliente() {

        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_BLOQUEO_CLIENTE;

        try {

            String urlParameters = "usr=" + user +
                    "&cli=" + codigoCliente;

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {

                    sb.append(line).append("\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;
            } else {
                String error = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "BloueoClientes");
                Log.e(TAG, "" + error);
                respuestaServer = "Error: " + error;
            }

        } catch (Exception e) {

            mensaje = "Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e(TAG, "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Metodo por el cual se envia la informacion al servidor, esta infomrcion es la contenida
     * en el base de datos tempo.db la cual se comprime en un .zip y se envia por una url
     */
    private void enviarInformacion() {
        ok = false;
        String msg = "";
        int intentos = 0;
        int maxIntentos = 2;
        boolean exito = false;

        if (comprimirArchivo()) {
            File zipPedido = new File(Utilidades.dirApp(context), "Temp.zip");

            if (!zipPedido.exists()) {
                Log.i("EnviarPedido", "El archivo Temp.zip no Existe");
                sincronizador.respSync(ok, "", "El archivo Temp.zip no Existe", codeRequest);
                return;
            }

            while (intentos < maxIntentos && !exito) {
                intentos++;
                Log.i("EnviarPedido", "Intento #" + intentos);

                DataOutputStream dos = null;
                HttpURLConnection conexion = null;
                BufferedReader bufferedReader = null;
                FileInputStream fileInputStream = null;

                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                int bytesRead, bytesAvailable, bufferSize;

                String urlUpLoad = Constantes.URL_SYNC_DB + Constantes.URL_ENVIAR_INFORMACION + "?un=" + user + "&ext=zip&fecha=" + Utilidades.fechaActual("yyyy/MM/dd") + "&fm=" + Utilidades.fechaActual("yyyy/MM/dd");
                Log.i("EnviarPedido", "URL Enviar Info = " + urlUpLoad);

                try {
                    URL url = new URL(urlUpLoad);
                    conexion = (HttpURLConnection) url.openConnection();

                    conexion.setDoInput(true);
                    conexion.setDoOutput(true);
                    conexion.setUseCaches(false);
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Connection", "Keep-Alive");
                    conexion.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
                    conexion.setConnectTimeout(6000);

                    fileInputStream = new FileInputStream(zipPedido);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    dos = new DataOutputStream(conexion.getOutputStream());
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dos.flush();
                    Log.i("EnviarPedido", "Enviando informacion del Archivo");

                    bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    String line;

                    respuestaServer = "";
                    while ((line = bufferedReader.readLine()) != null)
                        respuestaServer = String.format("%s%s", respuestaServer, line);

                    if (respuestaServer.startsWith("ok") || respuestaServer.startsWith("listo")) {
                        ok = true;
                        exito = true; // Éxito, salimos del bucle
                    } else {
                        if (respuestaServer.equals(""))
                            msg = "Sin respuesta del servidor";
                        else
                            msg = respuestaServer;

                        if (intentos == maxIntentos) {
                            break;
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    Log.i("EnviarPedido", "respuesta: " + respuestaServer);

                } catch (Exception ex) {
                    msg = ex.getMessage();
                    Log.e("EnviarPedido", "Intento " + intentos + " fallido: " + msg, ex);

                    if (intentos == maxIntentos) {
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                } finally {
                    try {
                        if (bufferedReader != null)
                            bufferedReader.close();
                        if (fileInputStream != null)
                            fileInputStream.close();
                        if (dos != null)
                            dos.close();
                        if (conexion != null)
                            conexion.disconnect();
                    } catch (IOException e) {
                        Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
                    }
                }
            }
        } else {
            msg = "Error comprimiendo la Base de datos Pedido";
            Log.e("FileUpLoad", msg);
        }

        if (respuestaServer.equals(""))
            respuestaServer = "error, Sin respuesta del servidor";

        sincronizador.respSync(ok, respuestaServer, msg, codeRequest);
    }

    /**
     * Metodo para enviar el token
     */
    public void enviarTokenPost() {

        boolean ok = false;
        respuestaServer = "";
        HttpURLConnection urlConnection = null;

        try {

            String strURL = Constantes.URL_TOKEN + "actualizarToken.aspx?usuario=" + this.user + "&token=" + this.token;

            Log.i("token", "URL token" +
                    "" +
                    ": " + strURL);
            System.out.println(strURL);

            URL url = new URL(strURL);
            urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            int statusCode = urlConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {

                /*****************************************************************
                 * Si la Solicitud al Servidor fue Satisfactoria, lee la Respuesta
                 *****************************************************************/

                String line = null;
                while ((line = reader.readLine()) != null) {

                    respuestaServer += line;
                }

                if (respuestaServer.startsWith("Ok") || respuestaServer.toUpperCase().indexOf("LISTO") != -1) {

                    ok = true;
                    mensaje = "token enviado con exito";
                    /*
                     * String[] datos = respuestaServer.split( ";" );
                     *
                     * if( datos.length > 4 ) Main.numCargues = datos[4]; else Main.numCargues =
                     * "NO_ASIGNADO";
                     */

                } else {

                    ok = false;
                    mensaje = "No se pudo registrar el token.\n" + respuestaServer;
                }

            } else if (statusCode == -1) {

                ok = false;
                mensaje = "No se pudo registrar el token. Pagina No Encontrada: ";

            } else {

                ok = false;
                mensaje = "Error enviando token";
            }

        } catch (Exception e) {

            String msg = e.getMessage();
            if (msg.startsWith("http://"))
                msg = "\n\nMotivo: Pagina No Encontrada: ";
            else
                msg = "\n\nMotivo: " + msg;

            mensaje = "No se pudo establecer la conexion con el servidor." + msg;

        } finally {

            if (urlConnection != null)
                urlConnection.disconnect();
        }

        sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);
    }

    /**
     * Metodo para obtener la lista de regionales que se pueden ver en la aplicacion
     */
    private void listarRegionales() {

        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_LISTAR_REGIONALES;

        try {

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {

                    sb.append(line).append("\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;
            } else {
                String error = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "ListaRegionales.aspx");
                Log.e(TAG, "" + error);
                respuestaServer = "Error: " + error;
            }

        } catch (Exception e) {

            mensaje = "Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e(TAG, "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Metodo para consultar el servicio que lista de vendedores, poblacion y segmento segun la regional
     */
    private void listarVendedorRegional() {
        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_LISTAR_VENDEDORES;

        try {

            String urlParameters = "regional=" + codRegional;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             */
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;
            } else {
                String error = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "Criterios");
                respuestaServer = error;
            }

        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);
            } catch (Exception e) {
                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Metodo para consultar el servicio para listar las poblaciones del vendedor que se seleccionaron
     * en la pantalla de búsqueda de rutero vendedor
     */
    private void listarPoblacionesVendedor() {

        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_LISTAR_POBLACIONES_VENDEDOR;

        try {

            String urlParameters = "vendedor=" + vendedorSeleccionado;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             */
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;
            } else {
                String error = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "Criterios");
                respuestaServer = error;
            }

        } catch (Exception e) {

            mensaje = "listarPoblacionesVendedor: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);
            } catch (Exception e) {
                Log.e("PoblacionesVendedor", "listarPoblacionesVendedor: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Metodo para consultar el servicio para saber los segmentos de un cliente que se selecciono en la
     * pantalla de rutero vendedor el cual corresponde a un spinner de la pantalla
     */
    private void listarSegmentoVendedores() {

        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_LISTAR_SEGMENTO_VENDEDOR;

        try {

            String urlParameters = "vendedor=" + vendedorSeleccionado +
                    "&poblacion=" + poblacionSeleccionada;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             */
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;
            } else {
                String error = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "Criterios");
                respuestaServer = error;
            }

        } catch (Exception e) {

            mensaje = "listarSegmentoVendedores: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);
            } catch (Exception e) {
                Log.e("SegmentoVendedores", "listarSegmentoVendedores: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Metodo para consultar el servicio que trae los dias que tiene ruta el vendedor que se selecciono
     * en la pantalla de rutero de vendedor con el fin de obetener el rutero de este
     */
    private void listarDias() {
        ok = false;

        int longTimeout = 45 * 1000;

        HttpURLConnection conexion = null;

        String urlUpLoad = Constantes.URL_SYNC + Constantes.URL_LISTAR_DIAS;

        try {

            String urlParameters = "vendedor=" + vendedorSeleccionado +
                    "&poblacion=" + poblacionSeleccionada +
                    "&segmento=" + segmentoSeleccionado;

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            // Send post request
            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            DataOutputStream wr = new DataOutputStream(conexion.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            /*
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             */
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                br.close();

                mensaje = "ok";
                respuestaServer = sb.toString();
                ok = true;
            } else {
                String error = conexion.getResponseMessage();
                mensaje = mensajeHttpError(statusCode, "Criterios");
                respuestaServer = error;
            }

        } catch (Exception e) {

            mensaje = "listarDias: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);
            } catch (Exception e) {
                Log.e("listarDias", "listarDias: " + e.getMessage(), e);
            }
        }
    }

    public void DownloadVersionApp() {

        boolean ok = false;
        InputStream inputStream = null;
        FileOutputStream fileOutput = null;

        try {

            String urlPorEmpresa = "";
            Usuario usuario = DataBaseBO.obtenerUsuario(context);

            urlPorEmpresa = DataBaseBO.obtenerUrlDownloadVersionApp(context);

            URL url = new URL(urlPorEmpresa);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
//	        urlConnection.setDoOutput(true);

            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            File file = new File(Utilidades.dirApp(context), Constantes.fileNameApk);

            if (file.exists())
                file.delete();

            if (file.createNewFile()) {

                fileOutput = new FileOutputStream(file);

                long downloadedSize = 0;
                int bufferLength = 0;
                byte[] buffer = new byte[1024];

                /**
                 * SE LEE LA INFORMACION DEL BUFFER Y SE ESCRIBE EL CONTENIDO EN EL ARCHIVO DE SALIDA
                 **/
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {

                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                }

                fileOutput.flush();
                fileOutput.close();
                inputStream.close();

                long content_length = Utilidades.ToLong(urlConnection.getHeaderField("content-length"));

                if (content_length == 0) {

                    ok = false;
                    mensaje = "Error de conexion, por favor intente de nuevo";

                } else  if (content_length != downloadedSize) { // La longitud de descarga no es igual al Content Length del Archivo

                    ok = false;
                    mensaje = "Error descargando la nueva version, por favor intente de nuevo";

                } else {

                    ok = true;
                    mensaje = "Descargo correctamente la Nueva Version";
                }

            } else {

                mensaje = "Error Creando el Archivo de la Nueva Version";
                ok = false;
            }

        } catch (Exception e) {

            mensaje = "Error Descargando la Nueva version de la Aplicacion\n";
            mensaje += "Detalle Error: " + e.getMessage();
            Log.e("Sync DownloadVersionApp", e.getMessage(), e);
            ok = false;

        } finally {

            try {

                if (fileOutput != null)
                    fileOutput.close();

                if (inputStream != null)
                    inputStream.close();

            } catch (IOException e) { }
        }

        sincronizador.respSync(ok, mensaje, mensaje, codeRequest);
    }

}
