package com.example.ui_screens.customers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ui_screens.R;
import com.example.ui_screens.data.Restaurant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ViewRestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    int mYear, mMonth, mDay, mHour, mMinute;
    HashMap<String, String> reservation = new HashMap<>();
    String preferredDate, preferredTime, str_nrpeople, str_message, ReservationRestaurant, str_userID;
    EditText nrpeople, message;
    TextView map;

    FirebaseAuth mAuth;
    String str_restaurantId = new String("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        mAuth = FirebaseAuth.getInstance();
        str_userID = mAuth.getCurrentUser().getUid();

        //selecting book button + adding click listener
        Button bookbutton = findViewById(R.id.bookbutton);
        bookbutton.setOnClickListener(this);
        //setting values for reservation data input
        preferredDate = "25-03-2022";
        preferredTime = "23:51";
        str_nrpeople = "0";
        str_message = "";
        reservation.put("message", str_message);
        reservation.put("restaurant", ReservationRestaurant);
        reservation.put("date", preferredDate);
        reservation.put("time", preferredTime);
        reservation.put("table", str_nrpeople);
        reservation.put("userID", str_userID);
        map = (TextView) findViewById(R.id.textView16);


        String restaurantId = getIntent().getExtras().getString("id");
        str_restaurantId = restaurantId;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .document(restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ((TextView)findViewById(R.id.tvViewRestaurantName)).setText(task.getResult().getData().get("name").toString());
                        ReservationRestaurant = (task.getResult().getData().get("name").toString());
                    }
                });
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
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            case R.id.restaurantLogOut:
                mAuth.getInstance().signOut();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //creates a date-picking dialog window when a button is pressed
    @Override
    public void onClick(View v) {
        pickDateDialog();
    }


    //date-picking dialog builder
    public void pickDateDialog() {

        //Get Current Date + set day, month, year vars
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        preferredDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        //txtDate.setText(preferreddate);
                        //Toast.makeText(getApplicationContext(),"You have made a booking!",Toast.LENGTH_LONG).show();
                        pickTimeDialog();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    //time-picking dialog builder
    public void pickTimeDialog() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (hourOfDay < 10) {
                            preferredTime = "0" + hourOfDay;
                        } else {
                            preferredTime = hourOfDay + "";
                        }
                        if (minute < 10) {
                            preferredTime = preferredTime + ":0" + minute;
                        } else {
                            preferredTime = preferredTime + ":" + minute;
                        }

                        confirmBookingDialog();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    //confirms the booking via asking for nr of people and optional message
    public void confirmBookingDialog() {
        //builds alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Book a Table");
        //view inflater
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialogbooklayout, null, false);
        nrpeople = (EditText) dialogView.findViewById(R.id.nrpeople);
        message = (EditText) dialogView.findViewById(R.id.message);
        //set layout of the dialog pop up
        dialog.setView(dialogView);

        //sets positive button
        dialog.setPositiveButton("BOOK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //initialise all query check variables
                        AtomicBoolean isTableAvailable = new AtomicBoolean();
                        AtomicReference<Boolean> isTableReserved = new AtomicReference<>(new Boolean(false));
                        //initialise query counter
                        AtomicInteger entrycounter = new AtomicInteger(0);
                        AtomicReference<String> str_entrycounter = new AtomicReference<>("0");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                        FirebaseFirestore db3 = FirebaseFirestore.getInstance();

                        //creating string vars for booking data
                        str_nrpeople = nrpeople.getText().toString();
                        int int_nrpeople = Integer.parseInt(str_nrpeople);
                        str_message = message.getText().toString();

                        //querying the restaurant tables
                        db.collection("restaurants")
                                .whereEqualTo("name", ReservationRestaurant)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            for(Map<String, Object> entry : (ArrayList<Map<String, Object>>) document.getData().get("tables")) {
                                                isTableReserved.set(false);
                                                if (((Long) entry.get("seats")).intValue() >= int_nrpeople) {
                                                    //querying the reservations
                                                    db2.collection("reservations")
                                                            .whereEqualTo("restaurant", str_restaurantId)
                                                            .get()
                                                            .addOnCompleteListener(task2 -> {
                                                                if(task2.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                                        if(document2.getData().get("time") == preferredTime &&
                                                                            document2.getData().get("date") == preferredDate &&
                                                                            document2.getData().get("table") == str_entrycounter) {
                                                                                isTableReserved.set(true);
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                    if (isTableReserved.get()) { isTableAvailable.set(false); System.out.println("I set it false");} else { isTableAvailable.set(true); System.out.println("I set it true"); reservation.put("table", str_entrycounter.toString()); }
                                                }
                                                entrycounter.getAndIncrement();
                                                str_entrycounter.set(Integer.toString(entrycounter.get()));
                                            }

                                        }
                                    }

                                    if(isTableAvailable.get()) {
                                        System.out.println("printed if");
                                        //adding values to hashmap
                                        reservation.put("message", str_message);
                                        reservation.put("restaurant", str_restaurantId);
                                        reservation.put("date", preferredDate);
                                        reservation.put("time", preferredTime);
                                        reservation.put("userID", str_userID);

                                    } else {
                                        Toast.makeText(getApplicationContext(),"No tables available at that time!",Toast.LENGTH_LONG).show();
                                    }

                                });

                        //adding reservation to database
                        db3.collection("reservation")
                                .add(reservation)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        System.out.println("printed addition to collection");
                                        Toast.makeText(getApplicationContext(), "You have booked a table!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"There was an error!",Toast.LENGTH_LONG).show();
                                    }
                                });

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



}

