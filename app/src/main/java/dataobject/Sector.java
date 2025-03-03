package dataobject;

/**
 * Created by john-f on 16/11/2016.
 */

import java.io.Serializable;

public class Sector implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id;
    public String desc;
    public String medida;

    public float factor;

    public String principal;

    public String codid;
}