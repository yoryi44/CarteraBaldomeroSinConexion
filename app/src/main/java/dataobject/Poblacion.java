package dataobject;

import java.util.ArrayList;
import java.util.List;

public class Poblacion {

    public String codigo;
    public String nombre;

    public List<Segmento> listaSegmentos = new ArrayList<>();

    // LISTA SEGMENTOS A MOSTRAR
    public List<String> listaSegmentosSpinner = new ArrayList<>();
}
