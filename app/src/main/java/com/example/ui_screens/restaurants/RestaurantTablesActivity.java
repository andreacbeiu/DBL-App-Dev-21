package com.example.ui_screens.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;

public class RestaurantTablesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_tables_activity);
    }

    public void openEditTable(View view) {
        //sets the intent of the function: changing the activity
        Intent intent = new Intent(this, TableEditActivity.class);
        //starts the activity associated with the intent
        startActivity(intent);
    }
}
