package com.example.syl.whereismycar.ui.activity;

import android.os.Bundle;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.ui.presenter.MapsPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends BaseActivity
        implements OnMapReadyCallback, MapsPresenter.View, MapsPresenter.Navigator {

    private GoogleMap mMap;

    float zoomLevel = 16.5f;
    double longitude, latitude;

    MapsPresenter mapsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapsPresenter = new MapsPresenter(this);
        mapsPresenter.setView(this);
        mapsPresenter.setNavigator(this);
        mapsPresenter.initialize();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        longitude = (double) getIntent().getExtras().get("longitude");
        latitude = (double) getIntent().getExtras().get("latitude");
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_maps;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng positionCar = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(positionCar).title(getString(R.string.last_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionCar, zoomLevel));
    }
}