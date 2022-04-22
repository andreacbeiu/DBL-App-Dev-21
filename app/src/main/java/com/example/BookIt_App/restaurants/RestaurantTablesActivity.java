package com.example.BookIt_App.restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookIt_App.R;
import com.example.BookIt_App.data.RecyclerTouchListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

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
                //Create intent and bundle to pass data
                Intent i = new Intent(RestaurantTablesActivity.this, TableEditActivity.class);
                Bundle args = new Bundle();

                ArrayList<SerializableTable> serTables = new ArrayList<>(); //Convert tables to serializable data so they can be passed
                for(RestaurantTablesAdapter.Table table: adapter.getTables()){
                    serTables.add(new SerializableTable((int)table.getSeats()));
                }
                TablesDataHolder holder = new TablesDataHolder(serTables);

                args.putSerializable("tables", holder); //Put all extra data
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
        adapter.refresh(); //Fetch new data on resume
    }

    public void openEditTable(View view) {
        //sets the intent of the function: changing the activity
        Intent i = new Intent(this, TableEditActivity.class);
        Bundle args = new Bundle();

        //All the same as in the ontap listener, but with position as size so it is a new table
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
