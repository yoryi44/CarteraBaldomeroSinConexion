package dataobject;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Regional {

    public String codigo;
    public String nombre;

    public List<Vendedor> listaVendedores = new ArrayList<>();

    // LISTA VENDEDORES A MOSTRAR
    public List<String> listaVendedoresSpinner = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
