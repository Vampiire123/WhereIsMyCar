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
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.datasource.db.DataLocationsDBImpl;
import com.example.syl.whereismycar.datasource.device.CheckPermissionImpl;
import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.ui.presenter.MainPresenter;

public class MainActivity extends BaseActivity implements MainPresenter.View, MainPresenter.Navigator {

    MainPresenter presenter;

    Button btnSaveLocation, btnLastLocation;
    TextView tvActualLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        presenter = new MainPresenter(this, new DataLocationsDBImpl(this), new CheckPermissionImpl(this));
        presenter.setView(this);
        presenter.setNavigator(this);
        presenter.initialize();

        tvActualLocation = findViewById(R.id.tv_actual_location);
        tvActualLocation.setText(getString(R.string.getting_location));
        btnLastLocation = findViewById(R.id.btn_last_location);
        btnSaveLocation = findViewById(R.id.btn_save_location);
        btnLastLocation.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                presenter.onLoadLastLocationButtonClicked();
            }
        });
        btnSaveLocation.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                presenter.onSaveLocationButtonClicked();
            }
        });
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showActualLocation(MLocation mLocation) {
        tvActualLocation.setText(getString(R.string.current_location) + "\n" + mLocation.getAddress() + "\n"
                + mLocation.getLatitude() + " " + mLocation.getLongitude());
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPermissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
    }

    @Override
    public void navigateToMap(MLocation location) {
        startActivity(new Intent(this, MapsActivity.class)
                .putExtra("latitude", location.getLatitude())
                .putExtra("longitude", location.getLongitude())
                .putExtra("address", location.getAddress()));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}