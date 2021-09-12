package com.example.carrentapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    MaterialAutoCompleteTextView from, to;
    FloatingActionButton locate;
    MaterialButton search;
    GoogleApiClient mGoogleApiClient;
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;

    /**
     * Prompt user to enable GPS and Location Services
     *
     * @param mGoogleApiClient
     * @param activity
     */
    public static void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            final LocationSettingsStates state = result1.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                activity, 1000);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationPermissionGranted && LocationManagerCompat.isLocationEnabled((LocationManager) getSystemService(Context.LOCATION_SERVICE))) {
            //EnableGPS();

            try {
                getDeviceLocation();
            } catch (Exception e) {
                Toast.makeText(this, "Wait for some time and try again to relocate", Toast.LENGTH_SHORT).show();
            }
        }

        Log.d(TAG, "onMapReady:map is ready");
//
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //init();


    }

    public boolean isServiceOk() {
        Log.d(TAG, "isServicesOk:checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOk: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            Objects.requireNonNull(dialog).show();
        } else {
            Toast.makeText(this, "you cant make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Search");
        toolbar.findViewById(R.id.logout).setOnClickListener(view -> Logout());
        if (isServiceOk()) {
            init();
        }

    }
    private void Logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, SignIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 34992, connectionResult -> {

                })
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {

                })
                .build();
        if (!LocationManagerCompat.isLocationEnabled((LocationManager) getSystemService(Context.LOCATION_SERVICE))) {
            locationChecker(mGoogleApiClient, MapsActivity.this);
        }


        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        locate = findViewById(R.id.locate);
        search = findViewById(R.id.searchCar);
        from.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.cities)));
        to.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.cities)));
        locate.setOnClickListener(view -> {
            if (!LocationManagerCompat.isLocationEnabled((LocationManager) getSystemService(Context.LOCATION_SERVICE))) {
                locationChecker(mGoogleApiClient, MapsActivity.this);
                return;
            }
            try {
                getDeviceLocation();
            } catch (Exception e) {
                Toast.makeText(this, "Wait for some time and try again to relocate", Toast.LENGTH_SHORT).show();
            }

        });
        search.setOnClickListener(view -> SearchCar());

        getLocationPermission();

    }


    private void SearchCar() {
        ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage("Please Wait");

        if (from.getText().toString().length() == 0) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (to.getText().toString().length() == 0) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        pd.show();
        String fromLocation = from.getText().toString(), ToLocation = to.getText().toString();

        startActivity(new Intent(MapsActivity.this, Home.class)
                .putExtra("from", fromLocation)
                .putExtra("to", ToLocation)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

//        ArrayList<CarDetailModal> carDetailModalArrayList = new ArrayList<>();
//        ArrayList<String> DbRef = new ArrayList<>();
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("CarDetails");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    CarDetailModal modal = dataSnapshot.getValue(CarDetailModal.class);
//                    if (modal != null) {
//                        String stopLocations = modal.StopLocations.trim();
//                        if (stopLocations.charAt(stopLocations.length() - 1) == ',') {
//                            stopLocations = stopLocations.substring(0, stopLocations.length() - 1);
//                        }
//                        List<String> locations = Arrays.asList(stopLocations.split(", "));
//                        if (locations.indexOf(Tolocation) > locations.indexOf(fromlocation)) {
//                            carDetailModalArrayList.add(modal);
//                            DbRef.add(dataSnapshot.getKey());
//                        }
//                    }
//                }
//                if (carDetailModalArrayList.size() > 0) {
//                    pd.dismiss();
//                    startActivity(new Intent(MapsActivity.this, Home.class)
//                            .putExtra("data", carDetailModalArrayList)
//                            .putExtra("Ref", DbRef)
//                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                } else {
//                    Toast.makeText(MapsActivity.this, "No Cars Found Between " + fromlocation + " & " + Tolocation, Toast.LENGTH_SHORT).show();
//                    pd.dismiss();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "onCancelled: " + error.getMessage());
//            }
//        });


    }

    //
//
//    private void init() {
//        Log.d(TAG, "init:initializing");
//        mSearchText.setOnEditorActionListener((v, actionId, keyEvent) -> {
//
//            if (actionId == EditorInfo.IME_ACTION_SEARCH
//                    || actionId == EditorInfo.IME_ACTION_DONE
//                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
//                geoLocate();
//            }
//            return false;
//        });
//        mGps.setOnClickListener(v -> {
//            Log.d(TAG, "onClick: clicked gps icon");
//            getDeviceLocation();
//        });
//        hideSoftKeyboard();
//    }

    //
//    private void geoLocate() {
//        Log.d(TAG, "geoLocate: geolocating");
//        String searchString = mSearchText.getText().toString();
//        Geocoder geocoder = new Geocoder(MapsActivity.this);
//        List<Address> list = new ArrayList<>();
//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        } catch (IOException e) {
//            Log.e(TAG, "geoLocate: IOException:" + e.getMessage());
//        }
//        if (list.size() > 0) {
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location: " + address.toString());
//            //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                    address.getAddressLine(0));
//        }
//    }
//

    //
//    private void geoLocatee() {
//        Log.d(TAG, "geoLocate: geolocating");
//        String searchString = B.getText().toString();
//        Geocoder geocoder = new Geocoder(MapsActivity.this);
//        List<Address> list = new ArrayList<>();
//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        } catch (IOException e) {
//            Log.e(TAG, "geoLocate: IOException:" + e.getMessage());
//        }
//        if (list.size() > 0) {
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location: " + address.toString());
//            //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                    address.getAddressLine(0));
//        }
//    }

    //
    private void openbook_activity() {
        Intent intent = new Intent(MapsActivity.this, BookingActivity.class);
        startActivity(intent);
    }

    //
    private void getDeviceLocation() throws Exception {

        Log.d(TAG, "getDeviceLocation:getting the device current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "onComplete:found location!");
                Location currentLocation = task.getResult();
                if (currentLocation != null) {
                    Log.e(TAG, "getDeviceLocation: " + currentLocation.toString());
                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            DEFAULT_ZOOM,
                            "My Location");
                } else {

                    Log.d(TAG, "onComplete:current location is null");
                    Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    //
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    //
    private void initMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);
    }

    //
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission:getting Location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: permission failed");
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                mLocationPermissionGranted = true;
                initMap();
            }
        }
    }

    private void hideSoftKeyboard() {
    }


}