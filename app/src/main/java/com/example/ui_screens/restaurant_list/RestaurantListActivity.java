package com.example.ui_screens.restaurant_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
import com.example.ui_screens.RestaurantLoginActivity;
import com.example.ui_screens.ViewRestaurantActivity;
import com.example.ui_screens.data.RecyclerTouchListener;
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

        rvRestaurants.addOnItemTouchListener(new RecyclerTouchListener(this, rvRestaurants, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //changes activity to a different one
                Intent intent = new Intent(RestaurantListActivity.this, ViewRestaurantActivity.class);
                //starts the activity associated with the intent
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
