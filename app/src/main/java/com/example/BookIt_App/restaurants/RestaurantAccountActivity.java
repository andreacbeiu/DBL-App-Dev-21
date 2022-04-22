package com.example.BookIt_App.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BookIt_App.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RestaurantAccountActivity extends AppCompatActivity {

    private String email;
    private TextView UsersEmail;
    private Button delButton;
    FirebaseAuth mAuth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_account);



        //initiate firebase authentication token
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        UsersEmail = (TextView) findViewById(R.id.tvRestUserEmail);
        delButton = (Button) findViewById(R.id.delbutton);
    }

    @Override
    public void onStart() {
        super.onStart();

        //getting current user object
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //making robust
        if (currentUser == null) {
            UsersEmail.setText("You are not signed in!");

        } else {
            email = user.getEmail();
            UsersEmail.setText(email);
        }
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public void deleteAccount(View view) {
        //firebase standard method to delete an account from database
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(RestaurantAccountActivity.this, RestaurantLoginActivity.class));
                            Log.d("BookIt", "User account deleted.");
                        }
                    }
                });
    }

    //Handles actions in the topbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //using switchase to show the different options associated with each account
        switch(item.getItemId()) {
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
}