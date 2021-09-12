package com.example.carrentapp;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.razorpay.Checkout;

import java.util.ArrayList;
import java.util.HashMap;

public class licence_admin extends AppCompatActivity  {

    private static final int PICK_IMAGES = 21;
    ArrayList<String> ImageList = new ArrayList<>();
    ArrayList<String> urlStrings;
    RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child( "verifyAdmin" );
    ProgressDialog progressDialog;
    TextInputLayout DriverName, LicenceNo, valid, carno;
    String DatabaseReferenceKey;
    private String admin;
    private String Valid;
    private String Carno;
    Button next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_licence_admin );
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("LicenceVerification");
        toolbar.findViewById(R.id.logout).setOnClickListener(view -> Logout());

        Checkout.preload( getApplicationContext() );
        DriverName = findViewById( R.id.DriverName );
        LicenceNo = findViewById( R.id.LicenceNo );
        valid = findViewById( R.id.valid );
        carno = findViewById( R.id.carno );
        next = findViewById( R.id.next );

        recyclerView = findViewById( R.id.recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false ) );
        recyclerView.setHasFixedSize( true );
        findViewById( R.id.SelectImage ).setOnClickListener( view -> selectImages() );
        findViewById( R.id.next ).setOnClickListener( view -> SaveLicenceData() );
        /*next.setOnClickListener( v -> opencar_detailsfor_admin1() );*/

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            DatabaseReferenceKey = bundle.getString( "DatabaseReferenceKey" );

        }

    }

    private void Logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, SignIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    private void SaveLicenceData() {
        progressDialog = new ProgressDialog( licence_admin.this );
        progressDialog.setMessage( "Uploading Images & Data please Wait.........!!!!!!" );
        urlStrings = new ArrayList<>();
        if (DriverName.getEditText().getText().toString().trim().equals( "" )
                || LicenceNo.getEditText().getText().toString().trim().equals( "" )
                 || valid.getEditText().getText().toString().trim().equals( "" )
                  || carno.getEditText().getText().toString().trim().equals( "" )) {
            Toast.makeText( this, "Fields can't be empty", Toast.LENGTH_SHORT ).show();
            return;
        }
        if (ImageList.size() == 0) {
            Toast.makeText( this, "Images not selected", Toast.LENGTH_SHORT ).show();
            return;
        }
        progressDialog.show();
        save();

    }


    private void selectImages() {

        Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
        intent.setType( "image/*" );
        intent.putExtra( Intent.EXTRA_ALLOW_MULTIPLE, true );
        startActivityForResult( Intent.createChooser( intent, "Select Picture" ), PICK_IMAGES );

    }

    private void save() {


        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child( "LicenceImageFolder" );

        int upload_count;
        for (upload_count = 0; upload_count < ImageList.size(); upload_count++) {

            Uri IndividualImage = Uri.parse( ImageList.get( upload_count ) );
            if (IndividualImage.toString().contains( "http" )) {
                urlStrings.add( ImageList.get( upload_count ) );
                finalSave();
            } else {
                final StorageReference ImageName = ImageFolder.child( "Images" + IndividualImage.getLastPathSegment() );
                ImageName.putFile( IndividualImage ).addOnSuccessListener( taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener( uri -> {
                    urlStrings.add( String.valueOf( uri ) );
                    finalSave();
                } ) ).addOnFailureListener( e -> Log.e( "Error", "getImageLinks: " + e.getMessage() ) );
            }
        }


    }

    private void finalSave() {
        if (urlStrings.size() == ImageList.size()) {
            try {

                saveToFirebase( urlStrings );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFirebase(ArrayList<String> urlStrings) throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < urlStrings.size(); i++) {
            hashMap.put( "ImgLink" + i, urlStrings.get( i ) );
        }
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String admin = firebaseUser.getUid(),
                driverName = DriverName.getEditText().getText().toString().trim(),
                licenceNo = LicenceNo.getEditText().getText().toString().trim();
                Valid = valid.getEditText().getText().toString().trim();
                Carno = carno.getEditText().getText().toString().trim();

        admin_drivinglicence_modal modal = new admin_drivinglicence_modal( admin, driverName, licenceNo,Valid,Carno, hashMap );


        databaseReference.push().child( admin ).setValue( modal ).addOnCompleteListener( task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText( licence_admin.this, "Successfully Uploaded", Toast.LENGTH_SHORT ).show();
                startActivity(new Intent(licence_admin.this, car_detailsfor_admin.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                clearFields();

            }
        } ).addOnFailureListener( e -> Toast.makeText( licence_admin.this, "" + e.getMessage(), Toast.LENGTH_SHORT ).show() );

        progressDialog.dismiss();

        ImageList.clear();
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

}

