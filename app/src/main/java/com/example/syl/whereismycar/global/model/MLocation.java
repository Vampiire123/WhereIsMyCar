package com.example.syl.whereismycar.global.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = MDatabase.class)
public class MLocation extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    int id;

    @Column
    double latitude;

    @Column
    double longitude;

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "MLocation{"
                + "id=" + id
                + ", latitude=" + latitude
                + ", longitude=" + longitude
                + '}';
    }
}