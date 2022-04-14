package com.example.ui_screens.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
import com.example.ui_screens.data.RecyclerTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantTablesActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String id;
    RestaurantTablesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_tables_activity);

        mAuth = FirebaseAuth.getInstance();
        id = getIntent().getStringExtra("id");

        RecyclerView rvTables = (RecyclerView) findViewById(R.id.rvTables);
        adapter = new RestaurantTablesAdapter(id);
        rvTables.setAdapter(adapter);
        rvTables.setLayoutManager(new LinearLayoutManager(this));

        rvTables.addOnItemTouchListener(new RecyclerTouchListener(this, rvTables, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(RestaurantTablesActivity.this, TableEditActivity.class);
                Bundle args = new Bundle();

                ArrayList<SerializableTable> serTables = new ArrayList<>();
                for(RestaurantTablesAdapter.Table table: adapter.getTables()){
                    serTables.add(new SerializableTable((int)table.getSeats()));
                }
                TablesDataHolder holder = new TablesDataHolder(serTables);

                args.putSerializable("tables", holder);
                i.putExtra("bundle", args);
                i.putExtra("position", position);
                i.putExtra("seats", adapter.getNumSeats(position));
                i.putExtra("id", id);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh();
    }

    public void openEditTable(View view) {
        //sets the intent of the function: changing the activity
        Intent i = new Intent(this, TableEditActivity.class);
        Bundle args = new Bundle();

        ArrayList<SerializableTable> serTables = new ArrayList<>();
        for(RestaurantTablesAdapter.Table table: adapter.getTables()){
            serTables.add(new SerializableTable((int)table.getSeats()));
        }
        TablesDataHolder holder = new TablesDataHolder(serTables);

        args.putSerializable("tables", holder);
        i.putExtra("bundle", args);
        i.putExtra("position", adapter.getTables().size());
        i.putExtra("seats", 0);
        i.putExtra("id", id);
        //starts the activity associated with the intent
        startActivity(i);
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

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
