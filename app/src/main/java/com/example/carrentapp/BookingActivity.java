package com.example.carrentapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class BookingActivity extends AppCompatActivity {


    public String[] cities;
    ArrayList<String> ImageList = new ArrayList<>();
    RecyclerView recyclerView;
    MaterialAutoCompleteTextView fuelTypeSelection;
    TextInputLayout CarBrand, ModelName, NoOfPassengers, CarAverage, NoOfAirbags, TotalLuggageBags, CarRate;
    RadioGroup ACGroup;
    String DatabaseReferenceKey = "";
    String from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        CarBrand = findViewById(R.id.CarName);
        ModelName = findViewById(R.id.modelName);
        NoOfPassengers = findViewById(R.id.passengers);
        CarAverage = findViewById(R.id.average);

        NoOfAirbags = findViewById(R.id.Airbags);
        TotalLuggageBags = findViewById(R.id.NoLuggage);
        CarRate = findViewById(R.id.CarRate);
        ACGroup = findViewById(R.id.acGroup);

        recyclerView = findViewById(R.id.recyclerView);
        cities = getResources().getStringArray(R.array.cities);

        fuelTypeSelection = findViewById(R.id.fuelTypeSelection);
        fuelTypeSelection.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Petrol", "Diesel"}));


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        CarDetailModal modal = (CarDetailModal) getIntent().getSerializableExtra("CarDataModal");
        if (modal != null) {
            from = getIntent().getExtras().getString("from");
            to = getIntent().getExtras().getString("to");
            setData(modal);
            disabledFields();
        }
        findViewById(R.id.Submit1).setOnClickListener(view -> next());

    }

    private void next() {
        if (!DatabaseReferenceKey.equals("")) {
            startActivity(new Intent(this, DateTime_Selection.class)
                    .putExtra("DatabaseReferenceKey", DatabaseReferenceKey)
                    .putExtra("from", from)
                    .putExtra("to", to)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

        }
    }

    private void setData(CarDetailModal modal) {
        CarBrand.getEditText().setText(modal.CarBrand);
        ModelName.getEditText().setText(modal.ModelName);
        NoOfPassengers.getEditText().setText(modal.NoOfPassengers);
        CarAverage.getEditText().setText(modal.CarAverage);
        fuelTypeSelection.setText(modal.FuelType);
        NoOfAirbags.getEditText().setText(modal.NoOfAirbags);
        TotalLuggageBags.getEditText().setText(modal.TotalLuggageBags);
        CarRate.getEditText().setText(modal.CarRate);
        ACGroup.check(ACGroup.getChildAt(modal.ACOptions.equals("AC") ? 0 : 1).getId());
        HashMap<String, String> images = modal.Images;
        ImageList.addAll(images.values());
        ImageSelectionAdapter adapter = new ImageSelectionAdapter(this, ImageList);
        recyclerView.setAdapter(adapter);
        DatabaseReferenceKey = getIntent().getStringExtra("DatabaseReferenceKey");


    }

    private void disabledFields() {
        LinearLayout linearLayout = findViewById(R.id.carDetailLayout);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            if (linearLayout.getChildAt(i) instanceof TextInputLayout) {
                TextInputLayout textInputLayout = (TextInputLayout) linearLayout.getChildAt(i);
                textInputLayout.getEditText().setEnabled(false);
                textInputLayout.getEditText().setTextColor(Color.BLACK);


            }
            if (linearLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(i);
                for (int j = 0; j < linearLayout1.getChildCount(); j++) {
                    if (linearLayout1.getChildAt(j) instanceof TextInputLayout) {
                        TextInputLayout textInputLayout = (TextInputLayout) linearLayout1.getChildAt(j);
                        textInputLayout.getEditText().setEnabled(false);
                        textInputLayout.getEditText().setTextColor(Color.BLACK);
                    }

                }
            }
        }
    }
}