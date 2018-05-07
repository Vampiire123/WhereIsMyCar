/*
 * Copyright (C) 2018 Sylvia Domenech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.syl.whereismycar.ui.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.datasource.mock.DataLocationsDBImpl;
import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.usecase.DataLocations;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainPresenter extends Presenter<MainPresenter.View, MainPresenter.Navigator> implements LocationListener {

    Context context;
    DataLocations dataLocations;

    MLocation actualLocation;

    public MainPresenter(Context context, DataLocationsDBImpl getLocationMock) {
        this.context = context;
        this.dataLocations = getLocationMock;
    }

    @Override
    public void initialize() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            view.showPermissionRequest();
        } else {
            locationStart();
        }

        view.showToastMessage(context.getString(R.string.welcome_back));
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
                view.showToastMessage(context.getString(R.string.saved_location) + location.toString());
            }

            @Override
            public void onError() {
                view.showToastMessage(context.getString(R.string.error_saving_location));
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
            public void onError() {
                view.showToastMessage(context.getString(R.string.error_loading_location));
            }
        });
    }

    public void onDeleteLocationButtonClicked() {
        if (dataLocations.deleteLocations()) {
           view.showToastMessage(context.getString(R.string.delete_completed));
        } else {
            view.showToastMessage(context.getString(R.string.delete_incompleted));
        }
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            view.showPermissionRequest();
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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

    public void setLocation(Location location, String address) {
        actualLocation = new MLocation();
        actualLocation.setLatitude(location.getLatitude());
        actualLocation.setLongitude(location.getLongitude());
        actualLocation.setAddress(address);

        view.showActualLocation(location, address);
    }

    @Override
    public void onLocationChanged(Location loc) {
        String calle = "";
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    calle = DirCalle.getAddressLine(0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setLocation(loc, calle);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public interface View {
        void showActualLocation(Location location, String address);

        void showToastMessage(String msg);

        void showPermissionRequest();
    }

    public interface Navigator {
        void navigateToMap(MLocation mLocation);
    }
}