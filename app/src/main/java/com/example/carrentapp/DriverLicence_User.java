package com.example.carrentapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DriverLicence_User extends AppCompatActivity implements PaymentResultListener {

    private static final int PICK_IMAGES = 21;
    ArrayList<String> ImageList = new ArrayList<>();
    ArrayList<String> urlStrings;
    RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("LicenceDetails");
    ProgressDialog progressDialog;
    TextInputLayout DriverName, LicenceNo;
    String DatabaseReferenceKey, FromDate, FromTime, ToTime, ToDate;
    String from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_licence__user);
        Checkout.preload(getApplicationContext());
        DriverName = findViewById(R.id.DriverName);
        LicenceNo = findViewById(R.id.LicenceNo);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        findViewById(R.id.SelectImage).setOnClickListener(view -> selectImages());
        findViewById(R.id.Submit1).setOnClickListener(view -> SaveLicenceData());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            DatabaseReferenceKey = bundle.getString("DatabaseReferenceKey");
            FromDate = bundle.getString("FromDate");
            ToDate = bundle.getString("ToDate");
            FromTime = bundle.getString("FromTime");
            ToTime = bundle.getString("ToTime");
            from = getIntent().getExtras().getString("from");
            to = getIntent().getExtras().getString("to");
        }

    }

    private void SaveLicenceData() {
        progressDialog = new ProgressDialog(DriverLicence_User.this);
        progressDialog.setMessage("Uploading Images & Data please Wait.........!!!!!!");
        urlStrings = new ArrayList<>();
        if (DriverName.getEditText().getText().toString().trim().equals("")
                || LicenceNo.getEditText().getText().toString().trim().equals("")) {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ImageList.size() == 0) {
            Toast.makeText(this, "Images not selected", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        save();

    }

    private void selectImages() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES);

    }

    private void save() {


        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("LicenceImageFolder");

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

                saveToFirebase(urlStrings);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFirebase(ArrayList<String> urlStrings) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < urlStrings.size(); i++) {
            hashMap.put("ImgLink" + i, urlStrings.get(i));
        }
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String User = firebaseUser.getUid(),
                driverName = DriverName.getEditText().getText().toString().trim(),
                licenceNo = LicenceNo.getEditText().getText().toString().trim();

        DrivingLicenceModal modal = new DrivingLicenceModal(User, driverName, licenceNo, hashMap);

        databaseReference.child(User).setValue(modal).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(DriverLicence_User.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                clearFields();
                payment();
            }
        }).addOnFailureListener(e -> Toast.makeText(DriverLicence_User.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

        progressDialog.dismiss();

        ImageList.clear();
    }

    private void payment() {

        final String[] rate = new String[1];
        if (DatabaseReferenceKey != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("CarDetails");
            databaseReference.child(DatabaseReferenceKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CarDetailModal modal = snapshot.getValue(CarDetailModal.class);
                    if (modal != null) {
                        rate[0] = modal.CarRate;
                        proceed(rate[0]);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void proceed(String rate) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_z0AsoFOJSWsIoK");

        checkout.setImage(R.drawable.oip__2_);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Merchant Name");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(Integer.parseInt(rate) * 100));//500*100
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact", "8605564295");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }


    private void clearFields() {
        LinearLayout linearLayout = findViewById(R.id.licencelayout);
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
    }

    @Override
    public void onPaymentSuccess(String s) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("BookedCar");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CarBookedModal modal = new CarBookedModal(firebaseUser.getUid(), DatabaseReferenceKey, FromDate, ToDate, FromTime, ToTime, from, to);
        databaseReference.push().setValue(modal).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(DriverLicence_User.this, "Car has been Booked", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(DriverLicence_User.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("TAG", "onPaymentError: " + s);
    }
}