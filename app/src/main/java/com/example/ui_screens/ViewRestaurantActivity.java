package com.example.ui_screens;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class ViewRestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        //selecting book button + adding click listener
        Button bookbutton = findViewById(R.id.bookbutton);
        bookbutton.setOnClickListener(this);

        String restaurantId = getIntent().getExtras().getString("id");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .document(restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ((TextView)findViewById(R.id.tvViewRestaurantName)).setText(task.getResult().getData().get("name").toString());
                    }
                });
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        return true;
    }

    //Handles menu actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /*private void dialogBook(View view) {
        //use an alert dialog
        AlertDialog.Builder dialog_Book = new AlertDialog.Builder(this);
        //set layout
        dialog_Book.setView(R.layout.dialog_book);
        //set message + title
        dialog_Book.setMessage("Book a Table!");
        dialog_Book.setTitle("Book a Table!");

        AlertDialog dialog = dialog_Book.create();


        //create button for confirmation
        //dialog_Book.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
        //    public void onClick(DialogInterface dialog, int id) {
        //        // START THE GAME!
        //    }
        //});
        //create button for deletion of event
        //dialog_Book.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
        //    public void onClick(DialogInterface dialog, int id) {
        //        // User cancelled the dialog
        //    }
        }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bookbutton:
                alertDialog();
                break;
        }
    }

    private void alertDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Book a Table");

        dialog.setView(R.layout.dialogbooklayout);
        dialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(),"You have booked a table!",Toast.LENGTH_LONG).show();
                        EditText nrpeople = (EditText) findViewById(R.id.nrpeople);
                        EditText date = (EditText) findViewById(R.id.date);
                        EditText message = (EditText) findViewById(R.id.message);
                        //get the input values
                        String str_nrpeople = nrpeople.getText().toString();
                        String str_date = date.getText().toString();
                        String str_message = message.getText().toString();
                    }
                });
        dialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"You have cancelled a booking!",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

}

