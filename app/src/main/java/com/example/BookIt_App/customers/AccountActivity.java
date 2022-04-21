package com.example.BookIt_App.customers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BookIt_App.R;
import com.example.BookIt_App.data.Reservation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class AccountActivity extends AppCompatActivity {

    private TextView UsersEmail, Reservation;
    private List<Reservation> reservations = new ArrayList<>();
    private String email;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //get authorisation instance
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        UsersEmail = (TextView) findViewById(R.id.tvUserEmail);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView rvReservations = findViewById(R.id.rvReservations);
        AccountAdapter rvReservationsAdapter = new AccountAdapter(db, user.getUid());

        rvReservations.setAdapter(rvReservationsAdapter);
        rvReservations.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            UsersEmail.setText("You are not signed in!");

        } else {
            email = user.getEmail();
            UsersEmail.setText(email);
        }
    }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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
                Toast.makeText(getApplicationContext(),"You are already viewing your account!",Toast.LENGTH_LONG).show();
                return true;
            case R.id.LogOut:
                mAuth.getInstance().signOut();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteAccount(View view) {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(AccountActivity.this, CustomerLoginActivity.class));
                            Log.d("BookIt", "User account deleted.");
                        }
                    }
                });
    }




}