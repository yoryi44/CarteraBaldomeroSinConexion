package servicio;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    private static Retrofit retrofitNoCache = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            String urlBase = "https://maps.googleapis.com/maps/api/directions/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(urlBase)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
