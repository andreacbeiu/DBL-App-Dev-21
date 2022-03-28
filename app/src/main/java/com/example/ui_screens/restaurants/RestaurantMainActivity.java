package com.example.ui_screens.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;

public class RestaurantMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);
    }

    public void restaurantEdit(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantEditActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void restaurantRewards(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantRewardsActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void restaurantTables(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantTablesActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }
}
