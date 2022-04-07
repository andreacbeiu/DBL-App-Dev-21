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
import com.example.ui_screens.data.Reservation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    int mYear, mMonth, mDay, mHour, mMinute;
    HashMap<String, String> ReservationToAdd = new HashMap<>();
    private List<Reservation> ReservationsToCheck = new ArrayList<>();
    private List<Integer> restaurantTables = new ArrayList<>();
    String preferredDate, preferredTime, str_nrpeople, str_message, str_RestaurantName, str_userID;
    EditText nrpeople, message;
    TextView map;

    FirebaseAuth mAuth;
    FirebaseFirestore db_tableArray;
    FirebaseFirestore db_reservationArray;
    String str_restaurantId = new String("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        mAuth = FirebaseAuth.getInstance();
        db_tableArray = FirebaseFirestore.getInstance();
        db_reservationArray = FirebaseFirestore.getInstance();
        str_userID = mAuth.getCurrentUser().getUid();

        //selecting book button + adding click listener
        Button bookbutton = findViewById(R.id.bookbutton);
        bookbutton.setOnClickListener(this);
        map = (TextView) findViewById(R.id.textView16);

        //setting restaurant name to title of page
        String restaurantId = getIntent().getExtras().getString("id");
        str_restaurantId = restaurantId;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .document(restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ((TextView)findViewById(R.id.tvViewRestaurantName)).setText(task.getResult().getData().get("name").toString());
                        str_RestaurantName = (task.getResult().getData().get("name").toString());
                    }
                });

        //setting values for reservation data input
        preferredDate = "25-03-2022";
        preferredTime = "23:51";
        str_nrpeople = "2";
        str_message = "";
        ReservationToAdd.put("message", str_message);
        ReservationToAdd.put("restaurant", str_restaurantId);
        ReservationToAdd.put("restName", str_RestaurantName);
        ReservationToAdd.put("date", preferredDate);
        ReservationToAdd.put("time", preferredTime);
        ReservationToAdd.put("table", str_nrpeople);
        ReservationToAdd.put("userID", str_userID);
        Reservation tempRes = new Reservation(preferredDate, preferredTime, str_message, "Restaurant", str_RestaurantName, str_nrpeople, str_userID);
        ReservationsToCheck.add(tempRes);

        //getting list of current tables for restaurant
        db_tableArray.collection("restaurants")
                .document(str_restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        for(Map<String, Object> entry : (ArrayList<Map<String, Object>>) document.getData().get("tables")) {
                            int tempSeats= ((Long) entry.get("seats")).intValue();
                            System.out.println(tempSeats);
                            restaurantTables.add(tempSeats);
                        }
                        System.out.println("GOT TABLESSSSSSSSSSSSSSSS");
                    }
                });

        //getting list of current reservations for restaurant
        db_reservationArray.collection("reservation")
                .whereEqualTo("restaurant", str_restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation tempReservation = new Reservation(document.getData().get("date").toString(), document.getData().get("time").toString(),
                                    document.getData().get("message").toString(), document.getData().get("restaurant").toString(), document.getData().get("restName").toString(),
                                    document.getData().get("table").toString(), document.getData().get("userID").toString());
                            ReservationsToCheck.add(tempReservation);
                        }
                        System.out.println("GOT RESERVATIONSSSSSSSSSSSSSSSSSSSS");
                        System.out.println(ReservationsToCheck.get(0).getTable());
                        //System.out.println(ReservationsToCheck.size());
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
                        //creating string vars for booking data
                        str_nrpeople = nrpeople.getText().toString();
                        str_message = message.getText().toString();
                        int int_nrpeople = Integer.parseInt(str_nrpeople);
                        boolean isTableAvailable = new Boolean(false);
                        boolean isTableReserved = new Boolean(false);

                        //first for loop over the tables
                        for(int i = 0; i < restaurantTables.size(); i++) {
                            isTableReserved = false;
                            if (restaurantTables.get(i) >= int_nrpeople) {
                                //for each table, whose seats can accommodate the nr of people, check if a reservation has that table
                                for(Reservation r : ReservationsToCheck) {
                                    if(r.getDate().equals(preferredDate) && r.getTime().equals(preferredTime) && r.getTable().equals(Integer.toString(i))) {
                                        //&& r.getTime() == preferredTime && r.getTable() == Integer.toString(i)
                                        System.out.println("second if is entered, table" + i + "is reserved at" + r.getDate());
                                        isTableReserved = true;
                                        break;
                                    }
                                }
                                if(isTableReserved) {
                                    isTableAvailable = false;
                                } else {
                                    isTableAvailable = true;
                                    ReservationToAdd.put("table", Integer.toString(i));
                                    break;
                                }
                            }
                        }

                        if (isTableAvailable) {
                            //updating other elements of the reservation to be added
                            ReservationToAdd.put("message", str_message);
                            ReservationToAdd.put("restaurant", str_restaurantId);
                            ReservationToAdd.put("restName", str_RestaurantName);
                            ReservationToAdd.put("date", preferredDate);
                            ReservationToAdd.put("time", preferredTime);
                            ReservationToAdd.put("userID", str_userID);

                            //adding reservation to database
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("reservation")
                                    .add(ReservationToAdd)
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
                        } else {
                            Toast.makeText(getApplicationContext(),"No tables available!",Toast.LENGTH_LONG).show();
                        }
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

