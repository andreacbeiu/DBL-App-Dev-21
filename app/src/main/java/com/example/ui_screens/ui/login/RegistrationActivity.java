package  com.example.ui_screens.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ui_screens.customers.CustomerLoginActivity;
import com.example.ui_screens.R;
import com.example.ui_screens.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Button button;
    private EditText editTextEmail, editTextPassword, editTextPhone;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        db = FirebaseFirestore.getInstance();


        editTextEmail = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextPhone = (EditText) findViewById(R.id.phone);


        progressBar = (ProgressBar) findViewById(R.id.loading);

        button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });


        mAuth = FirebaseAuth.getInstance();

    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.login) {
//            createUser();
//        }
//    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void createUser() {

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (email.isEmpty()) {
            editTextEmail.setError("Email Cannot Be Empty");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password Cannot Be Empty");
            editTextPassword.requestFocus();
            return;

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be longer than 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, String> user = new HashMap<String, String>();

        user.put("name", email);
        user.put("restaurant", "");
        user.put("type", "customer");





        CollectionReference users = db.collection("restaurant_users");

        Query ref = users.whereEqualTo("type", Arrays.asList("manager", "employee"));

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email_id = document.getString("name");
                        if (email_id.equals(email)) {
                            Toast.makeText(RegistrationActivity.this, "Email already registered with Restaraunt account type", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Log.d("BookIt", "Error getting documents: ", task.getException());
                }
            }
        });



        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        db.collection("restaurant_users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("BookIT", "DocumentSnapshot written with ID " + documentReference.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("BookIt", "Error adding document", e);
                            }
                        });
                        Toast.makeText(RegistrationActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, CustomerLoginActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }







}