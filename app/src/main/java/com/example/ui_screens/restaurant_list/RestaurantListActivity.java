package com.example.ui_screens.restaurant_list;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.customers.AccountActivity;
import com.example.ui_screens.customers.MainActivity;
import com.example.ui_screens.R;
import com.example.ui_screens.customers.SearchPageActivity;
import com.example.ui_screens.restaurants.RestaurantLoginActivity;
import com.example.ui_screens.customers.ViewRestaurantActivity;
import com.example.ui_screens.data.RecyclerTouchListener;
import com.example.ui_screens.data.Restaurant;
import com.example.ui_screens.data.Table;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantListActivity extends AppCompatActivity {
    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    GeoLocation currentLocation;
    private List<Restaurant> restaurants = new ArrayList<>();

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView rvRestaurants = findViewById(R.id.rvRestaurants);

        RestaurantListAdapter rvRestaurantsAdapter = new RestaurantListAdapter(db);
        rvRestaurants.setAdapter(rvRestaurantsAdapter);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        rvRestaurants.addOnItemTouchListener(new RecyclerTouchListener(this, rvRestaurants, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //changes activity to a different one
                Intent intent = new Intent(RestaurantListActivity.this, ViewRestaurantActivity.class);
                //intent.putExtra("id", String.valueOf(currentLocation));
                intent.putExtra("id", view.getTag().toString());
                //starts the activity associated with the intent
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isGPSEnabled())
            turnOnGPS();
        else {
            getCurrentLocation();
            //getNearbyRestaurants();
        }
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

    public GeoLocation getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(RestaurantListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(RestaurantListActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(RestaurantListActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        currentLocation = new GeoLocation(latitude, longitude);
                                        Toast.makeText(RestaurantListActivity.this, "The location has been registered!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }, Looper.getMainLooper());

                } else
                    turnOnGPS();

            } else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        return currentLocation;
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
                Toast.makeText(RestaurantListActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(RestaurantListActivity.this, 2);
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

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Handles actions in the topbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchPageActivity.class));
                return true;
            case R.id.account:
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            case R.id.restaurantLogOut:
                mAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void filterRestaurants(){
        List<DocumentSnapshot> matchingDocs = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Find cities within 50km of current location
        final double radiusInM = 2 * 1000;

        // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
        // a separate query for each pair. There can be up to 9 pairs of bounds
        // depending on overlap, but in most cases there are 4.
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(currentLocation, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("cities")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }

        this.restaurants = new ArrayList<>();

    // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)

                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot document : snap.getDocuments()) {
                                double lat = document.getDouble("lat");
                                double lng = document.getDouble("lng");

                                List<Table> tables = new ArrayList<>();

                                for(Map<String, Object> entry : (ArrayList<Map<String, Object>>) document.getData().get("tables")) {
                                    Table table = new Table(((Long) entry.get("seats")).intValue());
                                    tables.add(table);
                                }

                                // W    e have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match
                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, currentLocation);
                                if (distanceInM <= radiusInM) {

                                    Restaurant tempRestaurant = new Restaurant(document.getId(), document.getData().get("name").toString(),
                                  document.getData().get("description").toString(), ((Double) document.getData().get("rating")).floatValue(), tables);
                                  restaurants.add(tempRestaurant);
                                }
                            }
                        }

                       // do something with matchingDocs;
                    }
                });
    }

}
