package com.example.syl.whereismycar.datasource;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.global.model.MLocation_Table;
import com.example.syl.whereismycar.ui.activity.MapsActivity;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WidgetBroadcast extends AppWidgetProvider implements LocationListener {

    private Context context;
    private LocationManager mlocManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        FlowManager.init(new FlowConfig.Builder(context).build());
        this.context = context;

        switch (intent.getAction()) {
            case "save_location":
                getLocation();
                break;
            case "load_location":
                loadLocation();
                break;
        }
    }

    private void loadLocation() {
        MLocation lastLocation = SQLite.select().from(MLocation.class)
                .orderBy(MLocation_Table.id, false)
                .querySingle();

        if (lastLocation != null) {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("latitude", lastLocation.getLatitude());
            intent.putExtra("longitude", lastLocation.getLongitude());
            intent.putExtra("address", lastLocation.getAddress());
            context.startActivity(intent);
        } else {
            Toast.makeText(context, context.getString(R.string.error_loading_location), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        Toast.makeText(context, context.getString(R.string.saving_location), Toast.LENGTH_SHORT).show();
        mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(settingsIntent);
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
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
        MLocation mLocation = new MLocation();
        mLocation.setLongitude(loc.getLongitude());
        mLocation.setLatitude(loc.getLatitude());
        mLocation.setAddress(calle);

        SQLite.delete().from(MLocation.class).executeUpdateDelete();
        mLocation.save();

        mlocManager.removeUpdates(this);

        Toast.makeText(context, context.getString(R.string.saved_location) + " " + mLocation.getAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // Empty method
    }

    @Override
    public void onProviderEnabled(String s) {
        // Empty method
    }

    @Override
    public void onProviderDisabled(String s) {
        // Empty method
    }
}