package com.example.BookIt_App.customers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BookIt_App.R;
import com.google.firebase.auth.FirebaseAuth;

public class SearchPageActivity extends AppCompatActivity {

    // These are the global variables
    EditText search_value;
    TextView result;
    Button buttonSearch;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        mAuth = FirebaseAuth.getInstance();
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
                Toast.makeText(getApplicationContext(),"You are already viewing the search page!",Toast.LENGTH_LONG).show();
                return true;
            case R.id.account:
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            case R.id.LogOut:
                mAuth.getInstance().signOut();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}