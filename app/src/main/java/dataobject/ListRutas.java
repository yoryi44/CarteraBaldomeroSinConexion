package dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListRutas {
    @SerializedName("routes")
    @Expose
    private List<Router> routes = null;

    public List<Router> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Router> routes) {
        this.routes = routes;
    }
}
