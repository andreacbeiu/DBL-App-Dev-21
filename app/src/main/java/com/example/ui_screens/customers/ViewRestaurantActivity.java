package com.example.ui_screens.customers;

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
import com.example.ui_screens.restaurant_list.RestaurantListActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class ViewRestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    int mYear, mMonth, mDay, mHour, mMinute;
    String preferreddate, preferredtime, str_nrpeople, str_message;
    EditText nrpeople, message;
    TextView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        //selecting book button + adding click listener
        Button bookbutton = findViewById(R.id.bookbutton);
        bookbutton.setOnClickListener(this);
        preferreddate = "25-03-2022";
        preferredtime = "23:51";
        str_nrpeople = "0";
        str_message = "";
        map = (TextView) findViewById(R.id.textView16);


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

                        preferreddate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
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

                        preferredtime = hourOfDay + ":" + minute;
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

                        str_nrpeople = nrpeople.getText().toString();
                        str_message = message.getText().toString();

                        Toast.makeText(getApplicationContext(), "You have booked a table!", Toast.LENGTH_LONG).show();

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

    public void onRewardsClick(View view) {
        Intent intent = new Intent(this, RewardListActivity.class);
        startActivity(intent);
    }


}

