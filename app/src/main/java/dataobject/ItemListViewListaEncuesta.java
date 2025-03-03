package dataobject;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ItemListViewListaEncuesta {

    public String titulo;
    public String subTitulo;
    public int icono;

    public String referencia;
    public int colorTitulo;
    public String respEditText = "";

    //Variables para exhibidores

    public int posSpinner    = 0;
    public int codExhibidor   = 0;
    public String cantidadExhib = "0";
    public boolean isFecha = false;

    public boolean isChecked = false;

    //Variables para Estudio de punto Meals
    public int tipo = 0;
    public int codPregunta;
    public int codRespuestaEspinner;

}