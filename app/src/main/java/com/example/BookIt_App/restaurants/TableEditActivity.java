package com.example.BookIt_App.restaurants;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.BookIt_App.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class TableEditActivity extends AppCompatActivity {
    EditText etSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_edit_table);

        etSeats = (EditText) findViewById(R.id.etEditTableSeats);
        etSeats.setText(((Integer)getIntent().getExtras().getInt("seats")).toString());
    }

    public void saveTable(View view){
        //Get tables from intent
        Bundle args = getIntent().getBundleExtra("bundle");
        TablesDataHolder holder = (TablesDataHolder) args
                .getSerializable("tables");
        ArrayList<HashMap<String, Object>> tables = new ArrayList<>();

        for(SerializableTable table: holder.tables){ //Create hashmap from tables to be compatible with FireBase
            HashMap<String, Object> map = new HashMap<>();
            map.put("occupied", false);
            map.put("seats", table.getSeats());
            tables.add(map);
        }

        int pos = getIntent().getIntExtra("position", tables.size());

        if(pos == tables.size()){ //If position is not in array, create entirely new table
            HashMap<String, Object> map = new HashMap<>();
            map.put("seats", Integer.parseInt(etSeats.getText().toString()));
            map.put("occupied", false);
            tables.add(map);
        } else { //Else just update the table at the position
            tables.get(pos).put("seats", Integer.parseInt(etSeats.getText().toString()));
        }

        DocumentReference resRef = FirebaseFirestore.getInstance() //Upload the data
                .collection("restaurants")
                .document(getIntent().getStringExtra("id"));
        resRef.update("tables", tables)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(unused -> {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        });

        this.finish(); //Finish the Activity
    }

    public void deleteTable(View view){
        //Get tables from intent
        Bundle args = getIntent().getBundleExtra("bundle");
        TablesDataHolder holder = (TablesDataHolder) args
                .getSerializable("tables");
        ArrayList<HashMap<String, Object>> tables = new ArrayList<>();

        for(SerializableTable table: holder.tables){ //Convert tables to map
            HashMap<String, Object> map = new HashMap<>();
            map.put("occupied", false);
            map.put("seats", table.getSeats());
            tables.add(map);
        }
        int pos = getIntent().getIntExtra("position", tables.size());

        if(pos == tables.size()){ //If table not in list, just return
            return;
        }

        tables.remove(pos); //Else remove table and upload the updated list
        DocumentReference resRef = FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(getIntent().getStringExtra("id"));
        resRef.update("tables", tables)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(unused -> {
            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
        });

        this.finish();
    }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
