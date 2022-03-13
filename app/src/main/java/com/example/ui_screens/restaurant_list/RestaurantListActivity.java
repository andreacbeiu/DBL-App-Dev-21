package com.example.ui_screens.restaurant_list;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView rvRestaurants = findViewById(R.id.rvRestaurants);
        RestaurantListAdapter rvRestaurantsAdapter = new RestaurantListAdapter(db);
        rvRestaurants.setAdapter(rvRestaurantsAdapter);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));
    }
}
