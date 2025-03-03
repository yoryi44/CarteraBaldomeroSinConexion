package utilidades;

public class Constantes {

    private static final String TITULO;
    public final static String URL_SYNC;
    public final static String URL_SYNC_DB;
    private final static String URL_DOWNLOAD_NEW_VERSION;
    public final static int PRUEBAS = 1;
    public final static int PRODUCCION = 2;
    public final static int APLICACION =  PRUEBAS;
    public static String nameDirApp = "CarteraBaldomero";
    public static String nameDirAppTemp = "/CarteraBaldomero/Temp";

    static {

        switch (APLICACION) {

            case PRUEBAS:
                TITULO = "CARTERA BALDOMERO";
                URL_SYNC = "http://qas.movilidadcn.com/SyncCarteraBaldomero/LogIn.aspx/";
                URL_SYNC_DB = "http://qas.movilidadcn.com/SyncCarteraBaldomero";
                URL_DOWNLOAD_NEW_VERSION = "";
                break;

            case PRODUCCION:
                TITULO = "CARTERA BALDOMERO";
                URL_SYNC = "https://movilidadcn.com/SyncCarteraBaldomero/LogIn.aspx/";
                URL_SYNC_DB = "https://movilidadcn.com/SyncCarteraBaldomero";
                URL_DOWNLOAD_NEW_VERSION = "";
                break;

            default:
                TITULO = " - Sin Definir";
                URL_SYNC = "Sin_Definir";
                URL_SYNC_DB = "";
                URL_DOWNLOAD_NEW_VERSION = "Sin_Definir";
                break;
        }
    }

    public static String ADMINISTRADOR = "1";
    public static String COORDINADOR = "2";
    public static String EJECUTOR = "3";


    //Constantes del Sincronizador
    public static final int LOGIN = 1;
    public static final int BUSQUEDACLIENTESPARAMETRO = 2;
    public static final int BUSQUEDACRITERIOSSPINNER = 3;
    public static final int BUSQUEDACLIENTESCRITERIOS = 4;
    public static final int BUSQUEDACRITERIOSCONDICIONSPINNER = 5;
    public static final int BUSQUEDARUTEROANTERIOR = 6;
    public static final int BUSQUEDACLIENTESRUTEROANTERIOR = 7;
    public static final int SINCRONIZARCLIENTESSELECCIONADOS = 8;
    public static final int TERMINARDIA = 9;
    public static final int BLOQUEARCLIENTE = 10;
    public static final int ENVIARINFORMACION = 11;
    public static final int FILTRODINAMICO = 12;
    public static final int LISTAREGIONALES= 13;
    public static final int LISTARVENDEDORES=14;
    public static final int LISTARPOBLACIONVENDEDORES=15;
    public static final int LISTARSEGMENTOVENDEDORES=16;
    public static final int LISTARDIAS=17;
    public final static int REQUEST_DISCOVERABLE_CODE = 1016;
    public final static String CONFIG_IMPRESORA = "PRINTER";
    public final static String MAC_IMPRESORA    = "MAC";
    public final static String LABEL_IMPRESORA  = "LABEL";
    public final static String TIPO_IMPRESORA  = "TIPO";

    public static final int DESCARGARINFO=55;
    public static final int CLAVEUSUARIO=66;

    public final static String NOMBRE_DIRECTORIO_PDF = "PDF's";

    public static final int ENCUESTA_OBLIGATORIA = 1;
    public static final int ENCUESTA_ESTUDIO_PUNTO_MEALS = 641;
    public static final int RESP_ELABORACIONPLANO = 1010;
    public final static int RESP_FORM_PREGUNTAS = 1020;
    public final static int RESP_CLIENTE_NUEVO = 1014;
    public static final int ITEM_SPINNER = 2;
    public static final int PREGUNTA_SELECCION_MULTIPLE_DATOS = 16;
    public static final int PREGUNTA_MATRIZ_NUMERICA = 18;
    public static final int PREGUNTA_MATRIZ_CHECK = 19;
    public static final int PREGUNTA_MATRIZ_COMBINADA = 20;
    public static final int PREGUNTA_SELECCION_MULTIPLE_TEXTO = 100; //PONER 21
    public final static String ALMACENAR_CODIGO_RESPUESTAS_ESTUDIO_PUNTOS = "estud_puntos";
    public final static String ALMACENAR_DATOS_UTIL = "almacenar_datos_utils";

    public final static String URL_CREARDB = "/CrearDB.aspx";
    public final static String URL_BUSCAR_CLIENTES_PARAMETRO = "/BuscarCliente.aspx";
    public final static String URL_BUSCAR_CLIENTES_CRITERIO = "/BuscarClienteVendedor.aspx";
    public final static String URL_BUSCAR_CRITERIOS = "/ListaBusquedaRegional.aspx";
    public final static String URL_BUSCAR_CRITERIOS_CONDICION = "/ListaBusquedaAbierta.aspx";
    public final static String URL_BUSCAR_RUTERO_ANTERIOR = "/BuscarRutero.aspx";
    public final static String URL_BUSCAR_CLIENTES_RUTERO_ANTERIOR = "/ListaBusquedaRutero.aspx";
    public final static String URL_FILTRO_DINAMICO = "/ListaBusquedaFiltroDimanico.aspx";
    public final static String URL_SINCRONIZAR_CLIENTES_SELECCIONADOS = "/CrearDB.aspx";
    public final static String URL_ENVIAR_INFORMACION = "/RegistrarInfo.aspx";
    public final static String URL_TERMINAR_DIA = "/terminarDia.aspx";
    public final static String URL_BLOQUEO_CLIENTE = "/BloquearCliente.aspx";
    public final static String URL_LISTAR_REGIONALES= "/ListaRegionales.aspx";
    public final static String URL_LISTAR_VENDEDORES = "/ListaVendedoresRegional.aspx";
    public final static String URL_LISTAR_POBLACIONES_VENDEDOR = "/ListaPoblacionesVendedor.aspx";
    public final static String URL_LISTAR_SEGMENTO_VENDEDOR = "/ListaSegmentosPoblacionVendedor.aspx";
    public final static String URL_LISTAR_DIAS = "/ListaBusquedaDias.aspx";

    public static final int BuscarClienteActivity = 100;
    public static final int RutaVendedorActivity = 101;
    public static final int CargarRuteroAnteriorActivity = 102;
    public static final int ENVIAR_TOKEN_NOTIFICATION = 1111;
    public static final String URL_TOKEN = "http://movilidadcn.com/ServiciosAppCartera/";

    public static final int PAGO_PARCIAL_ESP = 1;
    public static final int PAGO_PARCIAL_USA = 2;
    public static final int PAGO_TOTAL_PARCIAL_ESP = 3;
    public static final int PAGO_TOTAL_PARCIAL_USA = 4;
    public static final int PAGO_TOTAL_ESP = 5;
    public static final int PAGO_TOTAL_USA = 6;
    public static final int PAGO_ANTICIPO_ESP = 7;
    public static final int PAGO_ANTICIPO_USA = 8;
    public static final int PAGO_RECIBO_POR_LEGALIZAR_ESP = 9;
    public static final int PAGO_RECIBO_POR_LEGALIZAR_USA = 10;


    public  static int INTENT_GPS=9000;
}
