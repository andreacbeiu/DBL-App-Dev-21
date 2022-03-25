package com.example.ui_screens.restaurants;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RestaurantEditActivity extends AppCompatActivity {

    private String id = "4biUSxpvAawgmEUKuh2A";
    private Map<String, Object> data;
    FirebaseFirestore db;
    EditText etName;
    EditText etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit);

        etName = (EditText)findViewById(R.id.etEditInfoName);
        etDescription = (EditText)findViewById(R.id.etEditInfoDescription);

        db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        data = (HashMap<String, Object>)task.getResult().getData();
                        etName.setText(data.get("name").toString());
                        etDescription.setText(data.get("description").toString());
                    }
                });
    }

    public void save(View view){
        data.put("name", etName.getText().toString());
        data.put("description", etDescription.getText().toString());
        db.collection("restaurants")
                .document(id)
                .set(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Saving was succesful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(unused -> {
                    Toast.makeText(this, "ERROR: data was not saved", Toast.LENGTH_SHORT).show();
                });
    }
}
