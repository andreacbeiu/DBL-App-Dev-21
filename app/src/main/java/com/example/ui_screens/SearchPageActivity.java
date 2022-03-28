package com.example.ui_screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    public void displayResult(View view) {
        String str_search_value = search_value.getText().toString();
        result.setText(str_search_value);
    }
}