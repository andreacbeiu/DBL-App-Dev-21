package com.example.ui_screens;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ui_screens.ui.login.RegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// ...
// Initialize Firebase Auth

public class CustomerLoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(CustomerLoginActivity.this, RegistrationActivity.class));
        }
    }
    public void openRegistration(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RegistrationActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }

}

