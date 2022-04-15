package com.example.ui_screens.restaurants;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
import com.example.ui_screens.customers.CustomerLoginActivity;
import com.example.ui_screens.restaurant_list.RestaurantListAdapter;
import com.example.ui_screens.ui.login.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantAddAccount extends AppCompatActivity {

    private EditText editTextEmail, editTextPass, editTextName;
    private Button button;

    private FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db, db2;
    String restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        editTextEmail = (EditText) findViewById(R.id.emailacc);
        editTextPass = (EditText) findViewById(R.id.passacc);
        editTextName = (EditText) findViewById(R.id.nameacc);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


    }

    public void accountAdd(View view) {

        String email = editTextEmail.getText().toString();
        String password = editTextPass.getText().toString();
        String name = editTextName.getText().toString();


        Map<String, Object> user_acc = new HashMap<>();
        user_acc.put("name", name);
        user_acc.put("password", password);
        user_acc.put("email", email);

        db.collection("users")
                .document(user.getUid().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               restaurantID = task.getResult().getString("resId");
                                               user_acc.put("resId", restaurantID);
                                               db.collection("employees").document(email).set(user_acc).addOnCompleteListener(task1 -> {
                                                   if (task1.isSuccessful()) {
                                                       mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                                               if (task.isSuccessful()) {
                                                                   Map<String, Object> user = new HashMap<>();
                                                                   user.put("name", name);
                                                                   user.put("resId", restaurantID);
                                                                   user.put("email", email);
                                                                   user.put("phone", "");
                                                                   user.put("address", "");
                                                                   user.put("type", "employee");
                                                                   db.collection("users")
                                                                           .document(task.getResult().getUser().getUid())
                                                                           .set(user)
                                                                           .addOnSuccessListener(userDocRef -> {
                                                                               Toast.makeText(RestaurantAddAccount.this, "Successfully created account", Toast.LENGTH_SHORT).show();
                                                                               Intent i = new Intent(RestaurantAddAccount.this, RestaurantMainActivity.class);
                                                                               i.putExtra("resId", restaurantID);
                                                                               startActivity(i);
                                                                           });
                                                                   startActivity(new Intent(RestaurantAddAccount.this, RestaurantMainActivity.class));
                                                               } else {
                                                                   Toast.makeText(RestaurantAddAccount.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                       });
                                                       Toast.makeText(RestaurantAddAccount.this, "Successfully created employee account", Toast.LENGTH_SHORT).show();
                                                   }
                                                   else {
                                                       Log.d("BookIt", "task failed" + task1.getException());
                                                   }
                                               });

                                           }
                                       });
        Log.d("BookIt", "user_acc"  + user_acc);




    }

}
