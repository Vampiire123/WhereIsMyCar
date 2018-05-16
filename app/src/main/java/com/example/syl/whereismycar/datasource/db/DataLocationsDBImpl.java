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
package com.example.syl.whereismycar.datasource.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.global.model.MLocation_Table;
import com.example.syl.whereismycar.usecase.DataLocations;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DataLocationsDBImpl implements DataLocations, LocationListener {

    Context context;
    Listener listener;

    static final int LIMIT = 5;

    public DataLocationsDBImpl(Context context) {
        this.context = context;
        initDB();
    }

    private void initDB() {
        FlowManager.init(new FlowConfig.Builder(context).build());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getCurrentLocation(Listener listener) {
        this.listener = listener;

        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(settingsIntent);
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void getLocationFromDB(Listener listener) {
        MLocation lastLocation = SQLite.select().from(MLocation.class)
                .orderBy(MLocation_Table.id, false)
                .querySingle();

        if (lastLocation != null) {
            listener.onSuccess(lastLocation);
        } else {
            listener.onError();
        }
    }

    @Override
    public void saveLocationToDB(MLocation location, Listener listener) {
        checkSizeMLocationTable();

        if (location.save()) {
            listener.onSuccess(location);
        } else {
            listener.onError();
        }
    }

    @Override
    public void deleteLocationsFromDB(Listener listener) {
        if (SQLite.delete().from(MLocation.class).executeUpdateDelete() != 0) {
            listener.onSuccess(null);
        } else {
            listener.onError();
        }
    }

    private void checkSizeMLocationTable() {
        List<MLocation> mLocations = SQLite.select().from(MLocation.class).queryList();
        if (mLocations.size() >= DataLocationsDBImpl.LIMIT) {
            SQLite.delete().from(MLocation.class).where(MLocation_Table.id.eq(mLocations.get(0).getId())).execute();
        }
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
                listener.onError();
            }
        }
        MLocation mLocation = new MLocation();
        mLocation.setLongitude(loc.getLongitude());
        mLocation.setLatitude(loc.getLatitude());
        mLocation.setAddress(calle);
        listener.onSuccess(mLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        /*Empty method*/
    }

    @Override
    public void onProviderEnabled(String provider) {
        /*Empty method*/
    }

    @Override
    public void onProviderDisabled(String provider) {
        /*Empty method*/
    }
}