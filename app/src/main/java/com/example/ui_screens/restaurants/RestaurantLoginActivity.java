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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantLoginActivity extends AppCompatActivity {


    private EditText editTextEmail, editTextPassword;
    private Button button;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_login);

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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("ID " + task.getResult().getUser().getUid());
                    FirebaseFirestore.getInstance()
                            .collection("restaurant_users")
                            .document(task.getResult().getUser().getUid())
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    Toast.makeText(RestaurantLoginActivity.this, "User Logged In Successfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(RestaurantLoginActivity.this, RestaurantMainActivity.class);
                                    i.putExtra("resId", task1.getResult().getData().get("resId").toString());
                                    startActivity(i);
                                } else {
                                    Toast.makeText(RestaurantLoginActivity.this, "Could not sign in", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(RestaurantLoginActivity.this, "User Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}