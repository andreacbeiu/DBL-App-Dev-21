package com.example.ui_screens.restaurants;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ui_screens.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    ActivityResultLauncher<String> mGetContent, mGetPdf;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit);

        mAuth = FirebaseAuth.getInstance();

        id = getIntent().getStringExtra("resId");

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + id + ".jpg");
        final StorageReference pdfRef = FirebaseStorage.getInstance().getReference().child("menus/" + id + ".pdf");
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
        mGetPdf = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            System.out.println(uri.getPath());
                            InputStream stream = getContentResolver().openInputStream(uri);

                            pdfRef.putStream(stream)
                                    .addOnFailureListener(unused -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of menu was unsuccesful", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(RestaurantEditActivity.this, "Uploading of menu was succesful", Toast.LENGTH_SHORT).show();
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

    public void viewMenu(View view){
        StorageReference menuRef = FirebaseStorage.getInstance().getReference().child("menus/" + id + ".pdf");
        menuRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        }).addOnFailureListener(unused -> {
            Toast.makeText(this, "No menu available", Toast.LENGTH_SHORT).show();
        });
    }

    //Top bar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //Handles actions in the topbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.account:
                startActivity(new Intent(this, RestaurantAccountActivity.class));
                return true;
            case R.id.LogOut:
                mAuth.getInstance().signOut();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void chooseImage(View view){
        mGetContent.launch("image/*");
    }

    public void chooseMenu(View view) { mGetPdf.launch("application/pdf"); }

    //close activity upon leaving through back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
