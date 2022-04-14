package com.example.ui_screens.customers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ui_screens.R;
import com.example.ui_screens.data.Reservation;
import com.example.ui_screens.data.Restaurant;
import com.example.ui_screens.restaurants.RestaurantAccountActivity;
import com.example.ui_screens.restaurants.RestaurantLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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