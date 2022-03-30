package com.example.ui_screens.customers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import com.example.ui_screens.R;
import com.example.ui_screens.restaurants.RestaurantMainActivity;
import com.example.ui_screens.restaurants.RestaurantRegisterActivity;

public class RestaurantLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_login);
    }

    public void restaurantAccountLog(View view) {
        //temporarily just leads to the next page
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantMainActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }
    public void restaurantRegister(View view) {
        Intent intent = new Intent(this, RestaurantRegisterActivity.class);
        startActivity(intent);
    }
}