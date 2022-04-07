package com.example.ui_screens.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RestaurantMainActivity extends AppCompatActivity {

    private String resId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);
        resId = getIntent().getStringExtra("resId");

        db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .document(resId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Map<String, Object> data = (HashMap<String, Object>)task.getResult().getData();
                        TextView tvResName = (TextView) findViewById(R.id.textRestaurantName);
                        tvResName.setText(data.get("name").toString());
                    }
                });

        SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy");
        Date date = new Date();
        db.collection("reservation")
                .whereEqualTo("date", formatter.format(date))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        TextView numOfReservations = (TextView) findViewById(R.id.textReservedTables);
                        numOfReservations.setText("Reserved tables........." + task.getResult().size());
                    } else {
                        Toast.makeText(this, "Failed to get reservations", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void restaurantEdit(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantEditActivity.class);
        intent.putExtra("resId", resId);
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void restaurantRewards(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantRewardsActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }

    public void restaurantTables(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, RestaurantTablesActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }
}
