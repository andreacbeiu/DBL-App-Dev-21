package com.example.ui_screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CustomerLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
    }

    public void CustomerSignup(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, CustomerSignup.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }


}