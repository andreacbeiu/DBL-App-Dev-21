package com.example.ui_screens.restaurants;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RestaurantEditActivity extends AppCompatActivity {

    private String id;
    private Map<String, Object> data;
    FirebaseFirestore db;
    EditText etName;
    EditText etDescription;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit);

        id = getIntent().getStringExtra("resId");

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + id + ".jpg");
        final ImageView restIV = (ImageView)findViewById(R.id.ivEditRestaurantImage);
        final long ONE_MEGABYTE = 1024*1024;

        ref.getBytes(ONE_MEGABYTE * 2).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            restIV.setImageBitmap(bmp);
        }).addOnFailureListener(unused -> {
            restIV.setImageDrawable(getDrawable(R.drawable.default_restaurant));
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            System.out.println(uri.getPath());
                            InputStream stream = getContentResolver().openInputStream(uri);

                            ref.putStream(stream)
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of image was unsuccesful", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnSuccessListener(unused -> {
                                        restIV.setImageURI(uri);
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of image was succesful", Toast.LENGTH_SHORT).show();
                                    });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

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

    public void chooseImage(View view){
        mGetContent.launch("image/*");
    }
}
