package com.example.restapiwithokhttp;

import static com.example.restapiwithokhttp.model.service.RestApiWorker.SERVICE_EXCEPTION;
import static com.example.restapiwithokhttp.model.service.RestApiWorker.SERVICE_MESSAGE;
import static com.example.restapiwithokhttp.model.service.RestApiWorker.SERVICE_PAYLOAD;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.restapiwithokhttp.adapter.MyListAdapter;
import com.example.restapiwithokhttp.model.CityItem;
import com.example.restapiwithokhttp.model.RequestPackage;
import com.example.restapiwithokhttp.model.service.RestApiWorker;
import com.example.restapiwithokhttp.utils.ConnectivityCheckBroadcast;
import com.example.restapiwithokhttp.utils.Helper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ConnectivityCheckBroadcast.CheckConnectivity {

    private ProgressBar progressBar;
    ConnectivityCheckBroadcast connectivityCheckBroadcast = new ConnectivityCheckBroadcast(this);

    private static final String JSON_URL = "http://10.0.2.2/pakinfo/json/itemsfeed.php";
    public static final String SERVICE_REQUEST_PACKAGE = "serviceRequestPackage";
    private MyListAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<CityItem> cityItems;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(SERVICE_PAYLOAD)) {
                cityItems = intent.getParcelableArrayListExtra(SERVICE_PAYLOAD);
                progressBar.setVisibility(View.GONE);
                showRecycleList(cityItems);
            } else if (intent.hasExtra(SERVICE_EXCEPTION)) {
                String errorMessage = intent.getStringExtra(SERVICE_EXCEPTION);
                Toast.makeText(context, "" + errorMessage, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    };





    private void showRecycleList(ArrayList<CityItem> cityItems) {

        adapter = new MyListAdapter(this, cityItems);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        progressBar  = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        cityItems = new ArrayList<>();

    }


    public static OneTimeWorkRequest create() {

        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setEndPoint(JSON_URL);
        //requestPackage.setMethod("GET");

        requestPackage.setMethod("POST");
        requestPackage.setParams("province", "Punjab");

        String serializedRP = Helper.serializeToJson(requestPackage);

        Data inputData = new Data.Builder()
                .putString(SERVICE_REQUEST_PACKAGE, serializedRP)
                .build();
        return new OneTimeWorkRequest.Builder(RestApiWorker.class)
                .setInputData(inputData)
                .build();
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(SERVICE_MESSAGE));
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(connectivityCheckBroadcast,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        unregisterReceiver(connectivityCheckBroadcast);
        super.onStop();
    }

    boolean doReq = true;

    @Override
    public void onCheckConnection(Boolean isConnected) {
        if (isConnected){
            if (doReq){
                progressBar.setVisibility(View.VISIBLE);
                WorkManager.getInstance(getApplicationContext()).enqueue(create());
                doReq = false;
            }

        }else {
            Toast.makeText(this, "Network not available...", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

}
