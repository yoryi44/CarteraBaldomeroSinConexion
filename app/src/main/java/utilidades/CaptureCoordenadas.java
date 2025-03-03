package utilidades;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

public class CaptureCoordenadas implements LocationListener {

    private LocationManager locationManager;
    private String longitud;
    private String latitud;
    private Context mContex;

    public CaptureCoordenadas(Context contex) {
        this.mContex = contex;
    }

    @SuppressLint("MissingPermission")
    public void captureCoordenadas(){
        locationManager=(LocationManager) mContex.getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String locationProvider = locationManager.getBestProvider(criteria, true);

        if(locationProvider.equalsIgnoreCase("gps")){
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 10, 0, CaptureCoordenadas.this);
        }else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, CaptureCoordenadas.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.longitud = String.valueOf(location.getLongitude());
        this.latitud = String.valueOf(location.getLatitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(mContex, "GPS ACTIVO", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(mContex, "POR FAVOR ACTIVE EL GPS", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {

        Location location=null;
        try {

            locationManager = (LocationManager) mContex.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (isGPSEnabled) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, this);

                if (locationManager != null) {

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Log.v("COORDENADAS", "---------------------------------" + latitude + " - " + longitude);

                    }

                } else
                    Toast.makeText(mContex, "Hubo un error al obtener las Coordenadas.", Toast.LENGTH_SHORT).show();
            }

            if(isNetworkEnabled && location==null){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location!=null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Log.v("COORDENADAS", "---------------------------------" + latitude + " - " + longitude);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

}
