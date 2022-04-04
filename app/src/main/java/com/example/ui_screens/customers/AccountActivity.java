package com.example.ui_screens.customers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ui_screens.R;
import com.example.ui_screens.data.Restaurant;
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

    private TextView UsersEmail, UsersPassword, Reservation;
    private List<String> reservations = new ArrayList<>();
    private String email, str_restaurant;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Reservation = (TextView) findViewById(R.id.tvReservation);

        //get authorisation instance
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        UsersEmail = (TextView) findViewById(R.id.tvUserEmail);
        UsersPassword = (TextView) findViewById(R.id.tvUserPassword);

        db = FirebaseFirestore.getInstance();
        db.collection("reservation")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                        reservations = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                reservations.add(document.getData().get("date").toString());
                            }
                        }

                    }
                });

        Reservation = (TextView) findViewById(R.id.tvReservation);

        //query = db.collection("reservation").whereEqualTo("userID", user.getUid());


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

    public void displayRestaurant(View v) {
        Reservation.setText(reservations.get(0));
    }

}