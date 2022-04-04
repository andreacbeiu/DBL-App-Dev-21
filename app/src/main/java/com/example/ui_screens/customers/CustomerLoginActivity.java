package com.example.ui_screens.customers;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ui_screens.R;
import com.example.ui_screens.ui.login.RegistrationActivity;
import com.example.ui_screens.restaurant_list.RestaurantListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// ...
// Initialize Firebase Auth

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button button;
    FirebaseAuth mAuth;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        editTextEmail = (EditText) findViewById(R.id.loginemail);
        editTextPassword = (EditText) findViewById(R.id.passwordlogin);
        mAuth = FirebaseAuth.getInstance();

        button = (Button) findViewById(R.id.loginbutton);
        button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               loginUser();
           }
       });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(CustomerLoginActivity.this, RestaurantListActivity.class));
        }
    }
    //opens up registration page
    public void openRegistration(View view) {
        startActivity(new Intent(CustomerLoginActivity.this, RegistrationActivity.class));
    }

    public void loginClicked(View view) {
       loginUser();
    }

    public void finishLogin() {this.finish();}

    public void loginUser(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty()) {
            editTextEmail.setError("Email Cannot Be Empty");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password Cannot Be Empty");
            editTextPassword.requestFocus();
            return;

        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CustomerLoginActivity.this, "User Logged In Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CustomerLoginActivity.this, RestaurantListActivity.class));
                    finishLogin();
                } else {
                    Toast.makeText(CustomerLoginActivity.this, "User Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

