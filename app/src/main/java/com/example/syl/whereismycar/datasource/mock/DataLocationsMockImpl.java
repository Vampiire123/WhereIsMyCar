package com.example.syl.whereismycar.datasource.mock;

import android.location.Location;

import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.global.model.MLocation_Table;
import com.example.syl.whereismycar.usecase.DataLocations;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class DataLocationsMockImpl implements DataLocations {

    static final int LIMIT = 5;

    @Override
    public void getLocation(Listener listener) {
        MLocation lastLocation = SQLite.select().from(MLocation.class)
                .orderBy(MLocation_Table.id, false)
                .querySingle();

        if(lastLocation != null) {
            listener.onSuccess(lastLocation);
        } else {
            listener.onError("You don't have last location");
        }
    }

    @Override
    public void saveLocation(Location location, Listener listener) {
        checkSizeMLocationTable();

        MLocation mLocation = new MLocation();
        mLocation.setLatitude(location.getLatitude());
        mLocation.setLongitude(location.getLongitude());

        if(mLocation.save()){
            listener.onSuccess(mLocation);
        } else {
            listener.onError("Error saving location");
        }
    }

    private void checkSizeMLocationTable() {
        List<MLocation> mLocations = SQLite.select().from(MLocation.class).queryList();
        if (mLocations.size() >= DataLocationsMockImpl.LIMIT) {
            SQLite.delete().from(MLocation.class).where(MLocation_Table.id.eq(mLocations.get(0).getId())).execute();
        }
    }
}