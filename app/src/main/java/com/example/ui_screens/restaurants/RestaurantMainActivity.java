package com.example.ui_screens.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;
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

public class RestaurantMainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    public void restaurantEdit(View view) {
        CollectionReference users = db.collection("restaurant_users");

        Query ref = users.whereIn("type", Arrays.asList("employee"));

        String email = user.getEmail();

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
                        Toast.makeText(RestaurantMainActivity.this, "Cannot login with customer account", Toast.LENGTH_SHORT).show();
                    } else {
                        //sets the intent of the function: changing the activity
                        Intent intent = new Intent(RestaurantMainActivity.this, RestaurantEditActivity.class);
                        //starts the activity associated with the intent
                        startActivity(intent);
                    }
                }
                else {
                    Log.d("BookIt", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    public void restaurantRewards(View view) {
        CollectionReference users = db.collection("restaurant_users");

        Query ref = users.whereIn("type", Arrays.asList("employee"));

        String email = user.getEmail();

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
                        Toast.makeText(RestaurantMainActivity.this, "Cannot login with customer account", Toast.LENGTH_SHORT).show();
                    } else {
                        //sets the intent of the function: changing the activity
                        Intent intent = new Intent(RestaurantMainActivity.this, RestaurantRewardsActivity.class);
                        //starts the activity associated with the intent
                        startActivity(intent);
                    }
                }
                else {
                    Log.d("BookIt", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void restaurantTables(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantTablesActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void logoutRestaurant(View view) {
        mAuth.signOut();
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantLoginActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }
}
