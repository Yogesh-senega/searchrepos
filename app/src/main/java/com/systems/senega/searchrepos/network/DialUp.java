package com.systems.senega.searchrepos.network;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import io.reactivex.Observable;

public class DialUp {

    private DialUp(){}

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static Observable<Boolean> listen(@NonNull Context context){
        final Context appContext = context.getApplicationContext();
        final IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        return Observable.create(new OnSubscribeBroadcastRegister(appContext, filter, null, null))
                .startWith(new Intent())
                .map(intent -> status(appContext))
                .distinctUntilChanged();
    }

    private static Boolean status(@NonNull Context appContext) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
