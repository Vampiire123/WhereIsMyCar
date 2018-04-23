package com.example.syl.whereismycar.ui.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.syl.whereismycar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    float zoomLevel = 16.5f;
    double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        longitude = (double) getIntent().getExtras().get("longitude");
        latitude = (double) getIntent().getExtras().get("latitude");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng somePosition = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(somePosition).title(getString(R.string.last_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(somePosition, zoomLevel));
    }
}