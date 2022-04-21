package com.example.BookIt_App.customers;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BookIt_App.R;
import com.example.BookIt_App.ui.login.RegistrationActivity;
import com.example.BookIt_App.restaurant_list.RestaurantListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

// ...
// Initialize Firebase Auth

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private FirebaseFirestore db;
    private Button button;
    FirebaseAuth mAuth;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);


        //initialize the firestore database
        db = FirebaseFirestore.getInstance();

        //assigning var names to each xml unit
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

    //checks if a user is already logged in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(CustomerLoginActivity.this, RestaurantListActivity.class));
        }
    }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //opens up registration page
    public void openRegistration(View view) {
        startActivity(new Intent(CustomerLoginActivity.this, RegistrationActivity.class));
    }

    public void loginClicked(View view) {
       loginUser();
    }

    //finishes the login
    public void finishLogin() {this.finish();}

    //method to login the user using firebase
    public void loginUser(){
        //store the email, pass in a var to be used later
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();


        //checks to make sure no errors occur when logging in
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

        //store a reference to the db to be used later
        CollectionReference users = db.collection("restaurant_users");

        //creating a query to check that only customers are allowed to enter from this page
        Query ref = users.whereIn("type", Arrays.asList("manager", "employee"));

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String email_check = "";
                if (task.isSuccessful()) {
                    //for each user that is a manager/employee
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //get the string email id for that user
                        String email_id = document.getString("name");

                        //check if the email id is the same as inputted email id
                        if (email_id.equals(email)) {
                            email_check = email_id;
                            Log.d("BookIt", document.getId() + " => " + document.getData() + email_check);
                        }
                    }
                    //if the emails are the same, then produce a toast and cancel the login
                    if (email.equals(email_check)) {
                        Toast.makeText(CustomerLoginActivity.this, "Cannot login with restaurant account", Toast.LENGTH_SHORT).show();
                    } else { //otherwise continue with firebase login
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) { //making toasts for when the task is successful
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
                else {
                    Log.d("BookIt", "Error getting documents: ", task.getException());
                }
            }
        });


    }

}

