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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.datasource.mock.DataLocationsMockImpl;
import com.example.syl.whereismycar.datasource.mock.LocationData;
import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.ui.presenter.MainPresenter;

public class MainActivity extends BaseActivity implements MainPresenter.view, MainPresenter.navigator {

    MainPresenter presenter;

    DataLocationsMockImpl getLocationMock;

    Button btn_save_location, btn_last_location;
    TextView tv_actual_location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLocationMock = new DataLocationsMockImpl();

        presenter = new MainPresenter(this, getLocationMock);
        presenter.setView(this);
        presenter.setNavigator(this);
        presenter.initialize();

        tv_actual_location = findViewById(R.id.tv_actual_location);
        tv_actual_location.setText(getString(R.string.getting_location));
        btn_last_location = findViewById(R.id.btn_last_location);
        btn_save_location = findViewById(R.id.btn_save_location);
        btn_last_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLoadLastLocationButtonClicked();
            }
        });
        btn_save_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSaveLocationButtonClicked();
            }
        });
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void showActualLocation(Location location, String address) {
        tv_actual_location.setText(getString(R.string.current_location) + "\n"
                + location.getLatitude() + " " + location.getLongitude() + "\n"
                + address);
    }

    @Override
    public void showToastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPermissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
    }

    @Override
    public void navigateToMap(MLocation location) {
        startActivity(new Intent(this, MapsActivity.class)
                .putExtra("latitude", location.getLatitude())
                .putExtra("longitude", location.getLongitude()));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}