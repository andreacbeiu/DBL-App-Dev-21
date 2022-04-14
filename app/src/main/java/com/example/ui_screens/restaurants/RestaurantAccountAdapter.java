package com.example.ui_screens.restaurants;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_screens.R;
import com.example.ui_screens.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantAccountAdapter extends RecyclerView.Adapter<RestaurantAccountAdapter.MyViewHolder> {

    private List<User> listUsers = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, email;

        public MyViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameaccount);
            email = (TextView) itemView.findViewById(R.id.emailaccount);
        }
    }

    public RestaurantAccountAdapter(FirebaseFirestore db) {
        CollectionReference users = db.collection("restaurant_users");
        Query ref = users.whereIn("type", Arrays.asList("employee", "manager"));
        ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    this.listUsers = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email_id = document.getString("name");
                        Log.d("BookIT", "email: " + email_id);
                        User tempUser = new User("", email_id);
                        listUsers.add(tempUser);
                    }

                }
                else {
                    Log.d("BookIt", "Error getting documents: ", task.getException());
                }

        });
    }

    @NonNull
    @Override
    public RestaurantAccountAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_account_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAccountAdapter.MyViewHolder holder, int position) {
        holder.name.setText(listUsers.get(position).getName());
        holder.email.setText(listUsers.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }
}
