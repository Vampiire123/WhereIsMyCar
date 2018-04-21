package com.example.syl.whereismycar.usecase;

import android.location.Location;

import com.example.syl.whereismycar.global.model.MLocation;

public interface DataLocations {

    void getLocation(Listener listener);
    void saveLocation(Location location, Listener listener);

    interface Listener {
        void onSuccess(MLocation location);
        void onError(String msg);
    }
}