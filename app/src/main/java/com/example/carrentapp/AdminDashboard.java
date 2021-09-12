package com.example.carrentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AdminDashboard extends AppCompatActivity {
    CarViewAdapter adapter;
    ProgressDialog pd;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Dashboard");
        toolbar.findViewById(R.id.logout).setOnClickListener(view -> Logout());
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait");

        recyclerView = findViewById(R.id.list);
        findViewById(R.id.AddCar).setOnClickListener(view -> startActivity(new Intent(this, AdminCarDetail.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();


    }

    private void Logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, SignIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        pd.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        pd.dismiss();
    }


    private void fetch() {
        pd.show();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("CarDetails");
        FirebaseRecyclerOptions<CarDetailModal> options = new FirebaseRecyclerOptions.Builder<CarDetailModal>()
                .setQuery(query, CarDetailModal.class).build();
        adapter = new CarViewAdapter(options, this, pd);
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
}