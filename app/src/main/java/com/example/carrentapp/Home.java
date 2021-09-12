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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Home extends AppCompatActivity {
    CarViewAdapter adapter;
    ProgressDialog pd;
    RecyclerView recyclerView;
    String from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Home");
        toolbar.findViewById(R.id.logout).setOnClickListener(view -> Logout());
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait");

        recyclerView = findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getString("from");
            to = bundle.getString("to");
            fetch();
        }


//        ArrayList<CarDetailModal> carDetailModalArrayList = (ArrayList<CarDetailModal>) getIntent().getSerializableExtra("data");
//        ArrayList<String> DbRef = (ArrayList<String>) getIntent().getSerializableExtra("Ref");
//        if (carDetailModalArrayList != null && carDetailModalArrayList.size() > 0) {
//            adapter = new CarDetailsForUserAdapter(this, carDetailModalArrayList, DbRef);
//            recyclerView.setAdapter(adapter);
//        }
    }

    private void Logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, SignIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
        adapter = new CarViewAdapter(options, this, pd, from, to);
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }


}