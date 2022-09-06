package com.example.restapiwithokhttp.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class ConnectivityCheckBroadcast extends BroadcastReceiver {
    private CheckConnectivity checkConnectivity;

    public ConnectivityCheckBroadcast(CheckConnectivity checkConnectivity) {
        this.checkConnectivity = checkConnectivity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            boolean booleanExtra = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (!booleanExtra){
                checkConnectivity.onCheckConnection(true);
            }else {
                checkConnectivity.onCheckConnection(false);
            }
        }
    }


    public interface CheckConnectivity{
        void onCheckConnection(Boolean isConnected);
    }
}