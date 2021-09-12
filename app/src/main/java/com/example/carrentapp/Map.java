package com.example.carrentapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Objects;


public class Map extends AppCompatActivity {
    private static final String TAG = "Home";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Object btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isServiceOk()) {
            init();
        }
    }

    private void init() {
        Button BtnMap = findViewById(R.id.btnMap);
        BtnMap.setOnClickListener(v -> {
                    Intent intent = new Intent(Map.this, MapsActivity.class);
                    startActivity(intent);
                }
        );

    }

    public boolean isServiceOk() {
        Log.d(TAG, "isServicesOk:checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Map.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOk: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Map.this, available, ERROR_DIALOG_REQUEST);
            Objects.requireNonNull(dialog).show();
        } else {
            Toast.makeText(this, "you cant make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}