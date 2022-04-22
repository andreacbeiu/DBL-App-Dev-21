package com.example.BookIt_App.restaurants;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BookIt_App.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class RestaurantAddAccount extends AppCompatActivity {

    private EditText editTextEmail, editTextPass, editTextName;

    private FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db, db2;
    String restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        //creating variable names for each xml unit

        editTextEmail = (EditText) findViewById(R.id.emailacc);
        editTextPass = (EditText) findViewById(R.id.passacc);
        editTextName = (EditText) findViewById(R.id.nameacc);



        //initializing databases and firebase authentication, also getting current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


    }

    public void accountAdd(View view) {

        /*
        * similar to registering an account, we first add the account to the database
        * then the account is added to authentication */

        String email = editTextEmail.getText().toString();
        String password = editTextPass.getText().toString();
        String name = editTextName.getText().toString();


        //creating an user hashmap with the relevant information

        Map<String, Object> user_acc = new HashMap<>();
        user_acc.put("name", name);
        user_acc.put("password", password);
        user_acc.put("email", email);

        //adding the user to the users database

        db.collection("users")
                .document(user.getUid().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               restaurantID = task.getResult().getString("resId");
                                               user_acc.put("resId", restaurantID);

                                               //adding the user to the employees database, since the account added manually is always an employee
                                               db.collection("employees").document(email).set(user_acc).addOnCompleteListener(task1 -> {
                                                   if (task1.isSuccessful()) {
                                                       mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                                               if (task.isSuccessful()) {
                                                                   //creating a manager account entails that a new restaurant also be created alongside the manager
                                                                   Map<String, Object> user = new HashMap<>();
                                                                   user.put("name", name);
                                                                   user.put("resId", restaurantID);
                                                                   user.put("email", email);
                                                                   user.put("phone", "");
                                                                   user.put("address", "");
                                                                   user.put("type", "employee");

                                                                   //adding to the user database as well
                                                                   db.collection("users")
                                                                           .document(task.getResult().getUser().getUid())
                                                                           .set(user)
                                                                           .addOnSuccessListener(userDocRef -> {
                                                                               Toast.makeText(RestaurantAddAccount.this, "Successfully created account", Toast.LENGTH_SHORT).show();
                                                                               Intent i = new Intent(RestaurantAddAccount.this, RestaurantMainActivity.class);
                                                                               i.putExtra("resId", restaurantID);
                                                                               startActivity(i);
                                                                           });
                                                                   startActivity(new Intent(RestaurantAddAccount.this, RestaurantMainActivity.class));
                                                               } else {
                                                                   Toast.makeText(RestaurantAddAccount.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                       });
                                                       Toast.makeText(RestaurantAddAccount.this, "Successfully created employee account", Toast.LENGTH_SHORT).show();
                                                   }
                                                   else {
                                                       Log.d("BookIt", "task failed" + task1.getException());
                                                   }
                                               });

                                           }
                                       });
        Log.d("BookIt", "user_acc"  + user_acc);




    }

}
