package com.example.BookIt_App.restaurants;

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

import com.example.BookIt_App.R;
import com.example.BookIt_App.data.Restaurant;
import com.example.BookIt_App.universal.ViewPdfActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    Restaurant restaurant;
    private LocationRequest locationRequest;
    Map map;
    Button locationButton;
    GeoLocation currentLocation;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit);



        etName = (EditText)findViewById(R.id.etEditInfoName);
        etDescription = (EditText)findViewById(R.id.etEditInfoDescription);
        final ImageView restIV = (ImageView)findViewById(R.id.ivEditRestaurantImage);

        mAuth = FirebaseAuth.getInstance();

        id = getIntent().getStringExtra("resId");

        Restaurant.makeFromId(id, res -> {
            this.restaurant = res;
            etName.setText(res.getName());
            etDescription.setText(res.getDescription());
            restaurant.getImageBytes(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                restIV.setImageBitmap(bmp);
            }, unused -> {
                restIV.setImageDrawable(getDrawable(R.drawable.default_restaurant));
            });
        }, unused -> {
            Toast.makeText(this, "Could not retrieve restaurant data", Toast.LENGTH_SHORT).show();
        });

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + id + ".jpg");
        final StorageReference pdfRef = FirebaseStorage.getInstance().getReference().child("menus/" + id + ".pdf");
        final long ONE_MEGABYTE = 1024*1024;



        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), //Lets user select a file
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            System.out.println(uri.getPath());
                            InputStream stream = getContentResolver().openInputStream(uri); //Get input stream to upload from uri

                            ref.putStream(stream) //Upload it to FireBase using the reference created earlier
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RestaurantEditActivity.this, "ERROR: could not upload image", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnSuccessListener(unused -> {
                                        restIV.setImageURI(uri);
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of image was successful", Toast.LENGTH_SHORT).show();
                                    });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
        mGetPdf = registerForActivityResult(new ActivityResultContracts.GetContent(), //Same as mGetContent, but used for the menus
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            System.out.println(uri.getPath());
                            InputStream stream = getContentResolver().openInputStream(uri);

                            pdfRef.putStream(stream)
                                    .addOnFailureListener(unused -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of menu was unsuccessful", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of menu was successful", Toast.LENGTH_SHORT).show();
                                    });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

        locationButton = findViewById(R.id.restLocation);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

//        MapView mapView = (MapView)findViewById(R.id.mapView2);

//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(googleMap -> {
//            LatLng coordinates = new LatLng(5, 6);
//            googleMap.addMarker(new MarkerOptions().position(coordinates).title("address"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
//            mapView.onResume();
//        });

        locationButton.setOnClickListener(v -> getCurrentLocation());
        img = (ImageView) findViewById(R.id.ivlocationImage);

        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.location_image);
    }

    public void setLocation(){

    }

    public void save(View view){
        restaurant.setName(etName.getText().toString()); //Update restaurant data to filled in data
        restaurant.setDescription(etDescription.getText().toString());
        restaurant.uploadData(unused -> { //Upload it
            Toast.makeText(this, "Succesfully saved data", Toast.LENGTH_SHORT).show();
        }, unused -> {
            Toast.makeText(this, "ERROR: data was not saved", Toast.LENGTH_SHORT).show();
        });
    }

    public void viewMenu(View view){
        restaurant.getMenuUrl(uri -> { //Get the menu from the
            Intent i = new Intent(this, ViewPdfActivity.class);
            i.putExtra("url", uri);
            startActivity(i);
        }, unused -> {
            Toast.makeText(this, "Failed to get menu", Toast.LENGTH_SHORT).show();
        });
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Handles actions in the top bar menu
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
        mGetContent.launch("image/*"); //Let user select files that are images
    }

    public void chooseMenu(View view) {
        mGetPdf.launch("application/pdf"); //Let user select files that are of type pdf
    }

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
                //getNearbyRestaurants();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2)
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
                //getNearbyRestaurants();
            }
    }

    public void registerLocation(View view){
        if (!isGPSEnabled())
            turnOnGPS();
        else {
            img.setImageResource(R.drawable.current);
            System.out.println("macac");
            getCurrentLocation();
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
                                        System.out.println("help");
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
                Toast.makeText(RestaurantEditActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

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
