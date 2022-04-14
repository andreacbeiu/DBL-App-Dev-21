package com.example.ui_screens.restaurants;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ui_screens.R;
import com.example.ui_screens.restaurant_list.RestaurantListActivity;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RestaurantEditActivity extends AppCompatActivity {

    private String id;
    private Map<String, Object> data;
    FirebaseFirestore db;
    EditText etName;
    EditText etDescription;
    ActivityResultLauncher<String> mGetContent, mGetPdf;
    FirebaseAuth mAuth;
    private LocationRequest locationRequest;
    Map map;
    Button locationButton;
    GeoLocation currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit);

        mAuth = FirebaseAuth.getInstance();

        id = getIntent().getStringExtra("resId");

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + id + ".jpg");
        final StorageReference pdfRef = FirebaseStorage.getInstance().getReference().child("menus/" + id + ".pdf");
        final ImageView restIV = (ImageView)findViewById(R.id.ivEditRestaurantImage);
        final long ONE_MEGABYTE = 1024*1024;

        ref.getBytes(ONE_MEGABYTE * 2).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            restIV.setImageBitmap(bmp);
        }).addOnFailureListener(unused -> {
            restIV.setImageDrawable(getDrawable(R.drawable.default_restaurant));
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            System.out.println(uri.getPath());
                            InputStream stream = getContentResolver().openInputStream(uri);

                            ref.putStream(stream)
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of image was unsuccesful", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnSuccessListener(unused -> {
                                        restIV.setImageURI(uri);
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of image was succesful", Toast.LENGTH_SHORT).show();
                                    });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
        mGetPdf = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            System.out.println(uri.getPath());
                            InputStream stream = getContentResolver().openInputStream(uri);

                            pdfRef.putStream(stream)
                                    .addOnFailureListener(unused -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of menu was unsuccesful", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of menu was succesful", Toast.LENGTH_SHORT).show();
                                    });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

        etName = (EditText)findViewById(R.id.etEditInfoName);
        etDescription = (EditText)findViewById(R.id.etEditInfoDescription);
//        map = findViewById(R.id.mapView2);
        locationButton = findViewById(R.id.restLocation);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        //locationButton.setOnClickListener(v -> getCurrentLocation());


        db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        data = (HashMap<String, Object>)task.getResult().getData();
                        etName.setText(data.get("name").toString());
                        etDescription.setText(data.get("description").toString());
                    }
                });
    }

    public void save(View view){
        data.put("name", etName.getText().toString());
        data.put("description", etDescription.getText().toString());
        db.collection("restaurants")
                .document(id)
                .set(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Saving was succesful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(unused -> {
                    Toast.makeText(this, "ERROR: data was not saved", Toast.LENGTH_SHORT).show();
                });
    }

    public void viewMenu(View view){
        StorageReference menuRef = FirebaseStorage.getInstance().getReference().child("menus/" + id + ".pdf");
        menuRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        }).addOnFailureListener(unused -> {
            Toast.makeText(this, "No menu available", Toast.LENGTH_SHORT).show();
        });
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Handles actions in the topbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.account:
                startActivity(new Intent(this, RestaurantAccountActivity.class));
                return true;
            case R.id.LogOut:
                mAuth.getInstance().signOut();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void chooseImage(View view){
        mGetContent.launch("image/*");
    }

    public void chooseMenu(View view) { mGetPdf.launch("application/pdf"); }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (!isGPSEnabled())
                turnOnGPS();
            else {
                getCurrentLocation();
                System.out.println("cacaman");
                //getNearbyRestaurants();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2)
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
                System.out.println("cacaman");
                //getNearbyRestaurants();
            }
    }

    public void registerLocation(View view){
        if (!isGPSEnabled())
            turnOnGPS();
        else {
            getCurrentLocation();
            System.out.println("cacaman");
            Toast.makeText(RestaurantEditActivity.this, "The location has been registered!", Toast.LENGTH_SHORT).show();
            //getNearbyRestaurants();
        }
    }

    public void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(RestaurantEditActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(RestaurantEditActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(RestaurantEditActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        currentLocation = new GeoLocation(latitude, longitude);
                                        Toast.makeText(RestaurantEditActivity.this, "The location has been registered!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }, Looper.getMainLooper());

                } else
                    turnOnGPS();

            } else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(RestaurantEditActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(RestaurantEditActivity.this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Device does not have location
                        break;
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = false;

        try {
            enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        return enabled;
    }
}
