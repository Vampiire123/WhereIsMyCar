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

import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.global.model.MLocation_Table;
import com.example.syl.whereismycar.usecase.DataLocations;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class DataLocationsDBImpl implements DataLocations {

    static final int LIMIT = 5;

    @Override
    public void getLocation(Listener listener) {
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
    public void saveLocation(MLocation location, Listener listener) {
        checkSizeMLocationTable();

        if (location.save()) {
            listener.onSuccess(location);
        } else {
            listener.onError();
        }
    }

    @Override
    public boolean deleteLocations() {
        return SQLite.delete().from(MLocation.class).executeUpdateDelete() != 0;
    }

    private void checkSizeMLocationTable() {
        List<MLocation> mLocations = SQLite.select().from(MLocation.class).queryList();
        if (mLocations.size() >= DataLocationsDBImpl.LIMIT) {
            SQLite.delete().from(MLocation.class).where(MLocation_Table.id.eq(mLocations.get(0).getId())).execute();
        }
    }
}