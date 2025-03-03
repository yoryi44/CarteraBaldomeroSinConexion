package dataobject;

import java.util.ArrayList;
import java.util.List;

public class Vendedor {

    public String codigo;
    public String nombre;

    public List<Poblacion> listaPoblaciones = new ArrayList<>();

    // LISTA POBLACION A MOSTRAR
    public List<String> listaPoblacionesSpinner = new ArrayList<>();
}
