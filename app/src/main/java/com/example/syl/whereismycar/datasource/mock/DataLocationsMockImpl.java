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

        if (lastLocation != null) {
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

        if (mLocation.save()) {
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