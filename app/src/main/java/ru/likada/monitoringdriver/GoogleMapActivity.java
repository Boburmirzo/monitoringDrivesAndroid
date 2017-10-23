package ru.likada.monitoringdriver;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by bumur on 19.05.2017.
 */

public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            coordinates = extras.getString("coordinates");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(googleMap!=null) {
            mMap = googleMap;
            String[] splitStr = coordinates.split("\\s+");
            final double langtitude = Double.parseDouble(splitStr[0]);
            final double longtitude = Double.parseDouble(splitStr[1]);
            // Add a marker in Sydney, Australia, and move the camera.
            LatLng sydney = new LatLng(langtitude, longtitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(150), 2000, null);
        }
    }

    public void onClickMap(View view) {
        finish();
    }
}
