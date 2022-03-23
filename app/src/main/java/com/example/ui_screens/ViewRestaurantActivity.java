package com.example.ui_screens;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ui_screens.restaurant_list.RestaurantListActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

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

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + getIntent().getExtras().getString("id")+".jpg");
        ImageView imageView = findViewById(R.id.ivViewRestaurant);

        final long ONE_MEGABYTE = 1024*1024;
        storageRef.getBytes(ONE_MEGABYTE * 2).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }
        });
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
                startActivity(new Intent(this, SearchPageActivity.class));
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


    //creates a dialog window when bookbutton is pressed
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bookbutton:
                pickTimeDialog();
                break;
        }
    }


    //dialog window builder function for bookbutton
    public void pickTimeDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Book a Table");

        //set layout of the dialog pop upb
        dialog.setView(R.layout.dialogtimepicklayout);

        //sets positive button
        dialog.setPositiveButton("CONTINUE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        //get input values for nr of people + date + message
                        String str_date = getDate(R.id.date).toString();
                        String str_time = getTime(R.id.time).toString();
                        confirmBookingDialog();

                    }
                });

        //sets negative button
        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"You have cancelled a booking!",Toast.LENGTH_LONG).show();
            }
        });
        //create the dialog pop-up
        AlertDialog bookingDialog = dialog.create();
        bookingDialog.show();
    }

    public void confirmBookingDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Book a Table");

        //set layout of the dialog pop upb
        dialog.setView(R.layout.dialogbooklayout);

        //sets positive button
        dialog.setPositiveButton("BOOK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(),"You have booked a table!",Toast.LENGTH_LONG).show();
                        //get input values for nr of people + date + message
                        int int_nrpeople = getNrPeople(R.id.nrpeople);
                        String str_message = getMessage(R.id.message);
                    }
                });

        //sets negative button
        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"You have cancelled a booking!",Toast.LENGTH_LONG).show();
            }
        });
        //create the dialog pop-up
        AlertDialog bookingDialog = dialog.create();
        bookingDialog.show();
    }


    public int getNrPeople (int id) {
        EditText nrpeople = (EditText) findViewById(id);
        String str_nrpeople = nrpeople.getText().toString();
        int int_nrpeople = Integer.parseInt(str_nrpeople);
        return int_nrpeople;
    }

    public String getMessage (int id) {
        EditText message = (EditText) findViewById(id);
        String str_message = message.getText().toString();
        return str_message;
    }

    public String getDate (int id) {
        DatePicker date = (DatePicker) findViewById(id);
        int day = date.getDayOfMonth ();
        int month = date.getMonth();
        int year = date.getYear();
        String str_date = day + "/" + (month + 1) + "/" + year;
        return str_date;
    }

    public String getTime (int id) {
        TimePicker time = (TimePicker) findViewById(id);
        int hour = 0;
        int minute = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = time.getHour();
        }
        else { hour = time.getCurrentHour(); }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            minute = time.getMinute();
        }
        else { minute = time.getCurrentMinute(); }
        String str_time = minute + ":" + hour;
        return str_time;
    }





}

