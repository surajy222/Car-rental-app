package com.example.carrentapp;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminCarDetail extends AppCompatActivity {
    private static final int PICK_IMAGES = 21;
    private static final String TAG = "AdminCarDetail";
    public String[] cities;
    ArrayList<String> ImageList = new ArrayList<>();
    ArrayList<String> urlStrings;
    RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("CarDetails");

    MaterialAutoCompleteTextView fuelTypeSelection;
    TextInputLayout CarBrand, ModelName, NoOfPassengers, CarAverage, NoOfAirbags, TotalLuggageBags, CarRate;
    RadioGroup ACGroup;
    String DatabaseReferenceKey = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_car_detail);
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
        findViewById(R.id.SelectImage).setOnClickListener(view -> selectImages());
        findViewById(R.id.Submit1).setOnClickListener(view -> saveCarDetail());

        CarDetailModal modal = (CarDetailModal) getIntent().getSerializableExtra("CarDataModal");
        if (modal != null)
            setData(modal);


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
        ((MaterialButton) findViewById(R.id.Submit1)).setText("Update");
        DatabaseReferenceKey = getIntent().getStringExtra("DatabaseReferenceKey");


    }

    private void selectImages() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES);

    }

    private void clearFields() {
        LinearLayout linearLayout = findViewById(R.id.carDetailLayout);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            if (linearLayout.getChildAt(i) instanceof TextInputLayout) {
                TextInputLayout textInputLayout = (TextInputLayout) linearLayout.getChildAt(i);
                textInputLayout.getEditText().setText(null);
                textInputLayout.getEditText().clearFocus();

            }
            if (linearLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(i);
                for (int j = 0; j < linearLayout1.getChildCount(); j++) {
                    if (linearLayout1.getChildAt(j) instanceof TextInputLayout) {
                        TextInputLayout textInputLayout = (TextInputLayout) linearLayout1.getChildAt(j);
                        textInputLayout.getEditText().setText(null);
                    }
                    if (linearLayout1.getChildAt(j) instanceof RecyclerView) {
                        recyclerView.setAdapter(null);
                    }
                }
            }
        }
    }

    private JSONObject getDataFromFields() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CarBrand", CarBrand.getEditText().getText().toString());
        jsonObject.put("ModelName", ModelName.getEditText().getText().toString());
        jsonObject.put("NoOfPassengers", NoOfPassengers.getEditText().getText().toString());
        jsonObject.put("CarAverage", CarAverage.getEditText().getText().toString());
        jsonObject.put("FuelType", fuelTypeSelection.getText().toString());
        jsonObject.put("NoOfAirbags", NoOfAirbags.getEditText().getText().toString());
        jsonObject.put("TotalLuggageBags", TotalLuggageBags.getEditText().getText().toString());
        jsonObject.put("CarRate", CarRate.getEditText().getText().toString());
        jsonObject.put("ACAvail", ((RadioButton) findViewById((ACGroup).getCheckedRadioButtonId())).getText());

        return jsonObject;

    }

    private void saveCarDetail() {
        progressDialog = new ProgressDialog(AdminCarDetail.this);
        progressDialog.setMessage("Uploading Images & Data please Wait.........!!!!!!");
        urlStrings = new ArrayList<>();
        progressDialog.show();
        save();


    }


    private void save() {

        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder");

        int upload_count;
        for (upload_count = 0; upload_count < ImageList.size(); upload_count++) {

            Uri IndividualImage = Uri.parse(ImageList.get(upload_count));
            if (IndividualImage.toString().contains("http")) {
                urlStrings.add(ImageList.get(upload_count));
                finalSave();
            } else {
                final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());
                ImageName.putFile(IndividualImage).addOnSuccessListener(taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener(uri -> {
                    urlStrings.add(String.valueOf(uri));
                    finalSave();
                })).addOnFailureListener(e -> Log.e("Error", "getImageLinks: " + e.getMessage()));
            }
        }


    }

    private void finalSave() {
        if (urlStrings.size() == ImageList.size()) {
            try {
                if (DatabaseReferenceKey.equals(""))
                    saveToFirebase(urlStrings);
                else
                    updateToFirebase(DatabaseReferenceKey, urlStrings);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateToFirebase(String databaseReferenceKey, ArrayList<String> urlStrings) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < urlStrings.size(); i++) {
            hashMap.put("ImgLink" + i, urlStrings.get(i));
        }

        JSONObject field = getDataFromFields();
        String CarBrand = field.getString("CarBrand"),
                ModelName = field.getString("ModelName"),
                NoOfPassengers = field.getString("NoOfPassengers"),
                CarAverage = field.getString("CarAverage"),
                FuelType = field.getString("FuelType"),
                NoOfAirbags = field.getString("NoOfAirbags"),
                TotalLuggageBags = field.getString("TotalLuggageBags"),
                CarRate = field.getString("CarRate"),
                ACAvail = field.getString("ACAvail");


        CarDetailModal modal = new CarDetailModal(CarBrand, ModelName, NoOfPassengers, CarAverage, FuelType, NoOfAirbags, TotalLuggageBags, CarRate, ACAvail, hashMap);


        databaseReference.child(databaseReferenceKey).setValue(modal).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(AdminCarDetail.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        }).addOnFailureListener(e -> Toast.makeText(AdminCarDetail.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

        progressDialog.dismiss();

        ImageList.clear();
        finish();

    }

    private void saveToFirebase(ArrayList<String> urlStrings) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < urlStrings.size(); i++) {
            hashMap.put("ImgLink" + i, urlStrings.get(i));
        }

        JSONObject field = getDataFromFields();
        String CarBrand = field.getString("CarBrand"),
                ModelName = field.getString("ModelName"),
                NoOfPassengers = field.getString("NoOfPassengers"),
                CarAverage = field.getString("CarAverage"),
                FuelType = field.getString("FuelType"),
                NoOfAirbags = field.getString("NoOfAirbags"),
                TotalLuggageBags = field.getString("TotalLuggageBags"),
                CarRate = field.getString("CarRate"),
                ACAvail = field.getString("ACAvail");


        CarDetailModal modal = new CarDetailModal(CarBrand, ModelName, NoOfPassengers, CarAverage, FuelType, NoOfAirbags, TotalLuggageBags, CarRate, ACAvail, hashMap);


        databaseReference.push().setValue(modal).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(AdminCarDetail.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        }).addOnFailureListener(e -> Toast.makeText(AdminCarDetail.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

        progressDialog.dismiss();

        ImageList.clear();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES && resultCode == RESULT_OK) {

            Uri imageUri;
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    imageUri = item.getUri();
                    ImageList.add(imageUri.toString());

                }


            } else if (data.getData() != null) {
                imageUri = data.getData();
                ImageList.add(imageUri.toString());
            } else {
                Toast.makeText(this, "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
            }
            if (ImageList.size() > 0) {
                ImageSelectionAdapter adapter = new ImageSelectionAdapter(this, ImageList);

                recyclerView.setAdapter(adapter);

            }

        }


//        if (resultCode == RESULT_OK) {
//            if (requestCode == PICK_IMAGES) {
//                if (data.getClipData() != null) {
//                    ClipData mClipData = data.getClipData();
//                    for (int i = 0; i < mClipData.getItemCount(); i++) {
//                        ClipData.Item item = mClipData.getItemAt(i);
//                        Uri uri = item.getUri();
//                        // display your images
//                        ((ImageView) findViewById(R.id.imageView1)).setImageURI(uri);
//                    }
//                } else if (data.getData() != null) {
//                    Uri uri = data.getData();
//                    // display your image
//                    ((ImageView) findViewById(R.id.imageView1)).setImageURI(uri);
//                }
//            }
//        }

////      if(requestCode==reCode && requestCode == RESULT_OK && data !=null)
//        //      {
//        Uri path = data.getData();
//        try {
//
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
//            Toast.makeText(getApplicationContext(), "BitmapValue" + bitmap, Toast.LENGTH_SHORT).show();
//            imageView.setImageBitmap(bitmap);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //    }else {
//        Toast.makeText(getApplicationContext(), "BitmapValue" + data, Toast.LENGTH_SHORT).show();
//        //  }
    }


}