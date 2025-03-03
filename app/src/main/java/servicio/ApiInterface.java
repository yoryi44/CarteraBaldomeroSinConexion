package servicio;

import dataobject.ListRutas;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("json")
    Call<ListRutas> getLogin(
            @Query(value="origin",encoded = true) String origin,
            @Query(value="destination",encoded = true) String destination,
            @Query(value="waypoints",encoded = true) String waypoints,
            @Query(value="departure_time",encoded = true) String departure_time,
            @Query(value="key",encoded = true) String key
    );
}
