package dataobject;

import androidx.annotation.NonNull;

public class Condicion {

    public String codigo;
    public String nombre;

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
