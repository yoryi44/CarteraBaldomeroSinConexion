package dataobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Main {
    public static ClienteSincronizado cliente2;
    public static List<ClienteSincronizado> listaClientes= new ArrayList<>();
    public static boolean clientesOrdenados=false;
    public static String version;


    //Listas para los informes pdf
    public static List<Cliente> listaPreAcuerdosClient= new ArrayList<>();
    public static List<Cliente> listaAcuerdosClient= new ArrayList<>();
    public static List<AcuerdoPago> listaAcuerdos= new ArrayList<>();
    public static List<AcuerdoPago> listaPreAcuerdos= new ArrayList<>();
    public static List<Cartera> listaCobros= new ArrayList<>();
    public static List<Cartera> listaRecaudos= new ArrayList<>();
    public static List<Cartera> listaConciliaciones= new ArrayList<>();
    public static Vector<Cliente> listaClientesConciliaciones= new Vector<>();


    public static String[] clientes= new String[]{};

    public static HashMap<String, Cliente> nuevosClientesSeleccionados = new HashMap<>();;

    public static ArrayList<String> ordenClientes=new ArrayList<>();

}
