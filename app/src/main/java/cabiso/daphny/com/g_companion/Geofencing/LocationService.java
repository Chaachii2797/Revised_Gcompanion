package cabiso.daphny.com.g_companion.Geofencing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by cicctuser on 10/6/2018.
 */

public class LocationService implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private Context mContext;
    private FragmentActivity mActivity;
    private String[] locationPermits = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private GoogleApiClient googleApiClient;
    private Location mLocation;
    private Location mLastLocation;
    private LocationServiceCallback locationServiceCallback;

    public LocationService() {

    }

    public LocationService setContext(Context context) {
        mContext = context;
        return this;
    }

    public LocationService setActivity(FragmentActivity activity) {
        mActivity = activity;
        return this;
    }

    public LocationService start() {
        if (mContext != null && mActivity != null) {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(mContext)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            if (googleApiClient != null) {
                googleApiClient.connect();
            }
        }
        return this;
    }

    public Location getLocation(){
        return mLocation;
    }

    public LocationService onChange(LocationServiceCallback callback){
        locationServiceCallback = callback;
        return this;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, locationPermits, 101);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (mLastLocation != null) {
            locationServiceCallback.onChange(mLastLocation);
//            Log.e("LocationCheck","LAT: "+String.valueOf(mLastLocation.getLatitude()));
//            Log.e("LocationCheck","LNG: "+String.valueOf(mLastLocation.getLatitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(mContext, "Connection suspended, please check your internet connection and try again.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(mContext, "Connection Failed, please check your internet connection and try again.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        locationServiceCallback.onChange(location);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public interface LocationServiceCallback {
        void onChange(Location location);
    }

}
