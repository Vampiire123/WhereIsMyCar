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
import android.content.pm.PackageManager;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.datasource.db.DataLocationsDBImpl;
import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.usecase.CheckPermission;
import com.example.syl.whereismycar.usecase.DataLocations;

public class MainPresenter extends Presenter<MainPresenter.View, MainPresenter.Navigator> {

    Context context;
    DataLocations dataLocations;
    CheckPermission checkPermission;

    MLocation actualLocation = new MLocation();

    public MainPresenter(Context context, DataLocationsDBImpl getLocationDBImpl, CheckPermission checkPermission) {
        this.context = context;
        this.dataLocations = getLocationDBImpl;
        this.checkPermission = checkPermission;
    }

    @Override
    public void initialize() {
        boolean locationGranted = checkPermission.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationGranted) {
            dataLocations.getCurrentLocation(new DataLocations.Listener() {
                @Override
                public void onSuccess(MLocation location) {
                    actualLocation = location;
                    view.showActualLocation(location);
                }

                @Override
                public void onError() {
                    view.showMessage(context.getString(R.string.error_getting_location));
                }
            });
        } else {
            view.showPermissionRequest();
        }

        view.showMessage(context.getString(R.string.welcome_back));
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
        dataLocations.saveLocationToDB(this.actualLocation, new DataLocations.Listener() {
            @Override
            public void onSuccess(MLocation location) {
                view.showMessage(context.getString(R.string.saved_location) + " " + location.toString());
            }

            @Override
            public void onError() {
                view.showMessage(context.getString(R.string.error_saving_location));
            }
        });
    }

    public void onLoadLastLocationButtonClicked() {
        dataLocations.getLocationFromDB(new DataLocations.Listener() {
            @Override
            public void onSuccess(MLocation location) {
                view.showMessage(location.toString());
                navigator.navigateToMap(location);
            }

            @Override
            public void onError() {
                view.showMessage(context.getString(R.string.error_loading_location));
            }
        });
    }

    public void onDeleteLocationButtonClicked() {
        if (dataLocations.deleteLocationsFromDB()) {
            view.showMessage(context.getString(R.string.delete_completed));
        } else {
            view.showMessage(context.getString(R.string.delete_incompleted));
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dataLocations.getCurrentLocation(new DataLocations.Listener() {
                    @Override
                    public void onSuccess(MLocation location) {
                        view.showActualLocation(location);
                    }

                    @Override
                    public void onError() {
                        view.showMessage(context.getString(R.string.error_getting_location));
                    }
                });
            } else {
                System.exit(0);
            }
        }
    }

    public interface View {
        void showActualLocation(MLocation mLocation);
        void showMessage(String msg);
        void showPermissionRequest();
    }

    public interface Navigator {
        void navigateToMap(MLocation mLocation);
    }
}