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