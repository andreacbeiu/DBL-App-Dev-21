package com.example.ui_screens.restaurants;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;

public class TableEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_edit_table);
    }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
