package com.example.syl.whereismycar.ui.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.example.syl.whereismycar.datasource.mock.DataLocationsMockImpl;
import com.example.syl.whereismycar.datasource.mock.LocationData;
import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.usecase.DataLocations;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class MainPresenter extends Presenter<MainPresenter.view, MainPresenter.navigator> {

    Context context;
    DataLocations dataLocations;

    Location actualLocation;
    String calle;

    public MainPresenter(Context context, DataLocationsMockImpl getLocationMock) {
        this.context = context;
        this.dataLocations = getLocationMock;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public void initialize() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            view.showPermissionRequest();
        } else {
            locationStart();
        }

        view.showToastMessage("Bienvenido/a de vuelta!");
        FlowManager.init(new FlowConfig.Builder(context).build());
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    public void onSaveLocationButtonClicked() {
        dataLocations.saveLocation(this.actualLocation, new DataLocations.Listener() {
            @Override
            public void onSuccess(MLocation location) {
                view.showToastMessage("Saved location in " + location.toString());
            }

            @Override
            public void onError(String msg) {
                view.showToastMessage(msg);
            }
        });
    }

    public void onLoadLastLocationButtonClicked() {
        dataLocations.getLocation(new DataLocations.Listener() {
            @Override
            public void onSuccess(MLocation location) {
                view.showToastMessage(location.toString());
                navigator.navigateToMap(location);
            }

            @Override
            public void onError(String msg) {
                view.showToastMessage(msg);
            }
        });
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationData locationData = new LocationData();
        locationData.setMainPresenter(this);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            view.showPermissionRequest();
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) locationData);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) locationData);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
            } else {
                System.exit(0);
            }
        }
    }

    public void setLocation(Location location, String calle) {
        this.actualLocation = location;
        this.calle = calle;

        view.showActualLocation(location, calle);
    }

    public interface view {
        void showActualLocation(Location location, String calle);

        void showToastMessage(String msg);

        void showPermissionRequest();
    }

    public interface navigator {
        void navigateToMap(MLocation mLocation);
    }
}