package com.example.ui_screens.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ui_screens.R;
import com.example.ui_screens.customers.CustomerLoginActivity;
import com.example.ui_screens.restaurant_list.RestaurantListActivity;
import com.example.ui_screens.ui.login.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class RestaurantLoginActivity extends AppCompatActivity {


    private EditText editTextEmail, editTextPassword;
    private Button button;
    private FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_login);

        db = FirebaseFirestore.getInstance();

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

        CollectionReference users = db.collection("restaurant_users");

        Query ref = users.whereIn("type", Arrays.asList("customer"));

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String email_check = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String email_id = document.getString("name");

                        if (email_id.equals(email)) {
                            email_check = email_id;
                            Log.d("BookIt", document.getId() + " => " + document.getData() + email_check);
                        }
                    }

                    if (email.equals(email_check)) {
                        Log.d("BookIt", "if statement reached");
                        Toast.makeText(RestaurantLoginActivity.this, "Cannot login with customer account", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RestaurantLoginActivity.this, "User Logged In Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RestaurantLoginActivity.this, RestaurantMainActivity.class));
                                } else {
                                    Toast.makeText(RestaurantLoginActivity.this, "User Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else {
                    Log.d("BookIt", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }



}