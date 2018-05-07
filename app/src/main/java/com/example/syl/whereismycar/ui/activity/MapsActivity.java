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
package com.example.syl.whereismycar.ui.activity;

import android.os.Bundle;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.ui.presenter.MapsPresenter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends BaseActivity
        implements OnMapReadyCallback, MapsPresenter.View, MapsPresenter.Navigator {

    MapsPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        presenter = new MapsPresenter(this);
        presenter.setView(this);
        presenter.setNavigator(this);

        presenter.initialize();
        presenter.onExtrasReceived(getIntent());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_maps;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        presenter.onMapReady(googleMap);
    }
}