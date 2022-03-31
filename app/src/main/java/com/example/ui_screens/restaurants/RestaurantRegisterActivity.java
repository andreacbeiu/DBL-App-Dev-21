package com.example.ui_screens.restaurants;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;
import com.example.ui_screens.customers.CustomerLoginActivity;
import com.example.ui_screens.ui.login.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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
        editTextToken = (EditText) findViewById(R.id.editTextTextPassword);
        aSwitch = (Switch) findViewById(R.id.switch1);



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
        String token = editTextToken.getText().toString();
        Boolean manager = aSwitch.isChecked();
        String restaurant = editTextRestaurant.getText().toString();

        String token_mcd = "subwayisbetter";

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




        HashMap<String, String> user = new HashMap<String, String>();

        user.put("name", name);
        user.put("restaurant", restaurant);

        if (manager) {
            if (token.isEmpty() && !restaurant.isEmpty()) {
                editTextPasswordRepeat.setError("Need Manager Specific Token for " + restaurant);
                editTextPasswordRepeat.requestFocus();
                return;
            } else if (!restaurant.equals("McDonalds")) {
                editTextPasswordRepeat.setError("Need Manager Specific Token for " + restaurant);
                editTextPasswordRepeat.requestFocus();
                return;
            } else if (restaurant.equals("McDonalds") && token.equals(token_mcd)) {
                user.put("type", "manager");
            }
        } else {
            user.put("type", "employee");
        }



        db.collection("restaurant_users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("BookIT", "DocumentSnapshot written with ID " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("BookIt", "Error adding document", e);
            }
        });

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RestaurantRegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RestaurantRegisterActivity.this, RestaurantEditActivity.class));
                } else {
                    Toast.makeText(RestaurantRegisterActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
