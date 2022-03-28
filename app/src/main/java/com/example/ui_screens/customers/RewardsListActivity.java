package com.example.ui_screens.customers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.customers.MainActivity;
import com.example.ui_screens.R;
import com.example.ui_screens.customers.RestaurantLoginActivity;
import com.example.ui_screens.customers.ViewRestaurantActivity;
import com.example.ui_screens.data.RecyclerTouchListener;
import com.example.ui_screens.restaurant_list.RestaurantListActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class RewardsListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView rvRewards = findViewById(R.id.rvRewards);

        RewardsListAdapter rvRewardsAdapter = new RewardsListAdapter(db);
        rvRewards.setAdapter(rvRewardsAdapter);
        rvRewards.setLayoutManager(new LinearLayoutManager(this));

        rvRewards.addOnItemTouchListener(new RecyclerTouchListener(this, rvRewards, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //changes activity to a different one
                Intent intent = new Intent(RewardsListActivity.this, ViewRestaurantActivity.class);
                intent.putExtra("id", view.getTag().toString());
                //starts the activity associated with the intent
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Handles actions in the topbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.nearbyrestaurants:
                startActivity(new Intent(this, RestaurantListActivity.class));
                return true;
            case R.id.account:
                startActivity(new Intent(this, RestaurantLoginActivity.class));
                return true;
            case R.id.chat:
                startActivity(new Intent(this, RestaurantLoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
