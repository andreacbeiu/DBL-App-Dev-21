package com.example.ui_screens.customers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ui_screens.R;
import com.example.ui_screens.restaurant_list.RestaurantListActivity;
import com.example.ui_screens.restaurants.RestaurantLoginActivity;
//import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void customerLogin(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, CustomerLoginActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void restaurantLogin(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantLoginActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void restaurantList(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantListActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }


}