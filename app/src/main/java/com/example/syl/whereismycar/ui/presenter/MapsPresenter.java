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

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsPresenter extends Presenter<MapsPresenter.View, MapsPresenter.Navigator> {

    Context context;

    private GoogleMap mMap;

    float zoomLevel = 16.5f;
    double longitude, latitude;
    String address;

    public MapsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void initialize() {
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

    public void onExtrasReceived(Intent intent) {
        longitude = (double) intent.getExtras().get("longitude");
        latitude = (double) intent.getExtras().get("latitude");
        address = (String) intent.getExtras().get("address");
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng positionCar = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(positionCar).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionCar, zoomLevel));
    }

    public interface View {

    }

    public interface Navigator {

    }
}
