package cabiso.daphny.com.g_companion.Maps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/20/2017.
 */

class SearchApartmentsMaps extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Double lati;
    Double longi;
    private GoogleMap mMap;
    public List<LatLng> mlatLongDatas;
    private List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_apartments_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String address = b.getString("Address");
        String location = b.getString("Location");
        Geocoder geocoder = new Geocoder(this);
        List<Address> mAddress;

        try {
            mAddress = geocoder.getFromLocationName(location,1);
            Address add = mAddress.get(0);
            LatLng latLng = new LatLng(add.getLatitude(), add.getLongitude());

            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20 ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (mLastLocation != null) {
            lati = mLastLocation.getLatitude();
            longi = mLastLocation.getLongitude();

            LatLng latLng = new LatLng(lati, longi);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here"));
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(500)
                    .strokeWidth(0f)
                    .fillColor(0x550000FF));

            mlatLongDatas = new ArrayList<>();
            mlatLongDatas.add(new LatLng(10.309063, 123.893360));
            mlatLongDatas.add(new LatLng(10.309163, 123.892360));
            mlatLongDatas.add(new LatLng(10.316268, 123.890878));
            Addmarkers(latLng, mlatLongDatas);
        } else
            Toast.makeText(this, "Unable to connect to the service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannont connect to the network", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void Addmarkers(LatLng latLng, List<LatLng> positions) {

        for (LatLng position : positions) {
            Marker marker = mMap.addMarker(
                    new MarkerOptions()
                            .position(position)
                            .snippet("String")
                            .visible(false)); // Invisible for now
            markers.add(marker);
            marker.showInfoWindow();
        }

      /*  for (Marker marker : markers) {
            if (SphericalUtil.computeDistanceBetween(latLng, marker.getPosition()) < 400) {
                marker.setVisible(true);
            }
        }*/
    }

}
