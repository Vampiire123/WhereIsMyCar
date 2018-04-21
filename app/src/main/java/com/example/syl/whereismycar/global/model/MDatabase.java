package com.example.syl.whereismycar.global.model;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = MDatabase.NAME, version = MDatabase.VERSION)
public class MDatabase {
    public static final String NAME = "MyDatabase";

    public static final int VERSION = 1;
}