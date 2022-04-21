package com.example.BookIt_App.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.BookIt_App.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantRegisterActivity extends AppCompatActivity {

    private Button button;
    private EditText editTextEmail, editTextPassword, editTextRestaurant, editTextName, editTextPasswordRepeat, editTextToken;
    private Switch aSwitch;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_signup_activity);

        db = FirebaseFirestore.getInstance();


        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword2);
        editTextPasswordRepeat = (EditText) findViewById(R.id.editTextTextPassword3);
        editTextName = (EditText) findViewById(R.id.editTextTextPersonName10);
        editTextRestaurant = (EditText) findViewById(R.id.editTextTextPersonName11);



        button = (Button) findViewById(R.id.submit_restaurant_signup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });


        mAuth = FirebaseAuth.getInstance();
    }

    private void createUser() {

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String password_repeat = editTextPasswordRepeat.getText().toString();
        String name = editTextName.getText().toString();
        String restaurant = editTextRestaurant.getText().toString();

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

        if (name.isEmpty()) {
            editTextName.setError("Name Cannot Be Empty");
            editTextName.requestFocus();
            return;
        }

        if (restaurant.isEmpty()) {
            editTextRestaurant.setError("Restaurant Cannot Be Empty");
            editTextRestaurant.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be longer than 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(password_repeat)) {
            editTextPasswordRepeat.setError("Passwords must match!");
            editTextPasswordRepeat.requestFocus();
            return;
        }


        Map<String, Object> newRes = new HashMap<>();
        newRes.put("description", "");
        newRes.put("location", null);
        newRes.put("name", restaurant);
        newRes.put("rating", 4.5);
        newRes.put("tables", new ArrayList());

        db.collection("restaurants")
                .add(newRes)
                .addOnSuccessListener(documentReference -> {
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("resId", documentReference.getId());
                    user.put("email", email);
                    user.put("phone", "");
                    user.put("address", "");
                    user.put("type", "manager");



                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    String user_id = task.getResult().getUser().getUid();
                                    user.put("userID", user_id);
                                    Log.d("BookIt", "user" + user);
                                    db.collection("users")
                                            .document(task.getResult().getUser().getUid())
                                            .set(user)
                                            .addOnSuccessListener(userDocRef -> {
                                                Toast.makeText(this, "Successfully created account", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(this, RestaurantMainActivity.class);
                                                i.putExtra("resId", documentReference.getId());
                                                startActivity(i);
                                            }).addOnFailureListener(unused -> {
                                        Toast.makeText(this, "Could not save user data", Toast.LENGTH_SHORT).show();
                                        task.getResult().getUser().delete();
                                        documentReference.delete();
                                    });

                                } else {
                                    Toast.makeText(this, "Could not create account", Toast.LENGTH_SHORT).show();
                                    documentReference.delete();
                                }
                            });


                })
                .addOnFailureListener(unused -> {
                    Toast.makeText(this, "Could not create restaurant", Toast.LENGTH_SHORT).show();
                });

    }
}
