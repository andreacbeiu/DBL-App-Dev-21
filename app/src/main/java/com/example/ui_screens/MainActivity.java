package com.example.ui_screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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


}