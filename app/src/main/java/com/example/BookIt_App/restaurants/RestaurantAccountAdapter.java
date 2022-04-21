package com.example.BookIt_App.restaurants;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookIt_App.R;
import com.example.BookIt_App.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantAccountAdapter extends RecyclerView.Adapter<RestaurantAccountAdapter.ViewHolder> {

    private List<User> listUsers = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private TextView name, email;
        ImageButton imageButton;
        private FirebaseFirestore db;
        private FirebaseAuth mAuth;
        private FirebaseUser user;


        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameaccount);
            email = (TextView) itemView.findViewById(R.id.emailaccount);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(this);
        }

        public TextView getNameTv() {
            return name;
        }

        public TextView getEmailTv() {
            return email;
        }

        @Override
        public void onClick(View view) {
            showPopUpMenu(view);
        }
        private void showPopUpMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.account_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            db = FirebaseFirestore.getInstance();

            switch (menuItem.getItemId()) {
                case R.id.edit:
                    CollectionReference users = db.collection("users");

                    Query ref = users.whereIn("type", Arrays.asList("employee"));

                    String email = user.getEmail();

                    ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            String email_check = "";
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String email_id = document.getString("name");

                                    if (email_id.equals(email)) {
                                        email_check = email_id;
                                        Log.d("BookIt", document.getId() + " => " + document.getData() + email_check);
                                    }
                                }

                                if (email.equals(email_check)) {
                                    Log.d("BookIt", "if statement reached");
//                                    Toast.makeText(RestaurantAccountManagement.class, "Cannot use this feature with employee account", Toast.LENGTH_SHORT).show();
                                } else {
                                }
                            }
                            else {
                                Log.d("BookIt", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                    return true;
                case R.id.delete:
                    List<User> tempList = new ArrayList<>();

                    mAuth = FirebaseAuth.getInstance();
                    user = mAuth.getCurrentUser();


                    CollectionReference userDb = db.collection("employees");
//                                   Query ref_1 = userDb.whereIn("resId", Arrays.asList(resId));

                    userDb.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {

                            for (QueryDocumentSnapshot document2 : task1.getResult()) {
                                String name = document2.getString("name");
                                String email_id = document2.getString("email");
                                String type = document2.getString("type");

                                User tempUser = new User(name, email_id, type);
                                tempList.add(tempUser);
                            }

                            User discUser = null;
                            discUser = tempList.get(getAdapterPosition());

                            db.collection("employees").document(discUser.getEmail())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("BookIt", "DocumentSnapshot successfully deleted!");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("BookIt", "Error deleting document", e);
                                        }
                                    });

                        }

                    });
                default:
                    return false;
            }
        }
    }

    public RestaurantAccountAdapter(FirebaseFirestore db) {
        List<User> tempList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        db.collection("users")
                .document(user.getUid().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
//                        String restaurantID = document.getData().get("resId").toString();
//                        System.out.println(restaurantID);
//                        String resId = restaurantID;

                        CollectionReference users = db.collection("employees");
//                        Query ref = users.whereIn("resId", Arrays.asList(resId));

                        users.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {

                                for (QueryDocumentSnapshot document2 : task1.getResult()) {
                                    String name = document2.getString("name");
                                    String email_id = document2.getString("email");
                                    String type = document2.getString("type");

                                    User tempUser = new User(name, email_id, type);
                                    tempList.add(tempUser);
                                }

                                this.listUsers = tempList;
                                this.notifyDataSetChanged();
                            }

                        });
                    }
                });



    }

    @NonNull
    @Override
    public RestaurantAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_account_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAccountAdapter.ViewHolder holder, int position) {
        holder.getNameTv().setText(listUsers.get(position).getName());
        holder.getEmailTv().setText(listUsers.get(position).getEmail());

    }

    @Override
    public int getItemCount() {
        Log.d("BookIT", "users: " + listUsers.size());
        return listUsers.size();
    }
}
