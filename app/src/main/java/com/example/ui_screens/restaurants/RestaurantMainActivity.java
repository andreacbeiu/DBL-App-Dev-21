package com.example.ui_screens.restaurants;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RestaurantMainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseFirestore db2;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String restaurantID;
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
                        restaurantID = document.getData().get("resId").toString();
                        System.out.println(restaurantID);
                        db2.collection("restaurants")
                                .document(restaurantID)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if(task2.isSuccessful()) {
                                        DocumentSnapshot document2 = task2.getResult();
                                        restaurantName.setText(document2.getData().get("name").toString());
                                    }
                                });
                    }
                });

        RecyclerView resRv = (RecyclerView) findViewById(R.id.reservationListRestMain);
        ReservationsListAdapter adapter = new ReservationsListAdapter();
        resRv.setAdapter(adapter);
        resRv.setLayoutManager(new LinearLayoutManager(this));

        SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy");
        Date date = new Date();
        db.collection("reservation")
                .whereEqualTo("date", formatter.format(date))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        TextView numOfReservations = (TextView) findViewById(R.id.textReservedTables);
                        ArrayList<ReservationsListAdapter.Reservation> reservations = new ArrayList<>();
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Map<String, Object> data = document.getData();
                            if(data.get("restaurant").equals(restaurantID)){
                                ReservationsListAdapter.Reservation reservation = new ReservationsListAdapter.Reservation(
                                        data.get("time").toString(),
                                        data.get("message").toString(),
                                        data.get("table").toString()
                                );
                                reservations.add(reservation);
                            }
                        }
                        numOfReservations.setText("Reserved tables........." + reservations.size());
                        adapter.setReservations(reservations);

                    } else {
                        Toast.makeText(this, "Failed to get reservations", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                        Toast.makeText(RestaurantMainActivity.this, "Cannot login with customer account", Toast.LENGTH_SHORT).show();
                    } else {
                        //sets the intent of the function: changing the activity
                        Intent intent = new Intent(RestaurantMainActivity.this, RestaurantEditActivity.class);
                        intent.putExtra("resId", restaurantID);
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
