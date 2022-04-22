package com.example.BookIt_App.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.BookIt_App.R;
import com.example.BookIt_App.data.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class RestaurantMainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseFirestore db2;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Restaurant restaurant;
    TextView restaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        restaurantName = (TextView) findViewById(R.id.textRestaurantName);
        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        db.collection("users")
                .document(user.getUid().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String restaurantID = document.getData().get("resId").toString();
                        Restaurant.makeFromId(restaurantID, res -> {
                            this.restaurant = res;
                            restaurantName.setText(restaurant.getName());
                        }, unused -> {
                            Toast.makeText(this,
                                    "Failed to get restaurant data",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        });
                    }

                });


    }

    public void restaurantEdit(View view) {
        CollectionReference users = db.collection("users");

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
                        Toast.makeText(RestaurantMainActivity.this, "Cannot use this feature with employee account", Toast.LENGTH_SHORT).show();
                    } else {
                        //sets the intent of the function: changing the activity
                        Intent intent = new Intent(RestaurantMainActivity.this, RestaurantEditActivity.class);
                        intent.putExtra("resId", restaurant.getId());
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
        intent.putExtra("id", restaurant.getId());
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void accountManagement(View view) {
        Intent intent = new Intent(this, RestaurantAccountManagement.class);
        startActivity(intent);
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Handles actions in the topbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.account:
                startActivity(new Intent(this, RestaurantAccountActivity.class));
                return true;
            case R.id.LogOut:
                mAuth.getInstance().signOut();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        Toast.makeText(getApplicationContext(), "You are already logged in!", Toast.LENGTH_LONG).show();
    }
}
