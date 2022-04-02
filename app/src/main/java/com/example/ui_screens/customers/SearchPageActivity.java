package com.example.ui_screens.customers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ui_screens.R;
import com.example.ui_screens.restaurants.RestaurantLoginActivity;

public class SearchPageActivity extends AppCompatActivity {

    // These are the global variables
    EditText search_value;
    TextView result;
    Button buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        search_value  = (EditText) findViewById(R.id.editTextTextPersonName5);
        result = (TextView) findViewById(R.id.textView20);
        buttonSearch = (Button) findViewById(R.id.button6);
    }

    public void displayQuery(View view) {
        String str_search_value = search_value.getText().toString();
        result.setText(str_search_value);
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Handles actions in the topbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchPageActivity.class));
                return true;
            case R.id.account:
                startActivity(new Intent(this, RestaurantLoginActivity.class));
                return true;
            case R.id.restaurantLogOut:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}