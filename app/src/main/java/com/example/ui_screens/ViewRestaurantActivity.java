package com.example.ui_screens;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewRestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

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
}