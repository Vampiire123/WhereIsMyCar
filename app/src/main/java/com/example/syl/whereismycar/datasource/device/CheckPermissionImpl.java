package com.example.syl.whereismycar.datasource.device;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.example.syl.whereismycar.usecase.CheckPermission;

public class CheckPermissionImpl implements CheckPermission {

    Context context;

    public CheckPermissionImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
